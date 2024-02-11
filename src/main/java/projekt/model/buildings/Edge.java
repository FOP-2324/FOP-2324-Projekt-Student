package projekt.model.buildings;

import javafx.beans.property.Property;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.TilePosition;

import java.util.Set;

/**
 * Holds information on a tile's edge.
 * An edge is defined by two adjacent {@link TilePosition}s or by the intersections on either end.
 */
public interface Edge {

    /**
     * Returns the HexGrid instance this edge is placed in.
     *
     * @return the HexGrid instance this edge is placed in
     */
    HexGrid getHexGrid();

    /**
     * Returns the first position.
     *
     * @return the first position
     */
    TilePosition getPosition1();

    /**
     * Returns the second position.
     *
     * @return the second position
     */
    TilePosition getPosition2();

    /**
     * Returns {@code true} if this edge is on the edge of the grid a gives access to a port, {@code false} otherwise.
     *
     * @return whether this edge provides access to a port
     */
    boolean hasPort();

    /**
     * Returns the port this edge provides access to, if any.
     *
     * @return the port this edge provides access to, if any
     */
    Port getPort();

    /**
     * Returns {@code true} if the given edge connects to this edge and {@code false} otherwise.
     *
     * @param other the other edge
     * @return whether the two edges are connected
     */
    boolean connectsTo(Edge other);

    /**
     * Returns the {@link TilePosition}s that this edge lies between.
     *
     * @return the adjacent tile positions
     */
    default Set<TilePosition> getAdjacentTilePositions() {
        return Set.of(getPosition1(), getPosition2());
    }

    /**
     * Returns the intersections on either end of this edge.
     *
     * @return the intersections
     */
    Set<Intersection> getIntersections();

    /**
     * Returns all edges that connect to this edge in the grid.
     *
     * @return all edges connected to this one
     */
    default Set<Edge> getConnectedEdges() {
        return getIntersections().stream()
            .flatMap(i -> i.getConnectedEdges().stream())
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Returns {@code true} if a player has built a road on this edge and {@code false} otherwise.
     *
     * @return whether a player has placed a road on this edge
     */
    default boolean hasRoad() {
        return getRoadOwnerProperty().getValue() != null;
    }

    /**
     * Returns the road's owner, if a road has been built on this edge.
     *
     * @return the road's owner, if a road has been built on this edge
     */
    Property<Player> getRoadOwnerProperty();

    default Player getRoadOwner() {
        return getRoadOwnerProperty().getValue();
    }

    /**
     * Returns the connected roads of the given player.
     *
     * @param player the player to check for.
     * @return the connected roads.
     */
    Set<Edge> getConnectedRoads(Player player);
}
