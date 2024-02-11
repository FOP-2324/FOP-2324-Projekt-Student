package projekt.controller.actions;

import projekt.controller.PlayerController;

/**
 * An action to accept or decline a trade offer.
 *
 * @param accepted whether the trade offer is accepted
 */
public record AcceptTradeAction(boolean accepted) implements PlayerAction {

    /**
     * Accepts or declines the trade offer.
     *
     * @throws IllegalActionException if the trade offer cannot be accepted or
     *                                declined
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.acceptTradeOffer(accepted);
    }
}
