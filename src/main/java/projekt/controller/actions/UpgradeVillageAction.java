package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.Intersection;

/**
 * An action to upgrade a village to a city.
 *
 * @param intersection the intersection to upgrade the village at
 */
public record UpgradeVillageAction(Intersection intersection) implements PlayerAction {

    /**
     * Upgrades the village to a city if possible.
     *
     * @throws IllegalActionException if the village cannot be upgraded
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.upgradeVillage(intersection);
    }
}
