package projekt.model.tiles;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.ResourceType;
import projekt.model.TilePosition;
import projekt.model.TilePosition.EdgeDirection;
import projekt.model.TilePosition.IntersectionDirection;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Settlement;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a tile in the game grid.
 * A tile has six sides ({@link Edge}s), six vertices ({@link Intersection}s), a {@link ResourceType} and a roll number.
 */
@DoNotTouch
public interface Tile {

    /**
     * Returns the position of this tile.
     *
     * @return the position of this tile
     */
    TilePosition getPosition();

    /**
     * Returns the type of this tile.
     *
     * @return the type of this tile
     */
    Type getType();

    /**
     * Returns the roll number of this tile.
     *
     * @return the roll number of this tile
     */
    int getRollNumber();

    /**
     * Returns the height of this tile.
     *
     * @return the height of this tile
     */
    ObservableDoubleValue heightProperty();

    /**
     * Returns the width of this tile.
     *
     * @return the width of this tile
     */
    ObservableDoubleValue widthProperty();

    /**
     * Returns the hex grid this tile is part of.
     *
     * @return the hex grid this tile is part of
     */
    HexGrid getHexGrid();


    /**
     * Returns all intersections adjacent to this tile.
     *
     * @return all intersections adjacent to this tile
     */
    Set<Intersection> getIntersections();

    /**
     * Returns the intersection in the given direction.
     *
     * @param direction the direction of the intersection
     * @return the intersection in the given direction
     */
    default Intersection getIntersection(final IntersectionDirection direction) {
        return getHexGrid().getIntersections().get(getIntersectionPositions(direction));
    }

    /**
     * Returns a set of the position defining the intersection in the given direction.
     *
     * @param direction the direction of the intersection
     * @return a set of positions defining the intersection
     */
    default Set<TilePosition> getIntersectionPositions(final IntersectionDirection direction) {
        return Set.of(
            getPosition(),
            TilePosition.neighbour(getPosition(), direction.leftDirection),
            TilePosition.neighbour(getPosition(), direction.rightDirection)
        );
    }

    /**
     * Returns all neighbours of this tile.
     *
     * @return all neighbours of this tile
     */
    default Set<Tile> getNeighbours() {
        return getHexGrid().getTiles().entrySet().stream()
            .filter(entrySet -> TilePosition.neighbours(getPosition()).contains(entrySet.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());
    }

    /**
     * Returns the tile next to the given edge.
     *
     * @param direction the direction of the edge
     * @return the neighbouring tile
     */
    default Tile getNeighbour(final EdgeDirection direction) {
        return getHexGrid().getTileAt(TilePosition.neighbour(getPosition(), direction));
    }

    /**
     * Returns the edge on the given edge.
     *
     * @param direction the direction of the edge
     * @return the edge on the given edge
     */
    Edge getEdge(EdgeDirection direction);

    /**
     * Add a road on the given edge.
     * Check {@link HexGrid#addRoad(TilePosition, TilePosition, Player, boolean)}
     * for details.
     *
     * @param direction     the direction of the edge
     * @param owner         the player who owns the road
     * @param checkVillages whether to check if the player has a connected village
     * @return whether the road was added
     */
    boolean addRoad(EdgeDirection direction, Player owner, boolean checkVillages);

    /**
     * Returns all settlements adjacent to this tile.
     *
     * @return all settlements adjacent to this tile
     */
    default Set<Settlement> getSettlements() {
        return getIntersections().stream().map(Intersection::getSettlement)
            .filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Place a Village at the intersection in the given direction for the given player.
     * Check {@link Intersection#placeVillage(Player, boolean)} for details.
     *
     * @param direction       the direction of the intersection
     * @param player          the player who owns the settlement
     * @param ignoreRoadCheck whether to ignore the condition that the player needs
     *                        a connected road
     * @return whether the settlement was placed
     */
    default boolean placeVillage(
        final IntersectionDirection direction,
        final Player player,
        final boolean ignoreRoadCheck
    ) {
        return getIntersection(direction).placeVillage(player, ignoreRoadCheck);
    }

    /**
     * Returns whether the robber is currently on this tile.
     *
     * @return whether the robber is currently on this tile
     */
    default boolean hasRobber() {
        return getHexGrid().getRobberPosition().equals(getPosition());
    }

    /**
     * An enumeration containing all available tile types.
     * Custom tile types need to be added to this list manually.
     */
    enum Type {
        WOODLAND(Color.web("#99C000"), ResourceType.WOOD),
        MEADOW(Color.web("#50B695"), ResourceType.WOOL),
        FARMLAND(Color.web("#FDCA00"), ResourceType.GRAIN),
        HILL(Color.web("#A94913"), ResourceType.CLAY),
        MOUNTAIN(Color.web("#B5B5B5"), ResourceType.ORE),
        DESERT(Color.web("#FFE05C"), null);

        /**
         * The color of the tile.
         */
        public final Color color;
        /**
         * The resource type of the tile.
         */
        public final ResourceType resourceType;

        Type(final Color color, final ResourceType resourceType) {
            this.color = color;
            this.resourceType = resourceType;
        }
    }
}
