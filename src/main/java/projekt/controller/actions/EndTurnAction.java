package projekt.controller.actions;

import projekt.controller.PlayerController;

/**
 * An action to end the turn.
 */
public class EndTurnAction implements PlayerAction {

    /**
     * Ends the turn.
     * The action does not execute any specific code, but is picked up by the
     * gameController.
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        // This action does not execute any specific code, but is picked up by the
        // gameController.
    }
}
