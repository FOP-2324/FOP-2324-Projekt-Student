package projekt.view;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.ResourceType;

/**
 * A Pane that displays a resource card with a label.
 * The resource card is a colored {@link CardPane} with a resource icon.
 * The label displays the amount of the resource.
 * The resource card has a tooltip that displays the resource type.
 * The Icons are from {@link Utils#resourcesSpriteSheet}.
 */
@DoNotTouch
public class ResourceCardPane extends CardPane {
    /**
     * Creates a new resource card pane with the given type and amount.
     *
     * @param resourceType The type of the resource.
     * @param amount       The amount of the resource.
     */
    public ResourceCardPane(final ResourceType resourceType, final int amount) {
        this(resourceType, Integer.toString(amount));
    }

    /**
     * Creates a new resource card pane with the given type. The label is empty.
     *
     * @param resourceType The type of the resource.
     */
    public ResourceCardPane(final ResourceType resourceType) {
        this(resourceType, null, 0.0);
    }

    /**
     * Creates a new resource card pane with the given type and label text.
     *
     * @param resourceType The type of the resource.
     * @param labelText    The text of the label.
     */
    public ResourceCardPane(final ResourceType resourceType, final String labelText) {
        this(resourceType, labelText, 0.0);
    }

    /**
     * Creates a new resource card pane with the given type, label text and card
     * width.
     *
     * @param resourceType The type of the resource.
     * @param labelText    The text of the label.
     * @param cardWidth    The width of the card.
     */
    public ResourceCardPane(final ResourceType resourceType, final String labelText, final double cardWidth) {
        super(resourceType.color, new Sprite(Utils.resourcesSpriteSheet, resourceType.iconIndex, resourceType.color),
              labelText, cardWidth
        );
        Utils.attachTooltip(resourceType.toString(), this);
    }
}
