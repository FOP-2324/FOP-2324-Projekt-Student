package projekt.controller;

import javafx.beans.property.Property;
import projekt.Config;
import projekt.controller.actions.AcceptTradeAction;
import projekt.controller.actions.BuildRoadAction;
import projekt.controller.actions.BuildVillageAction;
import projekt.controller.actions.EndTurnAction;
import projekt.controller.actions.PlayerAction;
import projekt.controller.actions.RollDiceAction;
import projekt.controller.actions.SelectCardsAction;
import projekt.controller.actions.SelectRobberTileAction;
import projekt.controller.actions.StealCardAction;
import projekt.model.GameState;
import projekt.model.HexGrid;
import projekt.model.Player;
import projekt.model.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A basic AI controller that does not follow any specific strategy.
 * It will always execute actions in a given order if they are allowed by the
 * player's objective.
 * Implements all actions that are required to not stop the game flow.
 * All implemented actions are performed randomly.
 */
public class BasicAiController extends AiController {
    /**
     * Creates a new BasicAiController with the given player controller, hex grid,
     * game state and active player controller.
     *
     * @param playerController       the player controller this belongs to
     * @param hexGrid                the hex grid
     * @param gameState              the game state
     * @param activePlayerController the active player controller
     */
    public BasicAiController(
        final PlayerController playerController, final HexGrid hexGrid, final GameState gameState,
        final Property<PlayerController> activePlayerController
    ) {
        super(playerController, hexGrid, gameState, activePlayerController);
    }

    @Override
    protected void executeActionBasedOnObjective(final PlayerObjective objective) {
        final Set<Class<? extends PlayerAction>> actions = objective.getAllowedActions();

        if (actions.contains(RollDiceAction.class)) {
            playerController.triggerAction(new RollDiceAction());
        }
        if (actions.contains(BuildVillageAction.class)) {
            buildVillage();
        }
        if (actions.contains(BuildRoadAction.class)) {
            buildRoad();
        }
        if (actions.contains(SelectCardsAction.class)) {
            selectCards();
        }
        if (actions.contains(SelectRobberTileAction.class)) {
            selectRobberTileAction();
        }
        if (actions.contains(AcceptTradeAction.class)) {
            playerController.triggerAction(new AcceptTradeAction(Config.RANDOM.nextBoolean()));
        }
        if (actions.contains(StealCardAction.class)) {
            stealCardAction();
        }
        if (actions.contains(EndTurnAction.class)) {
            playerController.triggerAction(new EndTurnAction());
        }
    }

    /**
     * This method builds a village on a random buildable intersection.
     */
    private void buildVillage() {
        playerController.getPlayerState().buildableVillageIntersections().stream().findAny().ifPresent(intersection -> {
            playerController.triggerAction(new BuildVillageAction(intersection));
        });
    }

    /**
     * This method builds a road on a random buildable edge.
     */
    private void buildRoad() {
        playerController.getPlayerState().buildableRoadEdges().stream().findAny().ifPresent(edge -> {
            playerController.triggerAction(new BuildRoadAction(edge));
        });
    }

    /**
     * This method selects the required amount of random cards from the player's
     * resources.
     * Important: This only implements dropping cards not selecting cards for
     * example when using a development card.
     */
    private void selectCards() {
        final Map<ResourceType, Integer> selectedCards = new HashMap<>();
        for (int i = 0; i < playerController.getPlayerState().cardsToSelect(); i++) {
            playerController.getPlayer().getResources().entrySet().stream()
                .filter(entry -> entry.getValue() - selectedCards.getOrDefault(entry.getKey(), 0) > 0).findAny()
                .ifPresent(entry -> {
                    selectedCards.put(entry.getKey(), selectedCards.getOrDefault(entry.getKey(), 0) + 1);
                });
        }
        playerController.triggerAction(new SelectCardsAction(selectedCards));
    }

    /**
     * This method selects a random robber tile.
     */
    private void selectRobberTileAction() {
        playerController.triggerAction(
            new SelectRobberTileAction(hexGrid.getTiles().values().stream().findAny().get().getPosition()));
    }

    /**
     * This method steals a random card from a random player that has resources.
     * <p>
     * Important: When there is nothing to steal or no one to steal from, no action
     * is performed and the EndTurnAction is triggered due to the control flow in
     * executeActionBasedOnObjective.
     * When implementing a more sophisticated AI, you may want to explicitly trigger
     * the EndTurnAction.
     */
    private void stealCardAction() {
        final Player playerToStealFrom = playerController.getPlayerState().playersToStealFrom().stream().findAny()
            .orElse(null);
        if (playerToStealFrom == null) {
            return;
        }
        final ResourceType resourceToSteal = playerToStealFrom.getResources().entrySet().stream()
            .filter(entry -> entry.getValue() > 0).map(Entry::getKey).findAny().orElse(null);
        if (resourceToSteal == null) {
            return;
        }
        playerController.triggerAction(new StealCardAction(resourceToSteal, playerToStealFrom));
    }
}
