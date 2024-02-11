package projekt.view;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import java.util.function.UnaryOperator;

/**
 * A collection of utility methods and constants for the gui.
 */
@DoNotTouch
public final class Utils {
    /**
     * A filter that only allows positive integers.
     * Can be used with {@link TextField#setTextFormatter(TextFormatter)} to make a
     * text field only accept positive integers.
     */
    public static final UnaryOperator<Change> positiveIntegerFilter = change -> {
        final String newText = change.getControlNewText();
        if (newText.matches("([0-9]*)?")) {
            return change;
        }
        return null;
    };

    /**
     * Attaches a tooltip to a node.
     *
     * @param text   The text to display in the tooltip.
     * @param target The node to attach the tooltip to.
     */
    public static void attachTooltip(final String text, final Node target) {
        final Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(target, tooltip);
    }

    /**
     * The sprite sheet for the resources.
     */
    public static final Image resourcesSpriteSheet = new Image("img/resources.png");

    /**
     * The sprite sheet for the settlements.
     */
    public static final Image settlementsSpriteSheet = new Image("img/settlements.png");

    /**
     * The sprite sheet for the cities.
     */
    public static final Image developmentCardsSpriteSheet = new Image("img/development_cards.png");

    /**
     * The sprite sheet for the development cards.
     */
    public static final Image emptyCardImage = new Image("img/empty_card.png");

    /**
     * The sprite sheet for the development cards.
     */
    public static final Image robberImage = new Image("img/robber.png");
}
