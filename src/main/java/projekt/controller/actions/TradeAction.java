package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.Player;
import projekt.model.ResourceType;
import projekt.model.TradePayload;

import java.util.Map.Entry;

/**
 * An action to trade with the bank or other players.
 *
 * @param payload the trade payload
 */
public record TradeAction(TradePayload payload) implements PlayerAction {

    /**
     * Executes the trade.
     * <p>
     * When trading with the bank, the ratio is determined by
     * {@link Player#getTradeRatio(ResourceType)}.
     * When trading with other players, each player is offered the trade and can
     * accept or decline it.
     *
     * @throws IllegalActionException if the trade cannot be executed
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        if (payload.withBank()) {
            final Entry<ResourceType, Integer> offer = payload.offer().entrySet().iterator().next();
            pc.tradeWithBank(offer.getKey(), offer.getValue(), payload.request().keySet().iterator().next());
        } else {
            pc.offerTrade(payload.offer(), payload.request());
        }
    }
}
