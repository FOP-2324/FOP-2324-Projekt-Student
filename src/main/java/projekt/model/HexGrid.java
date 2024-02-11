package projekt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.TilePosition.EdgeDirection;
import projekt.model.buildings.Edge;
import projekt.model.tiles.Tile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds all the information displayed on the hexagonal grid and information for
 * rendering. In short, the game board.
 * Specifically, information on...
 * <ul>
 *     <li>tiles and their logical and graphical properties (position, height, width, etc.)</li>
 *     <li>edges</li>
 *     <li>intersections</li>
 *     <li>the robber / bandit</li>
 * </ul>
 * are saved in and modified by instances of this interface.
 */
@DoNotTouch
public interface HexGrid {

    // Tiles

    /**
     * Returns the width of a tile.
     *
     * @return the width of a tile
     */
    double getTileWidth();

    /**
     * Returns the height of a tile.
     *
     * @return the height of a tile
     */
    double getTileHeight();

    /**
     * Returns the size of a tile.
     *
     * @return the size of a tile
     */
    double getTileSize();

    /**
     * Returns the width of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the width of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileWidthProperty();

    /**
     * Returns the height of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the height of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileHeightProperty();

    /**
     * Returns the size of a tile as an {@link DoubleProperty}.
     *
     * @return the size of a tile as an {@link DoubleProperty}
     */
    DoubleProperty tileSizeProperty();

    /**
     * Returns all tiles of the grid as a set.
     *
     * @return all tiles of the grid as a set
     */
    Map<TilePosition, Tile> getTiles();

    /**
     * Returns all tiles of the grid that have the given roll number as a set.
     *
     * @param diceRoll the roll number of the tiles
     * @return all tiles of the grid that have the given roll number as a set
     */
    Set<Tile> getTiles(int diceRoll);

    /**
     * Returns the tile at the given q and r coordinate.
     *
     * @param q the q-coordinate of the tile
     * @param r the r-coordinate of the tile
     * @return the tile at the given row and column
     */
    Tile getTileAt(int q, int r);

    /**
     * Returns the tile at the given position.
     *
     * @param position the position of the tile
     * @return the tile at the given position
     */
    Tile getTileAt(TilePosition position);


    // Intersections

    /**
     * Returns all intersections of the grid as a set.
     *
     * @return all intersections of the grid as a set
     */
    Map<Set<TilePosition>, Intersection> getIntersections();

    /**
     * Returns the intersection between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @param position2 the third position
     * @return the intersection at the given position
     */
    Intersection getIntersectionAt(TilePosition position0, TilePosition position1, TilePosition position2);


    // Edges / Roads

    /**
     * Returns all edges of the grid.
     *
     * @return all edges of the grid
     */
    Map<Set<TilePosition>, Edge> getEdges();

    /**
     * Returns the edge between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @return the edge between the given intersections
     */
    Edge getEdge(TilePosition position0, TilePosition position1);

    /**
     * Returns all roads of the given player.
     *
     * @param player the player to get the roads of
     * @return all roads of the given player
     */
    Map<Set<TilePosition>, Edge> getRoads(Player player);

    /**
     * Returns the longest continuous road of the given player.
     *
     * @param player the player to get the longest road of
     * @return list of all road segments that make up the longest road
     */
    List<Edge> getLongestRoad(Player player);

    /**
     * Adds the given road to the grid. Also, either checks if the player has a
     * connected road or a connected village with no other roads. Does not check or
     * remove the player's resources.
     *
     * @param position0     the first position of the road
     * @param position1     the second position of the road
     * @param player        the player that owns the road
     * @param checkVillages whether to check if the player has a connected village
     * @return whether the road was added
     */
    boolean addRoad(TilePosition position0, TilePosition position1, Player player, boolean checkVillages);

    /**
     * Adds the given road to the grid relative to the given tile.
     * See {@link HexGrid#addRoad(TilePosition, TilePosition, Player, boolean)}
     * for details.
     *
     * @param tile          the tile the road is next to
     * @param edgeDirection the direction of the edge the road is on
     * @param player        the player that owns the road
     * @param checkVillages whether to check if the player has a connected village
     * @return whether the road was added
     */
    default boolean addRoad(
        final Tile tile, final EdgeDirection edgeDirection, final Player player,
        final boolean checkVillages
    ) {
        return tile.addRoad(edgeDirection, player, checkVillages);
    }

    /**
     * Removes the road between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @return whether the road was removed
     */
    boolean removeRoad(TilePosition position0, TilePosition position1);

    /**
     * Removes the road at the given edge.
     *
     * @param road (the edge of) the road to remove
     * @return {@code true}, if the road has been successfully removed, {@code false} otherwise
     */
    default boolean removeRoad(final Edge road) {
        return removeRoad(road.getPosition1(), road.getPosition2());
    }


    // Robber / Bandit

    /**
     * Returns the current position of the robber.
     *
     * @return the current position of the robber
     */
    TilePosition getRobberPosition();

    /**
     * Sets the position of the robber.
     *
     * @param position the new position of the robber
     */
    void setRobberPosition(TilePosition position);
}
