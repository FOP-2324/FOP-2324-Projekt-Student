package projekt.model;

import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * Enum for the different resource types.
 * Each resource type has a color and icon index associated with it.
 * Icon index is used to determine which icon to use for the resource type.
 * The different icons are:
 * 0: Sheep
 * 1: Wheat
 * 2: Bricks
 * 3: Tree
 * 4: Rocks
 * 5: Ingots
 */
@DoNotTouch
public enum ResourceType {
    WOOD(Color.DARKGREEN, 3),
    CLAY(Color.SIENNA, 2),
    WOOL(Color.LINEN, 0),
    GRAIN(Color.YELLOW, 1),
    ORE(Color.SLATEGRAY, 4);

    public final Color color;
    public final int iconIndex;

    ResourceType(final Color color, final int iconIndex) {
        this.color = color;
        this.iconIndex = iconIndex;
    }
}
