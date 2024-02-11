package projekt.view;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Builder;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.TilePosition;
import projekt.model.buildings.Edge;
import projekt.model.tiles.Tile;
import projekt.view.tiles.TileBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * The builder for the {@link HexGrid}.
 * It creates a pane with the hex grid and handles the placement of the tiles,
 * intersections, edges and ports.
 * The hex grid pane can be zoomed, panned and centered.
 */
public class HexGridBuilder implements Builder<Region> {
    private final HexGrid grid;
    private final BiConsumer<ScrollEvent, Region> scrollHandler;
    private final Consumer<MouseEvent> pressedHandler;
    private final BiConsumer<MouseEvent, Region> draggedHandler;
    private final Point2D maxPoint;
    private final Point2D minPoint;
    private final BiConsumer<Event, Region> centerButtonHandler;
    private final Set<IntersectionBuilder> intersectionBuilders;
    private final Set<EdgeLine> edgeLines;
    private final Set<TileBuilder> tileBuilders;

    private final Pane hexGridPane = new Pane();

    /**
     * Creates a new hex grid builder with the given hex grid, intersection
     * builders, edge lines, tile builders and event handlers.
     *
     * @param grid                 The hex grid.
     * @param intersectionBuilders The intersection builders.
     * @param edgeLines            The edge lines.
     * @param tileBuilders         The tile builders.
     * @param scrollHandler        The handler for the scroll event. Used for
     *                             zooming.
     * @param pressedHandler       The handler for the mouse pressed event.
     * @param draggedHandler       The handler for the mouse dragged event. Used for
     *                             panning
     * @param centerButtonHandler  The handler for the center button event.
     */
    public HexGridBuilder(
        final HexGrid grid, final Set<IntersectionBuilder> intersectionBuilders,
        final Set<EdgeLine> edgeLines,
        final Set<TileBuilder> tileBuilders, final BiConsumer<ScrollEvent, Region> scrollHandler,
        final Consumer<MouseEvent> pressedHandler, final BiConsumer<MouseEvent, Region> draggedHandler,
        final BiConsumer<Event, Region> centerButtonHandler
    ) {
        this.grid = grid;
        this.intersectionBuilders = intersectionBuilders;
        this.edgeLines = edgeLines;
        this.tileBuilders = tileBuilders;

        this.scrollHandler = scrollHandler;
        this.pressedHandler = pressedHandler;
        this.draggedHandler = draggedHandler;
        this.centerButtonHandler = centerButtonHandler;

        final BiFunction<ToIntFunction<TilePosition>, IntBinaryOperator, Integer> reduceTiles = (
            positionFunction,
            reduceFunction
        ) -> grid.getTiles().values().stream().map(Tile::getPosition).mapToInt(positionFunction)
            .reduce(reduceFunction).getAsInt();

        this.maxPoint = new Point2D(
            calculatePositionTranslation(new TilePosition(reduceTiles.apply(TilePosition::q, Integer::max), 0))
                .getX(),
            calculatePositionTranslation(new TilePosition(0, reduceTiles.apply(TilePosition::r, Integer::max)))
                .getY()
        );
        this.minPoint = new Point2D(
            calculatePositionTranslation(new TilePosition(reduceTiles.apply(TilePosition::q, Integer::min), 0))
                .getX(),
            calculatePositionTranslation(new TilePosition(0, reduceTiles.apply(TilePosition::r, Integer::min)))
                .getY()
        );
    }

