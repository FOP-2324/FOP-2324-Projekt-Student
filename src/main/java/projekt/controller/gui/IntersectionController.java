package projekt.controller.gui;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import projekt.model.Intersection;
import projekt.view.IntersectionBuilder;

import java.util.function.Consumer;

/**
 * The controller for an intersection.
 */
public class IntersectionController implements Controller {
    private final IntersectionBuilder buidler;

    /**
     * Creates a new intersection controller.
     *
     * @param intersection the intersection to render
     */
    public IntersectionController(final Intersection intersection) {
        this.buidler = new IntersectionBuilder(intersection);
    }

    /**
     * Returns the intersection.
     *
     * @return the intersection
     */
    public Intersection getIntersection() {
        return buidler.getIntersection();
    }

    /**
     * Highlights the intersection.
     *
     * @param handler the handler to call when the intersection is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        Platform.runLater(() -> buidler.highlight(handler));
    }

    /**
     * Unhighlights the intersection.
     */
    public void unhighlight() {
        Platform.runLater(() -> buidler.unhighlight());
    }

    @Override
    public IntersectionBuilder getBuilder() {
        return buidler;
    }
}
