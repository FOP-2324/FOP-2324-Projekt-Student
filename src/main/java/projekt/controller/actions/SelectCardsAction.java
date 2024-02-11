package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.ResourceType;

import java.util.Map;

/**
 * An action to select cards from the player's hand.
 *
 * @param selectedCards the cards the player selected
 */
public record SelectCardsAction(Map<ResourceType, Integer> selectedCards) implements PlayerAction {

    /**
     * Processes the selected cards.
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.processSelectedResources(selectedCards);
    }

}
