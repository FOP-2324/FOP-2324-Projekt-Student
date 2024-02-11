package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.Player;
import projekt.model.ResourceType;

/**
 * An action to steal a card from another player.
 *
 * @param resourceToSteal   the resource to steal
 * @param playerToStealFrom the player to steal from
 */
public record StealCardAction(ResourceType resourceToSteal, Player playerToStealFrom) implements PlayerAction {

    /**
     * Steals the resource from the player.
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.selectPlayerAndResourceToSteal(playerToStealFrom, resourceToSteal);
    }
}
