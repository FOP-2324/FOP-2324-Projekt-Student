package projekt.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import projekt.model.Player;

/**
 * A Label that displays a player's name and color.
 */
public class PlayerLabel extends Label {
    /**
     * Creates a new player label for the given player.
     *
     * @param player The player to display.
     */
    public PlayerLabel(final Player player) {
        super(player.getName());
        final Rectangle playerColor = new Rectangle(20, 20, player.getColor());
        playerColor.setStroke(Color.BLACK);
        playerColor.setStrokeWidth(2);
        setGraphic(playerColor);
    }
}
