package projekt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import projekt.Config;
import projekt.controller.actions.AcceptTradeAction;
import projekt.controller.actions.EndTurnAction;
import projekt.controller.actions.PlayerAction;
import projekt.model.*;
import projekt.model.buildings.Settlement;
import projekt.model.tiles.Tile;
import projekt.util.PlayerControllerMock;
import projekt.util.PlayerMock;
import projekt.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.controller.PlayerObjective.*;

@TestForSubmission
public class GameControllerTest {

    private final HexGrid hexGrid = new HexGridImpl(Config.GRID_RADIUS, () -> 6, () -> Tile.Type.WOODLAND);
    private final List<Player> players = IntStream.range(0, Config.MAX_PLAYERS)
        .mapToObj(i -> (Player) new PlayerMock(new PlayerImpl.Builder(i).build(hexGrid)))
        .toList();
    private final GameController gameController = new GameController(new GameState(hexGrid, players));
    private final AtomicReference<PlayerAction> playerAction = new AtomicReference<>(playerController -> {});
    private Map<Player, List<PlayerObjective>> playerObjectives;
    private Map<Player, PlayerController> playerControllers;
    private final Context baseContext = contextBuilder().add("players", players).build();

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        playerObjectives = players.stream()
            .collect(Collectors.toMap(Function.identity(), player -> new ArrayList<>()));
        playerControllers = players.stream()
            .collect(Collectors.toMap(Function.identity(), player -> new PlayerControllerMock(gameController, player,
                Predicate.not(List.of("blockingGetNextAction", "waitForNextAction")::contains),
                (methodName, params) -> switch (methodName) {
                    case "blockingGetNextAction", "waitForNextAction" -> {
                        if (methodName.equals("waitForNextAction") && params.length == 2 && params[1] instanceof PlayerObjective nextObjective) {
                            ((PlayerController) params[0]).setPlayerObjective(nextObjective);
                        }
                        yield playerAction.get();
                    }
                    default -> null;
                }) {{
                    getPlayerObjectiveProperty().addListener((observable, oldValue, newValue) -> playerObjectives.get(getPlayer()).add(newValue));
                }}));
        Field playerControllersField = GameController.class.getDeclaredField("playerControllers");
        playerControllersField.trySetAccessible();
        playerControllersField.set(gameController, playerControllers);
    }

    @Test
    public void testFirstRound() throws ReflectiveOperationException {
        Method firstRoundMethod = GameController.class.getDeclaredMethod("firstRound");
        firstRoundMethod.trySetAccessible();

        call(() -> firstRoundMethod.invoke(gameController), baseContext, result ->
            "An exception occurred while invoking GameController.firstRound");
        List<PlayerObjective> expected = List.of(PLACE_VILLAGE, PLACE_ROAD, PLACE_VILLAGE, PLACE_ROAD, IDLE);
        playerObjectives.forEach((player, objectives) -> {
            Context context = contextBuilder()
                .add(baseContext)
                .add("active player", player)
                .build();

            assertEquals(expected, objectives, context, result ->
                "Actual objectives do not match the expected ones");
        });
    }

    @Test
    public void testRegularTurn() throws ReflectiveOperationException, InterruptedException {
        Player activePlayer = players.get(0);
        PlayerControllerMock activePlayerController = (PlayerControllerMock) playerControllers.get(activePlayer);
        AtomicInteger counter = new AtomicInteger();
        activePlayerController.setMethodAction((methodName, params) -> switch (methodName) {
            case "blockingGetNextAction", "waitForNextAction" -> {
                if (methodName.equals("waitForNextAction") && params.length == 2 && params[1] instanceof PlayerObjective nextObjective) {
                    ((PlayerController) params[0]).setPlayerObjective(nextObjective);
                }
                if (counter.incrementAndGet() > 3) {
                    playerAction.set(new EndTurnAction());
                }
                yield playerAction.get();
            }
            default -> null;
        });
        gameController.getActivePlayerControllerProperty().setValue(activePlayerController);
        Method regularTurnMethod = GameController.class.getDeclaredMethod("regularTurn");
        regularTurnMethod.trySetAccessible();

        Context context = contextBuilder()
            .add(baseContext)
            .add("active player", activePlayer)
            .build();
        Thread thread = new Thread(() -> {
            try {
                regularTurnMethod.invoke(gameController);
            } catch (Throwable t) {
                fail(t, baseContext, result -> "An uncaught exception was thrown by GameController.regularTurn");
            }
        });
        thread.start();
        thread.join(3000);
        if (thread.isAlive()) {
            thread.stop();
            fail(baseContext, result -> "Timeout of 3 seconds exceeded in GameController.regularTurn");
        }

        assertEquals(List.of(REGULAR_TURN), playerObjectives.get(activePlayer), context, result ->
            "Actual objectives do not match the expected ones");
    }

    @Test
    public void testDiceRollSeven() throws ReflectiveOperationException {
        Method diceRollSevenMethod = GameController.class.getDeclaredMethod("diceRollSeven");
        diceRollSevenMethod.trySetAccessible();
        Field cardsToSelectField = PlayerController.class.getDeclaredField("cardsToSelect");
        cardsToSelectField.trySetAccessible();

        Player rollingPlayer = players.get(0);
        PlayerMock dropCardsPlayer = (PlayerMock) players.get(1);
        Field resourcesField = PlayerImpl.class.getDeclaredField("resources");
        resourcesField.trySetAccessible();
        resourcesField.set(dropCardsPlayer.getDelegate(), new HashMap<>() {{put(ResourceType.WOOD, 15);}});
        gameController.getActivePlayerControllerProperty().setValue(playerControllers.get(rollingPlayer));

        call(() -> diceRollSevenMethod.invoke(gameController), baseContext, result ->
            "An exception occurred while invoking GameController.diceRollSeven");

        int cardsToSelect = (int) cardsToSelectField.get(playerControllers.get(dropCardsPlayer));
        playerObjectives.forEach((player, objectives) -> {
            Context context = contextBuilder()
                .add(baseContext)
                .add("active player", player)
                .build();

            if (player == rollingPlayer) {
                assertEquals(List.of(SELECT_ROBBER_TILE, SELECT_CARD_TO_STEAL), objectives, context, result ->
                    "Actual objectives do not match the expected ones");
            } else if (player == dropCardsPlayer) {
                assertEquals(7, cardsToSelect, context, result ->
                    "The amount of cards to select differs from the expected amount");
                assertEquals(List.of(DROP_CARDS, IDLE), objectives, context, result ->
                    "Actual objectives do not match the expected ones");
            } else {
                assertEquals(Collections.emptyList(), objectives, context, result ->
                    "Actual objectives do not match the expected ones");
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDistributeResources() throws ReflectiveOperationException {
        Field intersectionsField = HexGridImpl.class.getDeclaredField("intersections");
        intersectionsField.trySetAccessible();
        PlayerMock activePlayer = (PlayerMock) players.get(0);
        List<TilePosition> tilePositions = List.of(new TilePosition(0, 0), new TilePosition(0, 1), new TilePosition(-1, 1));
        AtomicReference<Settlement> settlementRef = new AtomicReference<>();
        Intersection intersection = new IntersectionImpl(hexGrid, tilePositions) {
            @Override
            public boolean playerHasSettlement(Player player) {
                return player == activePlayer;
            }

            @Override
            public boolean hasSettlement() {
                return true;
            }

            @Override
            public Settlement getSettlement() {
                return settlementRef.get();
            }
        };
        Settlement settlement = new Settlement(activePlayer, Settlement.Type.VILLAGE, intersection);
        settlementRef.set(settlement);
        Map<ResourceType, Integer> resources = new HashMap<>();
        ((Map<Set<TilePosition>, Intersection>) intersectionsField.get(hexGrid)).put(Set.copyOf(tilePositions), intersection);
        activePlayer.setUseDelegate(Predicate.not(List.of("getSettlements", "addResource", "addResources")::contains));
        activePlayer.setMethodAction((methodName, params) -> switch (methodName) {
            case "getSettlements" -> Set.of(settlement);
            case "addResource" -> resources.put((ResourceType) params[1], (Integer) params[2]);
            case "addResources" -> {
                resources.putAll((Map<? extends ResourceType, ? extends Integer>) params[1]);
                yield null;
            }
            default -> null;
        });

        TilePosition robberPosition = new TilePosition(1, 2);
        int diceRoll = 6;
        Context context = contextBuilder()
            .add(baseContext)
            .add("settlements", Set.of(settlement))
            .add("robber position", robberPosition)
            .add("diceRoll", diceRoll)
            .build();

        hexGrid.setRobberPosition(robberPosition);
        call(() -> gameController.distributeResources(diceRoll), context, result ->
            "An exception occurred while invoking GameController.distributeResources");
        assertEquals(Map.of(ResourceType.WOOD, 1), resources, context, result ->
            "The added resources do not match the expected ones");
    }

    @ParameterizedTest
    @JsonParameterSetTest("/controller/GameController/offerTrade.json")
    public void testOfferTrade(JsonParameterSet jsonParams) {
        PlayerMock offeringPlayer = (PlayerMock) players.get(0);
        Integer acceptingPlayerIndex = jsonParams.get("acceptingPlayerIndex");
        PlayerMock acceptingPlayer = (PlayerMock) (acceptingPlayerIndex != null ? players.get(acceptingPlayerIndex) : null);
        Map<ResourceType, Integer> offer = Utils.deserializeEnumMap(jsonParams.get("offer"), ResourceType.class, Utils.AS_INTEGER);
        Map<ResourceType, Integer> request = Utils.deserializeEnumMap(jsonParams.get("request"), ResourceType.class, Utils.AS_INTEGER);
        Map<PlayerMock, Boolean> offeredTrade = new HashMap<>();
        Map<PlayerMock, Boolean> calledSetPlayerTradeOffer = new HashMap<>();
        Map<PlayerMock, Boolean> calledResetPlayerTradeOffer = new HashMap<>();
        Map<PlayerMock, PlayerControllerMock> playerControllers = this.playerControllers.entrySet()
            .stream()
            .map(entry -> Map.entry((PlayerMock) entry.getKey(), (PlayerControllerMock) entry.getValue()))
            .peek(entry -> {
                PlayerControllerMock playerControllerMock = entry.getValue();
                playerControllerMock.setUseDelegate(Predicate.not(List.of(
                    "blockingGetNextAction", "waitForNextAction", "canAcceptTradeOffer", "acceptTradeOffer",
                    "setPlayerTradeOffer", "resetPlayerTradeOffer")::contains));
                playerControllerMock.setMethodAction((methodName, params) -> switch (methodName) {
                    case "blockingGetNextAction", "waitForNextAction" -> {
                        PlayerControllerMock pcMock = (PlayerControllerMock) params[0];
                        PlayerMock player = (PlayerMock) pcMock.getPlayer();
                        if (methodName.equals("waitForNextAction") && params.length == 2 && params[1] instanceof PlayerObjective nextObjective) {
                            if (nextObjective == ACCEPT_TRADE) {
                                offeredTrade.put(player, true);
                            }
                            pcMock.setPlayerObjective(nextObjective);
                        }
                        yield player == offeringPlayer ? playerAction.get() : new AcceptTradeAction(player == acceptingPlayer);
                    }
                    case "canAcceptTradeOffer" -> ((PlayerControllerMock) params[0]).getPlayer() != offeringPlayer;
                    case "setPlayerTradeOffer", "resetPlayerTradeOffer" -> {
                        Map<PlayerMock, Boolean> map = methodName.equals("setPlayerTradeOffer") ? calledSetPlayerTradeOffer : calledResetPlayerTradeOffer;
                        map.put((PlayerMock) ((PlayerControllerMock) params[0]).getPlayer(), true);
                        yield null;
                    }
                    default -> null;
                });
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Context baseContext = contextBuilder()
            .add("offeringPlayer", offeringPlayer)
            .add("accepting Player", acceptingPlayer)
            .add("offer", offer)
            .add("request", request)
            .build();
        call(() -> gameController.offerTrade(offeringPlayer, offer, request), baseContext, result ->
            "GameController.offerTrade threw an uncaught exception");
        for (Map.Entry<PlayerMock, PlayerControllerMock> entry : playerControllers.entrySet()) {
            PlayerMock player = entry.getKey();
            PlayerControllerMock playerController = entry.getValue();
            Context context = contextBuilder()
                .add(baseContext)
                .add("current Player", player)
                .add("current PlayerController", playerController)
                .build();
            if (player == offeringPlayer) {
                assertEquals(List.of(), playerObjectives.get(player), context, result ->
                    "The objectives of the current player do not match the expected ones");
            } else {
                if (offeredTrade.getOrDefault(player, false)) {
                    assertEquals(List.of(ACCEPT_TRADE, IDLE), playerObjectives.get(player), context, result ->
                        "The objectives of the current player do not match the expected ones");
                    assertTrue(calledSetPlayerTradeOffer.get(player), context, result ->
                        "setPlayerTradeOffer was not invoked on current PlayerController");
                    assertTrue(calledResetPlayerTradeOffer.get(player), context, result ->
                        "resetPlayerTradeOffer was not invoked on current PlayerController");
                } else {
                    assertEquals(List.of(), playerObjectives.get(player), context, result ->
                        "The objectives of the current player do not match the expected ones");
                }
            }
        }
    }
}