    @Override
    public Region build() {
        hexGridPane.getChildren().clear();

        edgeLines.stream().map(EdgeLine::getEdge).filter(Edge::hasPort).forEach(this::placePort);

        hexGridPane.getChildren().addAll(tileBuilders.stream().map(this::placeTile).toList());

        hexGridPane.maxWidthProperty().bind(Bindings
                                                .createDoubleBinding(
                                                    () -> Math.abs(minPoint.getX()) + maxPoint.getX() + grid.getTileWidth(),
                                                    grid.tileSizeProperty()
                                                ));
        hexGridPane.maxHeightProperty().bind(Bindings
                                                 .createDoubleBinding(
                                                     () -> Math.abs(minPoint.getY()) + maxPoint.getX() + grid.getTileHeight(),
                                                     grid.tileSizeProperty()
                                                 ));
        hexGridPane.minWidthProperty().bind(hexGridPane.maxWidthProperty());
        hexGridPane.minHeightProperty().bind(hexGridPane.maxHeightProperty());

        edgeLines.forEach(this::placeEdge);
        hexGridPane.getChildren().addAll(intersectionBuilders.stream().map(this::placeIntersection).toList());

        final StackPane mapPane = new StackPane(hexGridPane);
        mapPane.getStylesheets().add("css/hexmap.css");
        mapPane.getStyleClass().add("hex-grid");
        mapPane.setOnScroll(event -> scrollHandler.accept(event, hexGridPane));
        mapPane.setOnMousePressed(pressedHandler::accept);
        mapPane.setOnMouseDragged(event -> draggedHandler.accept(event, hexGridPane));

        final Button centerButton = new Button("Center map");
        centerButton.setOnAction(event -> centerButtonHandler.accept(event, hexGridPane));
        centerButton.translateXProperty().bind(Bindings
                                                   .createDoubleBinding(
                                                       () -> (centerButton.getWidth() - mapPane.getWidth()) / 2 + 10,
                                                       mapPane.widthProperty()
                                                   ));
        centerButton.translateYProperty().bind(Bindings
                                                   .createDoubleBinding(
                                                       () -> (mapPane.getHeight() - centerButton.getHeight()) / 2 - 10,
                                                       mapPane.heightProperty()
                                                   ));

        mapPane.getChildren().add(centerButton);

        return mapPane;
    }

    /**
     * Draws the tiles on the hex grid.
     */
    public void drawTiles() {
        tileBuilders.forEach(TileBuilder::build);
    }

    /**
     * Places a tile on the hex grid.
     *
     * @param builder The tile builder.
     * @return The region of the tile.
     */
    private Region placeTile(final TileBuilder builder) {
        final Region tileView = builder.build();
        final Tile tile = builder.getTile();
        final TilePosition position = tile.getPosition();
        final Point2D translatedPoint = calculatePositionTranslationOffset(position);
        tileView.translateXProperty().bind(
            Bindings.createDoubleBinding(() -> (translatedPoint.getX()), tile.widthProperty()));
        tileView.translateYProperty().bind(
            Bindings.createDoubleBinding(() -> translatedPoint.getY(), tile.heightProperty()));
        return tileView;
    }

    /**
     * Draws the intersections on the hex grid.
     */
    public void drawIntersections() {
        intersectionBuilders.forEach(IntersectionBuilder::build);
    }

    /**
     * Places an intersection on the hex grid.
     *
     * @param builder The intersection builder.
     * @return The region of the intersection.
     */
    private Region placeIntersection(final IntersectionBuilder builder) {
        final Region intersectionView = builder.build();
        final Point2D translatedPoint = calculateIntersectionTranslation(builder.getIntersection());
        intersectionView.translateXProperty().bind(Bindings.createDoubleBinding(
            () -> (translatedPoint.getX() - intersectionView.getWidth() / 2), intersectionView.widthProperty()));
        intersectionView.translateYProperty().bind(Bindings.createDoubleBinding(
            () -> (translatedPoint.getY() - intersectionView.getHeight() / 2), intersectionView.heightProperty()));
        return intersectionView;
    }

    /**
     * Draws the edges on the hex grid.
     */
    public void drawEdges() {
        edgeLines.forEach(EdgeLine::init);
    }

