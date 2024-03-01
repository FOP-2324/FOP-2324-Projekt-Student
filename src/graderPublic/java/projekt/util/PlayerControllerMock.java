package projekt.util;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import projekt.controller.GameController;
import projekt.controller.PlayerController;
import projekt.controller.PlayerObjective;
import projekt.controller.actions.IllegalActionException;
import projekt.controller.actions.PlayerAction;
import projekt.model.*;
import projekt.model.tiles.Tile;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class PlayerControllerMock extends PlayerController implements MockClass<PlayerController> {

    private Predicate<String> useDelegate;
    private BiFunction<String, Object[], ?> methodAction;

    public PlayerControllerMock(GameController gameController, Player player) {
        this(gameController, player, s -> true, (methodName, params) -> null);
    }

    public PlayerControllerMock(GameController gameController,
                                Player player,
                                Predicate<String> useDelegate,
                                BiFunction<String, Object[], ?> methodAction) {
        super(gameController, player);
        this.useDelegate = useDelegate;
        this.methodAction = methodAction;
        try {
            Field playerObjectivePropertyField = PlayerController.class.getDeclaredField("playerObjectiveProperty");
            playerObjectivePropertyField.trySetAccessible();
            playerObjectivePropertyField.set(this, new SimpleObjectProperty<>(PlayerObjective.IDLE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlayerController getDelegate() {
        return this;
    }

    @Override
    public Predicate<String> getUseDelegate() {
        return useDelegate;
    }

    @Override
    public void setUseDelegate(Predicate<String> useDelegate) {
        this.useDelegate = useDelegate;
    }

    @Override
    public BiFunction<String, Object[], ?> getMethodAction() {
        return methodAction;
    }

    @Override
    public void setMethodAction(BiFunction<String, Object[], ?> methodAction) {
        this.methodAction = methodAction;
    }

    @Override
    public Player getPlayer()  {
        if (useDelegate.test("getPlayer")) {
            return super.getPlayer();
        } else {
            return (Player) methodAction.apply("getPlayer", new Object[] {this});
        }
    }

    @Override
    public Property<PlayerState> getPlayerStateProperty()  {
        if (useDelegate.test("getPlayerStateProperty")) {
            return super.getPlayerStateProperty();
        } else {
            return (Property<PlayerState>) methodAction.apply("getPlayerStateProperty", new Object[] {this});
        }
    }

    @Override
    public PlayerState getPlayerState()  {
        if (useDelegate.test("getPlayerState")) {
            return super.getPlayerState();
        } else {
            return (PlayerState) methodAction.apply("getPlayerState", new Object[] {this});
        }
    }

    @Override
    public Property<PlayerObjective> getPlayerObjectiveProperty()  {
        if (useDelegate.test("getPlayerObjectiveProperty")) {
            return super.getPlayerObjectiveProperty();
        } else {
            return (Property<PlayerObjective>) methodAction.apply("getPlayerObjectiveProperty", new Object[] {this});
        }
    }

    @Override
    public void setPlayerObjective(PlayerObjective nextObjective)  {
        if (useDelegate.test("setPlayerObjective")) {
            super.setPlayerObjective(nextObjective);
        } else {
            methodAction.apply("setPlayerObjective", new Object[] {this, nextObjective});
        }
    }

    @Override
    public void setCardsToSelect(int cardsToSelect)  {
        if (useDelegate.test("setCardsToSelect")) {
            super.setCardsToSelect(cardsToSelect);
        } else {
            methodAction.apply("setCardsToSelect", new Object[] {this, cardsToSelect});
        }
    }

    @Override
    public List<Player> getOtherPlayers()  {
        if (useDelegate.test("getOtherPlayers")) {
            return super.getOtherPlayers();
        } else {
            return (List<Player>) methodAction.apply("getOtherPlayers", new Object[] {this});
        }
    }

    @Override
    public void rollDice()  {
        if (useDelegate.test("rollDice")) {
            super.rollDice();
        } else {
            methodAction.apply("rollDice", new Object[] {this});
        }
    }

    @Override
    public void processSelectedResources(Map<ResourceType, Integer> selectedResources) throws IllegalActionException  {
        if (useDelegate.test("processSelectedResources")) {
            super.processSelectedResources(selectedResources);
        } else {
            methodAction.apply("processSelectedResources", new Object[] {this, selectedResources});
        }
    }

    @Override
    public void triggerAction(PlayerAction action)  {
        if (useDelegate.test("triggerAction")) {
            super.triggerAction(action);
        } else {
            methodAction.apply("triggerAction", new Object[] {this, action});
        }
    }

    @Override
    public PlayerAction blockingGetNextAction() throws InterruptedException  {
        if (useDelegate.test("blockingGetNextAction")) {
            return super.blockingGetNextAction();
        } else {
            return (PlayerAction) methodAction.apply("blockingGetNextAction", new Object[] {this});
        }
    }

    @Override
    public PlayerAction waitForNextAction(PlayerObjective nextObjective)  {
        if (useDelegate.test("waitForNextAction")) {
            return super.waitForNextAction(nextObjective);
        } else {
            return (PlayerAction) methodAction.apply("waitForNextAction", new Object[] {this, nextObjective});
        }
    }

    @Override
    public PlayerAction waitForNextAction()  {
        if (useDelegate.test("waitForNextAction")) {
            return super.waitForNextAction();
        } else {
            return (PlayerAction) methodAction.apply("waitForNextAction", new Object[] {this});
        }
    }

    @Override
    public boolean canBuildVillage()  {
        if (useDelegate.test("canBuildVillage")) {
            return super.canBuildVillage();
        } else {
            return (boolean) methodAction.apply("canBuildVillage", new Object[] {this});
        }
    }

    @Override
    public void buildVillage(Intersection intersection) throws IllegalActionException  {
        if (useDelegate.test("buildVillage")) {
            super.buildVillage(intersection);
        } else {
            methodAction.apply("buildVillage", new Object[] {this, intersection});
        }
    }

    @Override
    public boolean canUpgradeVillage()  {
        if (useDelegate.test("canUpgradeVillage")) {
            return super.canUpgradeVillage();
        } else {
            return (boolean) methodAction.apply("canUpgradeVillage", new Object[] {this});
        }
    }

    @Override
    public void upgradeVillage(Intersection intersection) throws IllegalActionException  {
        if (useDelegate.test("upgradeVillage")) {
            super.upgradeVillage(intersection);
        } else {
            methodAction.apply("upgradeVillage", new Object[] {this, intersection});
        }
    }

    @Override
    public boolean canBuildRoad()  {
        if (useDelegate.test("canBuildRoad")) {
            return super.canBuildRoad();
        } else {
            return (boolean) methodAction.apply("canBuildRoad", new Object[] {this});
        }
    }

    @Override
    public void buildRoad(Tile tile, TilePosition.EdgeDirection edgeDirection) throws IllegalActionException  {
        if (useDelegate.test("buildRoad")) {
            super.buildRoad(tile, edgeDirection);
        } else {
            methodAction.apply("buildRoad", new Object[] {this, tile, edgeDirection});
        }
    }

    @Override
    public void buildRoad(TilePosition position0, TilePosition position1) throws IllegalActionException  {
        if (useDelegate.test("buildRoad")) {
            super.buildRoad(position0, position1);
        } else {
            methodAction.apply("buildRoad", new Object[] {this, position0, position1});
        }
    }

    @Override
    public boolean canBuyDevelopmentCard()  {
        if (useDelegate.test("canBuyDevelopmentCard")) {
            return super.canBuyDevelopmentCard();
        } else {
            return (boolean) methodAction.apply("canBuyDevelopmentCard", new Object[] {this});
        }
    }

    @Override
    public void buyDevelopmentCard() throws IllegalActionException  {
        if (useDelegate.test("buyDevelopmentCard")) {
            super.buyDevelopmentCard();
        } else {
            methodAction.apply("buyDevelopmentCard", new Object[] {this});
        }
    }

    @Override
    public void playDevelopmentCard(DevelopmentCardType developmentCard) throws IllegalActionException  {
        if (useDelegate.test("playDevelopmentCard")) {
            super.playDevelopmentCard(developmentCard);
        } else {
            methodAction.apply("playDevelopmentCard", new Object[] {this, developmentCard});
        }
    }

    @Override
    public void tradeWithBank(ResourceType offerType, int offerAmount, ResourceType request) throws IllegalActionException  {
        if (useDelegate.test("tradeWithBank")) {
            super.tradeWithBank(offerType, offerAmount, request);
        } else {
            methodAction.apply("tradeWithBank", new Object[] {this, offerType, offerAmount, request});
        }
    }

    @Override
    public void offerTrade(Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request)  {
        if (useDelegate.test("offerTrade")) {
            super.offerTrade(offer, request);
        } else {
            methodAction.apply("offerTrade", new Object[] {this, offer, request});
        }
    }

    @Override
    public boolean canAcceptTradeOffer(Player otherPlayer, Map<ResourceType, Integer> request)  {
        if (useDelegate.test("canAcceptTradeOffer")) {
            return super.canAcceptTradeOffer(otherPlayer, request);
        } else {
            return (boolean) methodAction.apply("canAcceptTradeOffer", new Object[] {this, otherPlayer, request});
        }
    }

    @Override
    protected void setPlayerTradeOffer(Player player, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request)  {
        if (useDelegate.test("setPlayerTradeOffer")) {
            super.setPlayerTradeOffer(player, offer, request);
        } else {
            methodAction.apply("setPlayerTradeOffer", new Object[] {this, player, offer, request});
        }
    }

    @Override
    protected void resetPlayerTradeOffer()  {
        if (useDelegate.test("resetPlayerTradeOffer")) {
            super.resetPlayerTradeOffer();
        } else {
            methodAction.apply("resetPlayerTradeOffer", new Object[] {this});
        }
    }

    @Override
    public void acceptTradeOffer(boolean accepted) throws IllegalActionException  {
        if (useDelegate.test("acceptTradeOffer")) {
            super.acceptTradeOffer(accepted);
        } else {
            methodAction.apply("acceptTradeOffer", new Object[] {this, accepted});
        }
    }

    @Override
    public void dropSelectedResources(Map<ResourceType, Integer> resourcesToDrop)  {
        if (useDelegate.test("dropSelectedResources")) {
            super.dropSelectedResources(resourcesToDrop);
        } else {
            methodAction.apply("dropSelectedResources", new Object[] {this, resourcesToDrop});
        }
    }

    @Override
    public void selectPlayerAndResourceToSteal(Player playerToStealFrom, ResourceType resourceToSteal) throws IllegalActionException  {
        if (useDelegate.test("selectPlayerAndResourceToSteal")) {
            super.selectPlayerAndResourceToSteal(playerToStealFrom, resourceToSteal);
        } else {
            methodAction.apply("selectPlayerAndResourceToSteal", new Object[] {this, playerToStealFrom, resourceToSteal});
        }
    }

    @Override
    public void setRobberPosition(TilePosition position)  {
        if (useDelegate.test("setRobberPosition")) {
            super.setRobberPosition(position);
        } else {
            methodAction.apply("setRobberPosition", new Object[] {this, position});
        }
    }

    @Override
    public List<Player> getPlayersToStealFrom()  {
        if (useDelegate.test("getPlayersToStealFrom")) {
            return super.getPlayersToStealFrom();
        } else {
            return (List<Player>) methodAction.apply("getPlayersToStealFrom", new Object[] {this});
        }
    }
}
