package projekt.controller;

import javafx.beans.property.Property;
import projekt.model.GameState;
import projekt.model.HexGrid;

/**
 * Represents an AI controller that can execute actions based on a player's
 * objective.
 * Gets all information that could be needed to execute actions.
 * Automatically subscribes to the player objective property to execute actions
 * when the player's objective changes.
 */
public abstract class AiController {
    protected final PlayerController playerController;
    protected final HexGrid hexGrid;
    protected final GameState gameState;
    protected final Property<PlayerController> activePlayerController;

    /**
     * Creates a new AI controller with the given player controller, hex grid, game
     * state and active player controller.
     * Adds a subscription to the player objective property to execute actions when
     * the player's objective changes.
     *
     * @param playerController       the player controller
     * @param hexGrid                the hex grid
     * @param gameState              the game state
     * @param activePlayerController the active player controller
     */
    public AiController(
        final PlayerController playerController, final HexGrid hexGrid, final GameState gameState,
        final Property<PlayerController> activePlayerController
    ) {
        this.playerController = playerController;
        this.hexGrid = hexGrid;
        this.gameState = gameState;
        this.activePlayerController = activePlayerController;
        playerController.getPlayerObjectiveProperty().subscribe(this::executeActionBasedOnObjective);
    }

    /**
     * Executes an action that is allowed by the given player objective.
     * May perform multiple actions if necessary and allowed.
     *
     * @param objective the player objective
     */
    protected abstract void executeActionBasedOnObjective(final PlayerObjective objective);
}
