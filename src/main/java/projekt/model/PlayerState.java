package projekt.model;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.buildings.Edge;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds information on a player's state.
 *
 * @param buildableVillageIntersections  a set of intersections on which villages can be built
 * @param upgradableVillageIntersections a set of intersections with villages that can be upgraded
 * @param buildableRoadEdges             a set of edges where road can be built
 * @param playersToStealFrom             a list of players this player can steal from
 * @param offeredTrade                   a trade payload offered to this player
 * @param cardsToSelect                  how many cards this player has to drop
 * @param changedResources               which resources have changed since the last update
 */
@DoNotTouch
public record PlayerState(
        Set<Intersection> buildableVillageIntersections,
        Set<Intersection> upgradableVillageIntersections,
        Set<Edge> buildableRoadEdges,
        List<Player> playersToStealFrom,
        TradePayload offeredTrade,
        int cardsToSelect,
        Map<ResourceType, Integer> changedResources,
        PlayerObjective playerObjective) {
}
