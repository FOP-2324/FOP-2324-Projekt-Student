package projekt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import projekt.Config;
import projekt.controller.actions.IllegalActionException;
import projekt.model.*;
import projekt.model.buildings.Settlement;
import projekt.util.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class PlayerControllerTest {

    private final HexGridMock hexGrid = new HexGridMock(new HexGridImpl(Config.GRID_RADIUS));
    private final List<Player> players = IntStream.range(0, Config.MAX_PLAYERS)
        .mapToObj(i -> new PlayerMock(new PlayerImpl.Builder(i).build(hexGrid)))
        .collect(Collectors.toList());
    private GameController gameController;

    @BeforeEach
    public void setup() {
        GameState gameState = new GameState(hexGrid, players);
        gameController = new GameController(gameState);
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/acceptTradeOffer.json")
    @SuppressWarnings("unchecked")
    public void testAcceptTradeOffer(JsonParameterSet jsonParams) throws ReflectiveOperationException {
        Field tradingPlayerField = PlayerController.class.getDeclaredField("tradingPlayer");
        Field playerTradingOfferField = PlayerController.class.getDeclaredField("playerTradingOffer");
        Field playerTradingRequestField = PlayerController.class.getDeclaredField("playerTradingRequest");
        tradingPlayerField.trySetAccessible();
        playerTradingOfferField.trySetAccessible();
        playerTradingRequestField.trySetAccessible();

        PlayerMock player = (PlayerMock) players.get(0);
        PlayerMock tradingPlayer = jsonParams.getBoolean("tradingPlayerSet") ? (PlayerMock) players.get(1) : null;
        Map<ResourceType, Integer> playerTradingOffer = Utils.deserializeEnumMap(jsonParams.get("playerTradingOffer"), ResourceType.class, Utils.AS_INTEGER);
        Map<ResourceType, Integer> playerTradingRequest = Utils.deserializeEnumMap(jsonParams.get("playerTradingRequest"), ResourceType.class, Utils.AS_INTEGER);

        Map<ResourceType, Integer> playerInventory = new HashMap<>(playerTradingRequest != null ? playerTradingRequest : Collections.emptyMap());
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "addResource" -> {
                playerInventory.merge((ResourceType) params[1], (Integer) params[2], Integer::sum);
                yield null;
            }
            case "addResources" -> {
                ((Map<ResourceType, Integer>) params[1]).forEach(((Player) params[0])::addResource);
                yield null;
            }
            case "hasResources" -> jsonParams.getBoolean("enablePlayerInventory") && playerTradingRequest.equals(params[1]);
            case "removeResource" -> {
                playerInventory.merge((ResourceType) params[1], -((Integer) params[2]), Integer::sum);
                yield true;
            }
            case "removeResources" -> ((Map<ResourceType, Integer>) params[1]).entrySet()
                .stream()
                .map(entry -> ((Player) params[0]).removeResource(entry.getKey(), entry.getValue()))
                .reduce((a, b) -> a && b);
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction")::contains),
            (methodName, params) -> {
                if (params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                    ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                }
                return null;
            });
        boolean accepted = jsonParams.getBoolean("accepted");

        playerTradingOfferField.set(playerController, playerTradingOffer);
        playerTradingRequestField.set(playerController, playerTradingRequest);
        Map<ResourceType, Integer> tradingPlayerInventory = new HashMap<>(playerTradingOffer != null ? playerTradingOffer : Collections.emptyMap());
        if (tradingPlayer != null) {
            tradingPlayer.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources");
            tradingPlayer.setMethodAction((methodName, params) -> switch (methodName) {
                case "addResource" -> {
                    ResourceType key = (ResourceType) params[1];
                    Integer value = (Integer) params[2];
                    tradingPlayerInventory.merge(key, value, Integer::sum);
                    yield null;
                }
                case "addResources" -> {
                    ((Map<ResourceType, Integer>) params[1]).forEach(((Player) params[0])::addResource);
                    yield null;
                }
                case "hasResources" -> jsonParams.getBoolean("enablePartnerInventory") && playerTradingOffer.equals(params[1]);
                case "removeResource" -> {
                    ResourceType key = (ResourceType) params[1];
                    Integer value = (Integer) params[2];
                    tradingPlayerInventory.merge(key, -value, Integer::sum);
                    yield true;
                }
                case "removeResources" -> ((Map<ResourceType, Integer>) params[1]).entrySet()
                    .stream()
                    .map(entry -> ((Player) params[0]).removeResource(entry.getKey(), entry.getValue()))
                    .reduce((a, b) -> a && b);
                default -> null;
            });
        }
        tradingPlayerField.set(playerController, tradingPlayer);

        Context context = contextBuilder()
            .add("player", player)
            .add("tradingPlayer", tradingPlayer)
            .add("playerTradingOffer", playerTradingOffer)
            .add("playerTradingRequest", playerTradingRequest)
            .add("accepted", accepted)
            .build();
        if (jsonParams.getBoolean("exception")) {
            Exception e = assertThrows(IllegalActionException.class, () -> playerController.acceptTradeOffer(accepted), context, result ->
                "Expected PlayerController.acceptTradeOffer to throw an IllegalActionException");
            if (jsonParams.getString("exceptionMessage") != null) {
                assertEquals(jsonParams.getString("exceptionMessage"), e.getMessage(), context, result ->
                    "The message of the exception thrown by PlayerController.acceptTradeOffer is not correct");
            }
        } else {
            call(() -> playerController.acceptTradeOffer(accepted), context, result ->
                "PlayerController.acceptTradeOffer threw an uncaught exception");

            if (accepted) {
                Map<ResourceType, Integer> originalOffer = Utils.deserializeEnumMap(jsonParams.get("playerTradingOffer"), ResourceType.class, Utils.AS_INTEGER);
                Map<ResourceType, Integer> originalRequest = Utils.deserializeEnumMap(jsonParams.get("playerTradingRequest"), ResourceType.class, Utils.AS_INTEGER);

                assertEquals(originalOffer,
                    playerInventory.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 0)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                    context,
                    result -> "Current player's inventory does not equal the expected state");
                assertEquals(originalRequest,
                    tradingPlayerInventory.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 0)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                    context,
                    result -> "Trading partner's inventory does not equal the expected state");
            }

            assertEquals(PlayerObjective.IDLE, playerController.getPlayerObjectiveProperty().getValue(), context, result ->
                "PlayerController.acceptTradeOffer did not set the player objective to IDLE");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/tradeWithBank.json")
    @SuppressWarnings("unchecked")
    public void testTradeWithBank(JsonParameterSet jsonParams) {
        ResourceType offerType = jsonParams.get("offerType", ResourceType.class);
        int offerAmount = jsonParams.getInt("offerAmount");
        ResourceType request = jsonParams.get("request", ResourceType.class);
        int tradeRatio = jsonParams.getInt("tradeRatio");

        PlayerMock player = (PlayerMock) players.get(0);
        Map<ResourceType, Integer> playerInventory = new HashMap<>(Map.of(offerType, offerAmount));
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources", "getTradeRatio");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "addResource" -> {
                playerInventory.merge((ResourceType) params[1], (Integer) params[2], Integer::sum);
                yield null;
            }
            case "addResources" -> {
                ((Map<ResourceType, Integer>) params[1]).forEach(((Player) params[0])::addResource);
                yield null;
            }
            case "hasResources" -> jsonParams.getBoolean("enablePlayerInventory") && Map.of(offerType, offerAmount).equals(params[1]);
            case "removeResource" -> {
                if (!jsonParams.getBoolean("enablePlayerInventory") || playerInventory.getOrDefault(params[1], -1) < (Integer) params[2]) {
                    yield false;
                }
                playerInventory.merge((ResourceType) params[1], -((Integer) params[2]), Integer::sum);
                yield true;
            }
            case "removeResources" -> {
                if (((Map<ResourceType, Integer>) params[1]).entrySet()
                    .stream()
                    .allMatch(entry -> playerInventory.containsKey(entry.getKey()) &&
                                       playerInventory.getOrDefault(entry.getKey(), -1) >= entry.getValue())) {
                    yield false;
                }
                ((Map<ResourceType, Integer>) params[1]).forEach((key, value) -> ((Player) params[0]).removeResource(key, value));
                yield true;
            }
            case "getTradeRatio" -> tradeRatio;
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction")::contains),
            (methodName, params) -> {
                if (params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                    ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                }
                return null;
            });

        Context context = contextBuilder()
            .add("player", player)
            .add("offerType", offerType)
            .add("offerAmount", offerAmount)
            .add("request", request)
            .add("trade ratio", tradeRatio)
            .build();
        if (jsonParams.getBoolean("exception")) {
            assertThrows(IllegalActionException.class, () -> playerController.tradeWithBank(offerType, offerAmount, request), context, result ->
                "Expected PlayerController.tradeWithBank to throw an IllegalActionException");
        } else {
            call(() -> playerController.tradeWithBank(offerType, offerAmount, request), context, result ->
                "PlayerController.tradeWithBank threw an uncaught exception");
            assertEquals(Map.of(offerType, 0, request, 1),
                playerInventory,
                context,
                result -> "Player's inventory is not in the expected state after invoking PlayerController.tradeWithBank");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/canBuildVillage.json")
    public void testCanBuildVillage(JsonParameterSet jsonParams) {
        PlayerMock player = (PlayerMock) players.get(0);
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources", "getRemainingVillages");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "hasResources" -> jsonParams.getBoolean("hasResources") && Config.SETTLEMENT_BUILDING_COST.get(Settlement.Type.VILLAGE).equals(params[1]);
            case "getRemainingVillages" -> jsonParams.getInt("remainingVillages");
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction")::contains),
            (methodName, params) -> {
                if (methodName.equals("waitForNextAction") && params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                    ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                }
                return null;
            });

        boolean objectiveSet = jsonParams.getBoolean("objectiveSet");
        Context context = contextBuilder()
            .add("player", player)
            .add("playerController", playerController)
            .add("objective set to PLACE_VILLAGE", objectiveSet)
            .add("remaining villages", jsonParams.getInt("remainingVillages"))
            .build();
        if (objectiveSet) {
            playerController.setPlayerObjective(PlayerObjective.PLACE_VILLAGE);
        }
        assertCallEquals(jsonParams.getBoolean("expected"), playerController::canBuildVillage, context, result ->
            "PlayerController.canBuildVillage did not return the expected value");
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/canBuildRoad.json")
    public void testCanBuildRoad(JsonParameterSet jsonParams) {
        PlayerMock player = (PlayerMock) players.get(0);
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources", "getRemainingRoads");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "hasResources" -> jsonParams.getBoolean("hasResources") && Config.ROAD_BUILDING_COST.equals(params[1]);
            case "getRemainingRoads" -> jsonParams.getInt("remainingRoads");
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction")::contains),
            (methodName, params) -> {
                if (methodName.equals("waitForNextAction") && params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                    ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                }
                return null;
            });

        boolean objectiveSet = jsonParams.getBoolean("objectiveSet");
        Context context = contextBuilder()
            .add("player", player)
            .add("playerController", playerController)
            .add("objective set to PLACE_ROAD", objectiveSet)
            .add("remaining roads", jsonParams.getInt("remainingRoads"))
            .build();
        if (objectiveSet) {
            playerController.setPlayerObjective(PlayerObjective.PLACE_ROAD);
        }
        assertCallEquals(jsonParams.getBoolean("expected"), playerController::canBuildRoad, context, result ->
            "PlayerController.canBuildRoad did not return the expected value");
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/buildVillage.json")
    public void testBuildVillage(JsonParameterSet jsonParams) {
        boolean firstRound = jsonParams.getBoolean("firstRound");
        AtomicBoolean calledRemoveResources = new AtomicBoolean();
        PlayerMock player = (PlayerMock) players.get(0);
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "hasResources" -> jsonParams.getBoolean("canBuildVillage") && Config.SETTLEMENT_BUILDING_COST.get(Settlement.Type.VILLAGE).equals(params[1]);
            case "removeResource", "removeResources" -> {
                calledRemoveResources.set(true);
                yield true;
            }
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction", "canBuildVillage")::contains),
            (methodName, params) -> switch (methodName) {
                case "waitForNextAction" -> {
                    if (params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                        ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                    }
                    yield null;
                }
                case "canBuildVillage" -> jsonParams.getBoolean("canBuildVillage");
                default -> null;
            });
        AtomicBoolean calledPlaceVillage = new AtomicBoolean();
        IntersectionMock intersection = new IntersectionMock(new IntersectionImpl(
            new TilePosition(0, 0), new TilePosition(0, 1), new TilePosition(1, 0), hexGrid),
            Predicate.not(List.of("placeVillage")::contains),
            (methodName, params) -> switch (methodName) {
                case "placeVillage" -> {
                    calledPlaceVillage.set(true);
                    yield !jsonParams.getBoolean("exception");
                }
                default -> null;
            });

        gameController.getRoundCounterProperty().set(firstRound ? 0 : 1);
        playerController.setPlayerObjective(firstRound ? PlayerObjective.PLACE_VILLAGE : PlayerObjective.IDLE);

        Context context = contextBuilder()
            .add("player", player)
            .add("playerController", playerController)
            .add("first round", firstRound)
            .add("canBuildVillage", jsonParams.getBoolean("canBuildVillage"))
            .build();
        if (jsonParams.getBoolean("exception")) {
            assertThrows(IllegalActionException.class, () -> playerController.buildVillage(intersection), context, result ->
                "Expected PlayerController.buildVillage to throw an IllegalActionException");
            assertFalse(calledPlaceVillage.get(), context, result ->
                "PlayerController.buildVillage called Intersection.placeVillage on the given intersection");
            assertFalse(calledRemoveResources.get(), context, result ->
                "PlayerController.buildVillage called Player.removeResource(s) on the current player");
        } else {
            call(() -> playerController.buildVillage(intersection), context, result ->
                "PlayerController.buildVillage threw an uncaught exception");
            assertTrue(calledPlaceVillage.get(), context, result ->
                "PlayerController.buildVillage did not call Intersection.placeVillage on the given intersection");
            assertTrue(firstRound || calledRemoveResources.get(), context, result ->
                "PlayerController.buildVillage did not call Player.removeResource(s) on the current player");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/buildRoad.json")
    public void testBuildRoad(JsonParameterSet jsonParams) {
        boolean firstRound = jsonParams.getBoolean("firstRound");
        AtomicBoolean calledRemoveResources = new AtomicBoolean();
        PlayerMock player = (PlayerMock) players.get(0);
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "hasResources" -> jsonParams.getBoolean("canBuildRoad") && Config.ROAD_BUILDING_COST.equals(params[1]);
            case "removeResource", "removeResources" -> {
                calledRemoveResources.set(true);
                yield true;
            }
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction", "canBuildRoad")::contains),
            (methodName, params) -> switch (methodName) {
                case "waitForNextAction" -> {
                    if (params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                        ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                    }
                    yield null;
                }
                case "canBuildRoad" -> jsonParams.getBoolean("canBuildRoad");
                default -> null;
            });
        AtomicBoolean calledAddRoad = new AtomicBoolean();
        TilePosition tilePosition1 = new TilePosition(0, 0);
        TilePosition tilePosition2 = new TilePosition(0, 1);

        hexGrid.setUseDelegate("addRoad");
        hexGrid.setMethodAction((methodName, params) -> switch (methodName) {
            case "addRoad" -> {
                calledAddRoad.set(true);
                yield !jsonParams.getBoolean("exception");
            }
            default -> null;
        });

        gameController.getRoundCounterProperty().set(firstRound ? 0 : 1);
        playerController.setPlayerObjective(firstRound ? PlayerObjective.PLACE_ROAD : PlayerObjective.IDLE);

        Context context = contextBuilder()
            .add("player", player)
            .add("playerController", playerController)
            .add("first round", firstRound)
            .add("canBuildRoad", jsonParams.getBoolean("canBuildRoad"))
            .build();
        if (jsonParams.getBoolean("exception")) {
            assertThrows(IllegalActionException.class, () -> playerController.buildRoad(tilePosition1, tilePosition2), context, result ->
                "Expected PlayerController.buildRoad to throw an IllegalActionException");
            assertFalse(calledAddRoad.get(), context, result ->
                "PlayerController.buildRoad called HexGrid.addRoad on the given intersection");
            assertFalse(calledRemoveResources.get(), context, result ->
                "PlayerController.buildRoad called Player.removeResource(s) on the current player");
        } else {
            call(() -> playerController.buildRoad(tilePosition1, tilePosition2), context, result ->
                "PlayerController.buildRoad threw an uncaught exception");
            assertTrue(calledAddRoad.get(), context, result ->
                "PlayerController.buildRoad did not call HexGrid.addRoad on the given intersection");
            assertTrue(firstRound || calledRemoveResources.get(), context, result ->
                "PlayerController.buildRoad did not call Player.removeResource(s) on the current player");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/PlayerController/upgradeVillage.json")
    public void testUpgradeVillage(JsonParameterSet jsonParams) {
        AtomicBoolean calledRemoveResources = new AtomicBoolean();
        PlayerMock player = (PlayerMock) players.get(0);
        player.setUseDelegate("addResource", "addResources", "hasResources", "removeResource", "removeResources");
        player.setMethodAction((methodName, params) -> switch (methodName) {
            case "hasResources" -> jsonParams.getBoolean("canUpgradeVillage") && Config.SETTLEMENT_BUILDING_COST.get(Settlement.Type.CITY).equals(params[1]);
            case "removeResource", "removeResources" -> {
                calledRemoveResources.set(true);
                yield true;
            }
            default -> null;
        });
        PlayerControllerMock playerController = new PlayerControllerMock(gameController, player,
            Predicate.not(List.of("blockingGetNextAction", "waitForNextAction", "canUpgradeVillage")::contains),
            (methodName, params) -> switch (methodName) {
                case "waitForNextAction" -> {
                    if (params.length == 1 + 1 && params[1] instanceof PlayerObjective playerObjective) {
                        ((PlayerController) params[0]).setPlayerObjective(playerObjective);
                    }
                    yield null;
                }
                case "canUpgradeVillage" -> jsonParams.getBoolean("canUpgradeVillage");
                default -> null;
            });
        AtomicBoolean calledUpgradeVillage = new AtomicBoolean();
        IntersectionMock intersection = new IntersectionMock(new IntersectionImpl(
            new TilePosition(0, 0), new TilePosition(0, 1), new TilePosition(1, 0), hexGrid),
            Predicate.not(List.of("upgradeSettlement")::contains),
            (methodName, params) -> switch (methodName) {
                case "upgradeSettlement" -> {
                    calledUpgradeVillage.set(true);
                    yield !jsonParams.getBoolean("exception");
                }
                default -> null;
            });

        Context context = contextBuilder()
            .add("player", player)
            .add("playerController", playerController)
            .add("canUpgradeVillage", jsonParams.getBoolean("canUpgradeVillage"))
            .build();
        if (jsonParams.getBoolean("exception")) {
            assertThrows(IllegalActionException.class, () -> playerController.upgradeVillage(intersection), context, result ->
                "Expected PlayerController.upgradeVillage to throw an IllegalActionException");
            assertFalse(!jsonParams.getBoolean("canUpgradeVillage") && calledUpgradeVillage.get(), context, result ->
                "PlayerController.upgradeVillage called Intersection.upgradeSettlement on the given intersection");
            assertFalse(calledRemoveResources.get(), context, result ->
                "PlayerController.upgradeVillage called Player.removeResource(s) on the current player");
        } else {
            call(() -> playerController.upgradeVillage(intersection), context, result ->
                "PlayerController.upgradeVillage threw an uncaught exception");
            assertTrue(calledUpgradeVillage.get(), context, result ->
                "PlayerController.upgradeVillage did not call Intersection.upgradeSettlement on the given intersection");
        }
    }
}