    /**
     * Places an edge on the hex grid.
     *
     * @param edgeLine The edge line.
     */
    private void placeEdge(final EdgeLine edgeLine) {
        final List<Intersection> intersections = edgeLine.getEdge().getIntersections().stream().toList();
        final Point2D translatedStart = calculateIntersectionTranslation(intersections.get(0));
        final Point2D translatedEnd = calculateIntersectionTranslation(intersections.get(1));
        edgeLine.setStartX(translatedStart.getX());
        edgeLine.setStartY(translatedStart.getY());
        edgeLine.setEndX(translatedEnd.getX());
        edgeLine.setEndY(translatedEnd.getY());
        edgeLine.init();
        hexGridPane.getChildren().add(edgeLine.getOutline());
        hexGridPane.getChildren().add(edgeLine);
    }

    /**
     * Places a port on the hex grid.
     *
     * @param edge The edge the port is on.
     */
    private void placePort(final Edge edge) {
        final TilePosition position = edge.getAdjacentTilePositions().stream()
            .filter(Predicate.not(grid.getTiles()::containsKey))
            .findAny()
            .orElseThrow();
        final List<Intersection> intersections = edge.getIntersections().stream().toList();
        final Point2D node0 = calculateIntersectionTranslation(intersections.get(0));
        final Point2D node1 = calculateIntersectionTranslation(intersections.get(1));
        final PortBuilder portBuilder = new PortBuilder(edge, grid.tileWidthProperty(), grid.tileHeightProperty(),
                                                        node0, node1
        );
        final Region portView = portBuilder.build();
        final Point2D translatedPoint = calculatePositionTranslationOffset(position);
        portView.translateXProperty().bind(Bindings.createDoubleBinding(
            () -> (translatedPoint.getX() - portView.getWidth() / 2), grid.tileWidthProperty()));
        portView.translateYProperty().bind(Bindings.createDoubleBinding(
            () -> (translatedPoint.getY() - portView.getHeight() / 2), grid.tileHeightProperty()));
        hexGridPane.getChildren().addAll(portBuilder.initConnections(calculatePositionCenterOffset(position)));
        hexGridPane.getChildren().addAll(portView);
    }

    /**
     * Calculates the upper left corner of the tile region.
     *
     * @param position The position of the tile.
     * @return The point of the upper left corner.
     */
    private Point2D calculatePositionTranslation(final TilePosition position) {
        return new Point2D(
            grid.getTileSize() * (Math.sqrt(3) * position.q() + Math.sqrt(3) / 2 * position.r()),
            grid.getTileSize() * (3.0 / 2 * position.r())
        );
    }

    /**
     * Calculates the upper left corner of the tile region with an offset to move
     * the coordinate system center to the center of the hex grid.
     *
     * @param position The position of the tile.
     * @return The point of the upper left corner.
     */
    private Point2D calculatePositionTranslationOffset(final TilePosition position) {
        return calculatePositionTranslation(position).add(Math.abs(minPoint.getX()), Math.abs(minPoint.getY()));
    }

    /**
     * Calculates the center of the tile region with an offset to move the
     * coordinate system center to the center of the hex grid.
     *
     * @param position The position of the tile.
     * @return The point of the center.
     */
    private Point2D calculatePositionCenterOffset(final TilePosition position) {
        return calculatePositionTranslationOffset(position).add(grid.getTileWidth() / 2, grid.getTileHeight() / 2);
    }

    /**
     * Calculates the center of the intersection region with an offset to move the
     * coordinate system center to the center of the hex grid.
     *
     * @param intersection The intersection.
     * @return The point of the center.
     */
    private Point2D calculateIntersectionTranslation(final Intersection intersection) {
        final Set<Point2D> adjacentPoints = intersection.getAdjacentTilePositions().stream()
            .map(this::calculatePositionCenterOffset)
            .collect(Collectors.toSet());
        return adjacentPoints.stream().reduce(Point2D::add).get().multiply(1.0 / 3);
    }
}
