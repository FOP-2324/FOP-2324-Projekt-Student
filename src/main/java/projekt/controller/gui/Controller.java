package projekt.controller.gui;

import javafx.scene.layout.Region;
import javafx.util.Builder;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * A controller for a view.
 */
@DoNotTouch
public interface Controller {
    /**
     * Returns a builder for the view of this controller.
     *
     * @return a builder for the view of this controller
     */
    Builder<Region> getBuilder();

    /**
     * Returns the view of this controller.
     *
     * @return the view of this controller
     */
    default Region buildView() {
        return getBuilder().build();
    }
}
