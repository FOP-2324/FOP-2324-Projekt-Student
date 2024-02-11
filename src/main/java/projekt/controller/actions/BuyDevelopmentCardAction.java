package projekt.controller.actions;

import projekt.controller.PlayerController;

/**
 * An action to buy a development card.
 */
public record BuyDevelopmentCardAction() implements PlayerAction {

    /**
     * Buys a random development card.
     *
     * @throws IllegalActionException if the development card cannot be bought
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.buyDevelopmentCard();
    }
}
