package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.Intersection;

/**
 * An action to build a village.
 *
 * @param intersection the intersection to build the village at
 */
public record BuildVillageAction(Intersection intersection) implements PlayerAction {

    /**
     * Builds a village at the intersection.
     *
     * @throws IllegalActionException if the village cannot be built
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.buildVillage(intersection);
    }
}
