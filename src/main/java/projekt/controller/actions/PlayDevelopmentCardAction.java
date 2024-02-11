package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.DevelopmentCardType;

/**
 * An action to play a development card.
 *
 * @param developmentCard the development card to play
 */
public record PlayDevelopmentCardAction(DevelopmentCardType developmentCard) implements PlayerAction {

    /**
     * Plays the development card.
     *
     * @throws IllegalActionException if the development card cannot be played
     * @see DevelopmentCardType
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.playDevelopmentCard(developmentCard);
    }
}
