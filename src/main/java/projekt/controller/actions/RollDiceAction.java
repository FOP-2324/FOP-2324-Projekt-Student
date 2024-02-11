package projekt.controller.actions;

import projekt.controller.PlayerController;

/**
 * An action to roll the dice.
 */
public class RollDiceAction implements PlayerAction {

    /**
     * Rolls the dice.
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.rollDice();
    }
}
