package projekt.view;

import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Builder;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Port;

import java.util.List;

/**
 * A Builder to create views for {@link Port}s.
 * Renders the {@link Port} as a circle with a resource icon and a label for the
 * ratio.
 * Has methods to initialize the connections to the nodes.
 */
public class PortBuilder implements Builder<Region> {

    private final ObservableDoubleValue width;
    private final Edge edge;
    private final ObservableDoubleValue height;
    private final Point2D node0;
    private final Point2D node1;

    /**
     * Creates a new PortBuilder on the given {@link Edge}.
     *
     * @param edge   The edge the port is on.
     * @param width  The width of the port.
     * @param height The height of the port.
     * @param node0  The position of the first node.
     * @param node1  The position of the second node.
     */
    public PortBuilder(
        final Edge edge, final ObservableDoubleValue width, final ObservableDoubleValue height,
        final Point2D node0, final Point2D node1
    ) {
        this.width = width;
        this.height = height;
        this.node0 = node0;
        this.node1 = node1;
        if (!edge.hasPort()) {
            throw new IllegalArgumentException("Edge has no port");
        }
        this.edge = edge;
    }

    @Override
    public Region build() {
        final StackPane mainPane = new StackPane();
        mainPane.minHeightProperty().bind(height);
        mainPane.minWidthProperty().bind(width);
        mainPane.maxHeightProperty().bind(height);
        mainPane.maxWidthProperty().bind(width);
        final Circle background = new Circle((width.get() / 2) * 0.6, Color.WHITE);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(3);
        final Node icon;
        if (edge.getPort().resourceType() != null) {
            final ImageView resourceImage = new Sprite(Utils.resourcesSpriteSheet, edge.getPort().resourceType().iconIndex,
                                                       edge.getPort().resourceType().color
            );
            resourceImage.setFitWidth(background.getRadius() * 2 * 0.5);
            resourceImage.setPreserveRatio(true);
            icon = resourceImage;
        } else {
            final Label missingLabel = new Label("?");
            missingLabel.setFont(new Font(30));
            missingLabel.getStyleClass().add("bold");
            missingLabel.setPadding(new Insets(-5));
            icon = missingLabel;
        }

        final Label ratioLabel = new Label(String.format("%d:1", edge.getPort().ratio()));
        ratioLabel.setFont(Font.font(10));
        ratioLabel.getStyleClass().add("highlighted-label");
        final VBox iconBox = new VBox(icon, ratioLabel);
        iconBox.setAlignment(Pos.CENTER);
        mainPane.getChildren().addAll(background, iconBox);
        return mainPane;
    }

    /**
     * Initializes the connections to the nodes.
     *
     * @param center The center of the port.
     * @return The connections to the nodes.
     */
    public List<Node> initConnections(final Point2D center) {
        final Line connection0 = new Line(center.getX(), center.getY(), node0.getX(), node0.getY());
        final Line connection1 = new Line(center.getX(), center.getY(), node1.getX(), node1.getY());
        connection0.setStrokeWidth(3);
        connection1.setStrokeWidth(3);
        connection0.setStroke(Color.BLACK);
        connection1.setStroke(Color.BLACK);
        return List.of(connection0, connection1);
    }
}
