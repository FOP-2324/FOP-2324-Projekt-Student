package projekt.view;

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import projekt.model.buildings.Edge;
import projekt.model.buildings.EdgeImpl;

import java.util.function.Consumer;

/**
 * A Line that represents an {@link EdgeImpl}. Has methods to highlight and
 * unhighlight itself.
 */
public class EdgeLine extends Line {
    private final Edge edge;
    private double distance = 0;
    private final int strokeWidth = 5;
    private final double positionOffset = 10;
    private final Line outline = new Line();

    /**
     * Creates a new EdgeLine for the given {@link EdgeImpl}.
     *
     * @param edge the edge to represent
     */
    public EdgeLine(final Edge edge) {
        this.edge = edge;
        outline.startXProperty().bind(startXProperty());
        outline.startYProperty().bind(startYProperty());
        outline.endXProperty().bind(endXProperty());
        outline.endYProperty().bind(endYProperty());
        outline.strokeDashOffsetProperty().bind(strokeDashOffsetProperty());
        outline.setStrokeWidth(strokeWidth * 1.4);
        outline.setStroke(Color.TRANSPARENT);
        getStrokeDashArray().addListener((ListChangeListener<Double>) change -> {
            outline.getStrokeDashArray().clear();
            outline.getStrokeDashArray().addAll(change.getList());
        });
    }

    /**
     * Returns the {@link Edge} this EdgeLine represents.
     *
     * @return the edge
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns the outline of the EdgeLine.
     *
     * @return the outline
     */
    public Line getOutline() {
        return outline;
    }

    /**
     * Initializes the EdgeLine with a dashScale of 1.
     *
     * @see #init(double)
     */
    public void init() {
        init(1);
    }

    /**
     * Initializes the EdgeLine with the given dashScale.
     *
     * @param dashScale factor to scale the dash length by
     */
    public void init(final double dashScale) {
        this.distance = new Point2D(getStartX(), getStartY()).distance(getEndX(), getEndY());
        setStrokeWidth(strokeWidth);
        setStroke(edge.hasRoad() ? edge.getRoadOwner().getColor() : Color.TRANSPARENT);
        setStrokeDashOffset(-positionOffset / 2);
        getStrokeDashArray().clear();
        getStrokeDashArray().add((distance - positionOffset) * dashScale);
        if (edge.hasRoad()) {
            outline.setStroke(Color.BLACK);
        }
    }

    /**
     * Highlights the EdgeLine with the given handler.
     *
     * @param handler the handler to call when the EdgeLine is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        init(0.1);
        outline.setStroke(Color.BLACK);
        outline.setStrokeWidth(strokeWidth * 1.6);
        getStyleClass().add("selectable");
        getStrokeDashArray().add(10.0);
        setStrokeWidth(strokeWidth * 1.2);
        setOnMouseClicked(handler::accept);
    }

    /**
     * Removes the highlight from the EdgeLine.
     */
    public void unhighlight() {
        outline.setStroke(Color.TRANSPARENT);
        setOnMouseClicked(null);
        getStyleClass().remove("selectable");
        init();
    }
}
