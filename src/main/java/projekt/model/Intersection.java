package projekt.model;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Port;
import projekt.model.buildings.Settlement;
import projekt.model.tiles.Tile;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an intersection between three tile positions or at least two edges in the game.
 * As an example, the following intersection has the positions ordered clockwise:
 * <pre>
 *      |
 *      |
 *   0  *  1
 *     / \
 *    / 2 \
 * </pre>
 * <p>
 * Intersections connect at least two and at most three (virtual, if necessary) tiles / edges.
 * They hold information on...
 * <ul>
 *     <li>the grid they are placed in</li>
 *     <li>settlements - placed ones, placement and upgrade of villages</li>
 *     <li>ports, if any</li>
 *     <li>their connected edges</li>
 *     <li>their adjacent tiles</li>
 * </ul>
 */
@DoNotTouch
public interface Intersection {

    /**
     * Returns the hexGrid instance
     *
     * @return the hexGrid instance
     */
    HexGrid getHexGrid();

    /**
     * Returns the settlement on this intersection or null
     *
     * @return the settlement on this intersection
     */
    Settlement getSettlement();

    /**
     * Returns true if there is a settlement on this intersection
     *
     * @return true if there is a settlement on this intersection
     */
    boolean hasSettlement();

    /**
     * Returns true if the player has a settlement on this intersection
     *
     * @param player the player to check
     * @return true if the player has a settlement on this intersection
     */
    boolean playerHasSettlement(Player player);

    /**
     * Places a village on this intersection for the given player. Verifies that the
     * player has a connected road to this intersection if not explicitly ignored.
     * Does not check or remove resources.
     *
     * @param player          the player who places the settlement
     * @param ignoreRoadCheck whether to ignore the condition that the player needs a connected road
     * @return whether the placement was successful
     */
    boolean placeVillage(Player player, boolean ignoreRoadCheck);

    /**
     * Upgrades the settlement on this intersection to a city. Player resources are not checked or removed.
     *
     * @param player the player who owns the settlement
     * @return whether the upgrade was successful
     */
    boolean upgradeSettlement(Player player);

    /**
     * Returns the port on this intersection or null
     *
     * @return the port on this intersection
     */
    Port getPort();

    /**
     * Returns all edges connected to this intersection.
     *
     * @return all edges connected to this intersection
     */
    Set<Edge> getConnectedEdges();

    /**
     * Returns true if the player has a connected road to this intersection
     *
     * @param player the player to check
     * @return true if the player has a connected road to this intersection
     */
    boolean playerHasConnectedRoad(Player player);

    /**
     * Returns all Intersection that are adjacent to this intersection.
     *
     * @return all Intersection that are adjacent to this intersection
     */
    Set<Intersection> getAdjacentIntersections();

    /**
     * Returns a set of all adjacent Positions
     *
     * @return a set of all adjacent Positions
     */
    Set<TilePosition> getAdjacentTilePositions();

    /**
     * Returns a set of all adjacent Tiles
     *
     * @return a set of all adjacent Tiles
     */
    default Set<Tile> getAdjacentTiles() {
        return getHexGrid().getTiles()
            .entrySet()
            .stream()
            .filter(entrySet -> getAdjacentTilePositions().contains(entrySet.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());
    }

    /**
     * Checks whether this intersection is connected to all given positions
     *
     * @param position the positions to check
     * @return whether all positions are connected
     */
    boolean isConnectedTo(TilePosition... position);
}
