package projekt.view;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Builder;
import projekt.model.Intersection;
import projekt.model.buildings.Settlement;

import java.util.function.Consumer;

/**
 * A Builder to create views for {@link Intersection}s.
 * Renders the {@link Settlement} of the intersection with a sprite from
 * {@link Utils#settlementsSpriteSheet}
 * Has methods to highlight and unhighlight the intersection.
 */
public class IntersectionBuilder implements Builder<Region> {
    private final Intersection intersection;
    private final StackPane pane = new StackPane();

    /**
     * Creates a new IntersectionBuilder for the given {@link Intersection}.
     *
     * @param intersection the intersection to render
     */
    public IntersectionBuilder(final Intersection intersection) {
        this.intersection = intersection;
    }

    @Override
    public Region build() {
        pane.getChildren().clear();
        unhighlight();
        addSettlement();
        pane.getStyleClass().add("intersection");
        return pane;
    }

    /**
     * Adds the {@link Settlement} of the {@link Intersection} to the pane.
     */
    private void addSettlement() {
        final Settlement settlement = intersection.getSettlement();
        if (settlement == null) {
            return;
        }

        final Sprite settlementSprite = new Sprite(Utils.settlementsSpriteSheet, settlement.type().ordinal(),
                                                   settlement.owner().getColor()
        );
        settlementSprite.setFitWidth(25);
        settlementSprite.setPreserveRatio(true);

        pane.getChildren().add(settlementSprite);
    }

    /**
     * Returns the {@link Intersection} this builder renders.
     *
     * @return the intersection
     */
    public Intersection getIntersection() {
        return intersection;
    }

    /**
     * Highlights the intersection and sets a handler for mouse clicks.
     *
     * @param handler the handler to call when the intersection is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        unhighlight();
        final Circle circle = new Circle(15, Color.TRANSPARENT);
        circle.setStroke(Color.RED);
        circle.setStrokeWidth(4);
        circle.getStyleClass().add("selectable");
        pane.getChildren().add(circle);
        pane.setOnMouseClicked(handler::accept);
    }

    /**
     * Removes the highlight from the intersection.
     */
    public void unhighlight() {
        pane.getChildren().removeIf(Circle.class::isInstance);
        pane.setOnMouseClicked(null);
    }
}
