package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.buildings.Edge;

/**
 * An action to build a road.
 *
 * @param edge the edge to build the road at
 */
public record BuildRoadAction(Edge edge) implements PlayerAction {

    /**
     * Builds a road at the edge.
     *
     * @throws IllegalActionException if the road cannot be built
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.buildRoad(edge.getPosition1(), edge.getPosition2());
    }
}
