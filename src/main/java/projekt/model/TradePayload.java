package projekt.model;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import java.util.Map;

/**
 * Holds information on a trade's payload / resources.
 *
 * @param offer    the offered resources mapped to their amount
 * @param request  the requested resources mapped to their amount
 * @param withBank whether the bank is the other party in this trade
 * @param player   the player that initiated the trade
 */
@DoNotTouch
public record TradePayload(
    Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request, boolean withBank,
    Player player
) {

    @Override
    public String toString() {
        return String.format("TradePayload [offer=%s, request=%s, withBank=%s, player=%s]", offer, request, withBank,
                             player
        );
    }
}
