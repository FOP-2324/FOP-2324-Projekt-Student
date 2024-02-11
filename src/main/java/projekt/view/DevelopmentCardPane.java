package projekt.view;

import javafx.scene.paint.Color;
import projekt.model.DevelopmentCardType;

/**
 * A pane that represents a development card.
 * The development card is a colored {@link CardPane} with a development card
 * icon.
 * The label displays the amount of the development card.
 * The development card has a tooltip that displays the development card type.
 * The Icons are from {@link Utils#developmentCardsSpriteSheet}.
 */
public class DevelopmentCardPane extends CardPane {
    /**
     * Creates a new development card pane with the given type and amount.
     *
     * @param type   The type of the development card.
     * @param amount The amount of cards.
     */
    public DevelopmentCardPane(final DevelopmentCardType type, final int amount) {
        this(type, Integer.toString(amount), 0.0);
    }

    /**
     * Creates a new development card pane with the given type, label text and card
     * width.
     *
     * @param type      The type of the development card.
     * @param labelText The text of the label.
     * @param cardWidth The width of the card.
     */
    public DevelopmentCardPane(final DevelopmentCardType type, final String labelText, final double cardWidth) {
        super(Color.BLUEVIOLET, new Sprite(Utils.developmentCardsSpriteSheet, type.iconIndex, Color.BLACK), labelText,
              cardWidth
        );
        Utils.attachTooltip(type.toString(), this);
    }
}
