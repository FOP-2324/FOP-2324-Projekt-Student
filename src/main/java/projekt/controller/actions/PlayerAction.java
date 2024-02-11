package projekt.controller.actions;

import projekt.controller.PlayerController;

/**
 * An action that can be executed by a player and tells the player controller
 * what to do.
 */
public interface PlayerAction {
    /**
     * Executes the action.
     *
     * @param pc the player controller that executes the action
     * @throws IllegalActionException if the action is illegal
     */
    void execute(PlayerController pc) throws IllegalActionException;
}
