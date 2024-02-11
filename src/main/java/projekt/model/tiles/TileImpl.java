package projekt.model.tiles;

import javafx.beans.value.ObservableDoubleValue;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.TilePosition;
import projekt.model.TilePosition.EdgeDirection;
import projekt.model.buildings.Edge;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds information on a tile.
 *
 * @param position       this tile's position
 * @param type           the resource type this tile can produce
 * @param rollNumber     the number that needs to be rolled to yield the resource
 * @param heightProperty the height of this tile
 * @param widthProperty  the width of this tile
 * @param hexGrid        the grid this tile is placed in
 * @see Tile
 * @see TilePosition
 */
public record TileImpl(
    TilePosition position,
    Type type,
    int rollNumber,
    ObservableDoubleValue heightProperty,
    ObservableDoubleValue widthProperty,
    HexGrid hexGrid
) implements Tile {

    /**
     * Alternative constructor with q- and r-coordinates instead of a {@link TilePosition}.
     *
     * @param q              the q-coordinate of this tile in the grid
     * @param r              the r-coordinate of this tile in the grid
     * @param type           the resource type this tile can produce
     * @param rollNumber     the number that needs to be rolled to yield the resource
     * @param heightProperty the height of this tile
     * @param widthProperty  the width of this tile
     * @param hexGrid        the grid this tile is placed in
     */
    @DoNotTouch
    public TileImpl(
        final int q,
        final int r,
        final Type type,
        final int rollNumber,
        final ObservableDoubleValue heightProperty,
        final ObservableDoubleValue widthProperty,
        final HexGrid hexGrid
    ) {
        this(new TilePosition(q, r), type, rollNumber, heightProperty, widthProperty, hexGrid);
    }

    @Override
    public TilePosition getPosition() {
        return position;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int getRollNumber() {
        return rollNumber;
    }

    @Override
    public HexGrid getHexGrid() {
        return hexGrid;
    }

    @Override
    public Set<Intersection> getIntersections() {
        return Arrays.stream(TilePosition.IntersectionDirection.values())
            .map(this::getIntersection)
            .collect(Collectors.toSet());
    }

    @Override
    public Edge getEdge(final EdgeDirection direction) {
        final var neighbour = TilePosition.neighbour(this.position, direction);
        return this.hexGrid.getEdges().get(Set.of(this.position, neighbour));
    }

    @Override
    public boolean addRoad(final EdgeDirection direction, final Player owner, final boolean checkVillages) {
        return this.hexGrid.addRoad(this.position, TilePosition.neighbour(this.position, direction), owner,
                                    checkVillages
        );
    }
}
