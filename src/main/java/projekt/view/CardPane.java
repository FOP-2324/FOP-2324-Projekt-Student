package projekt.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * A StackPane that displays a card with an icon and a label.
 */
@DoNotTouch
public class CardPane extends StackPane {
    private static final int defaultCardWidth = 40;

    /**
     * Creates a new CardPane with the given color and no icon or label.
     *
     * @param cardColor The color of the card.
     */
    public CardPane(final Color cardColor) {
        this(cardColor, "", "");
    }

    /**
     * Creates a new CardPane with the given color and label and no icon.
     *
     * @param cardColor The color of the card.
     * @param labelText The text of the label.
     */
    public CardPane(final Color cardColor, final String labelText) {
        this(cardColor, "", labelText);
    }

    /**
     * Creates a new CardPane with the given color and icon and label.
     *
     * @param cardColor The color of the card.
     * @param labelText The text of the label.
     * @param iconPath  The path to the icon.
     */
    public CardPane(final Color cardColor, final String iconPath, final String labelText) {
        this(cardColor, new Image(iconPath), labelText);
    }

    /**
     * Creates a new CardPane with the given color, icon and label.
     *
     * @param cardColor The color of the card.
     * @param icon      The icon to display
     * @param labelText The text of the label.
     */
    public CardPane(final Color cardColor, final Image icon, final String labelText) {
        this(cardColor, new ImageView(icon), labelText, 0);
    }

    /**
     * Creates a card with the given color, icon, label and width. If
     * the width is 0, the default width is used.
     *
     * @param cardColor The color of the card.
     * @param icon      The icon to display
     * @param labelText The text of the label.
     * @param cardWidth The width of the card.
     */
    public CardPane(final Color cardColor, final ImageView icon, final String labelText, double cardWidth) {
        super();
        this.setAlignment(Pos.CENTER);
        final ImageView cardImage = new ColoredImageView(Utils.emptyCardImage, cardColor);

        if (cardWidth <= 0) {
            cardWidth = defaultCardWidth;
        }

        cardImage.setFitWidth(cardWidth);
        cardImage.setPreserveRatio(true);
        this.getChildren().add(cardImage);
        final VBox iconBox = new VBox();
        if (icon != null) {
            icon.setFitWidth(cardWidth * 0.7);
            icon.setPreserveRatio(true);
            iconBox.getChildren().add(icon);
        }

        if (labelText != null && !labelText.isBlank()) {
            final Label valueLabel = new Label(labelText);
            valueLabel.getStyleClass().add("highlighted-label");
            iconBox.getChildren().add(valueLabel);
        }
        iconBox.setAlignment(Pos.CENTER);
        this.getChildren().add(iconBox);
    }
}
