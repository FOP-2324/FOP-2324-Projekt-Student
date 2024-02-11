package projekt.controller.gui;

import javafx.scene.input.MouseEvent;
import projekt.model.buildings.Edge;
import projekt.view.EdgeLine;

import java.util.function.Consumer;

/**
 * The controller for an edge.
 */
public class EdgeController {
    private final EdgeLine line;

    /**
     * Creates a new edge controller.
     *
     * @param edge the edge to render
     */
    public EdgeController(final Edge edge) {
        this.line = new EdgeLine(edge);
    }

    /**
     * Returns the edge.
     *
     * @return the edge
     */
    public Edge getEdge() {
        return line.getEdge();
    }

    /**
     * Returns the edge line.
     *
     * @return the edge line
     */
    public EdgeLine getEdgeLine() {
        return line;
    }

    /**
     * Highlights the edge.
     *
     * @param handler the handler to call when the edge is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        line.highlight(handler);
    }

    /**
     * Unhighlights the edge.
     */
    public void unhighlight() {
        line.unhighlight();
    }
}
