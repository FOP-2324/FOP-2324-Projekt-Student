package projekt.model;

import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Settlement;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a player in the game.
 * Players hold information on...
 * <ul>
 *     <li>the grid they are playing on</li>
 *     <li>victory points</li>
 *     <li>resources and their amounts</li>
 *     <li>roads and settlements</li>
 *     <li>development cards and their amounts</li>
 *     <li>miscellaneous, such as color, name, ID, etc.</li>
 * </ul>
 */
@DoNotTouch
public interface Player {

    /**
     * Returns the hexGrid instance
     *
     * @return the hexGrid instance
     */
    HexGrid getHexGrid();

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    String getName();

    /**
     * Returns the Player ID, aka the Index of the Player, starting with 1
     *
     * @return the Player ID
     */
    int getID();

    /**
     * Returns the color of the player.
     *
     * @return the color of the player
     */
    Color getColor();

    /**
     * Returns true if the player is an AI, false otherwise.
     *
     * @return true if the player is an AI, false otherwise
     */
    default boolean isAi() {
        return false;
    }

    /**
     * Returns the amount of victory points from settlements and development cards
     * the player has.
     *
     * @return the amount of victory points from settlements and development cards
     * the player has.
     */
    int getVictoryPoints();

    /**
     * Returns an immutable map of all resources the player currently has and how
     * many of each.
     *
     * @return an immutable map of all resources the player currently has and how
     * many of each.
     */
    Map<ResourceType, Integer> getResources();

    /**
     * Adds the given amount of the given resource to the player.
     * Expects a positive amount.
     *
     * @param resourceType the ResourceType to add
     * @param amount       the amount to add
     */
    void addResource(ResourceType resourceType, int amount);

    /**
     * Adds the given resources to the player.
     * Expects positive amounts.
     *
     * @param resources a mapping of resources to their amounts
     */
    void addResources(Map<ResourceType, Integer> resources);

    /**
     * Returns true if the player has at least the given amount of the resource.
     * Returns false otherwise.
     *
     * @param resources a mapping of resources to their amounts to check
     * @return true if the player has at least the given amount of the resource,
     * false otherwise
     */
    boolean hasResources(Map<ResourceType, Integer> resources);

    /**
     * Removes the given amount of the given resource from the player.
     * Expects a positive amount.
     * Checks if the player has enough resources to remove.
     * If the player does not have enough resources, nothing is removed.
     * Ensures the player never has negative resources.
     *
     * @param resourceType the ResourceType to remove from
     * @param amount       the amount to remove
     * @return true if the player had enough resources to remove, false otherwise
     */
    boolean removeResource(ResourceType resourceType, int amount);

    /**
     * Removes the given resources from the player.
     * Expects positive amounts.
     * Checks if the player has enough resources to remove.
     * If the player does not have enough resources, nothing is removed.
     * Ensures the player never has negative resources.
     *
     * @param resources a mapping of resources to their amounts to remove
     * @return true if the player had enough resources to remove, false otherwise
     */
    boolean removeResources(Map<ResourceType, Integer> resources);

    /**
     * Returns the ratio the player can trade the given resource for with the bank.
     *
     * @param resourceType the resource to trade
     * @return the ratio the player can trade the given resource for with the bank
     */
    int getTradeRatio(ResourceType resourceType);

    /**
     * Returns the amount of roads the player can still build.
     *
     * @return the amount of roads the player can still build
     */
    int getRemainingRoads();

    /**
     * Returns the amount of villages the player can still build.
     *
     * @return the amount of villages the player can still build
     */
    int getRemainingVillages();

    /**
     * Returns the amount of cities the player can still build.
     *
     * @return the amount of cities the player can still build
     */
    int getRemainingCities();

    /**
     * Returns all roads the player currently has.
     *
     * @return all roads the player currently has
     */
    default Map<Set<TilePosition>, Edge> getRoads() {
        return getHexGrid().getRoads(this);
    }

    /**
     * Returns all settlements the player currently has.
     *
     * @return all settlements the player currently has
     */
    default Set<Settlement> getSettlements() {
        return getHexGrid().getIntersections()
            .values()
            .stream()
            .map(Intersection::getSettlement)
            .filter(settlement -> settlement != null && settlement.owner().equals(this))
            .collect(Collectors.toSet());
    }

    /**
     * Returns an unmodifiable map of all development cards the player currently has
     * and how many.
     *
     * @return an unmodifiable map of all development cards the player currently has
     * and how many.
     */
    Map<DevelopmentCardType, Integer> getDevelopmentCards();

    /**
     * Adds the given development card to the player.
     *
     * @param developmentCardType the development card to add
     */
    void addDevelopmentCard(DevelopmentCardType developmentCardType);

    /**
     * Removes the given development card from the player.
     *
     * @param developmentCardType the development card to remove
     * @return true if the card was removed, false otherwise
     */
    boolean removeDevelopmentCard(DevelopmentCardType developmentCardType);

    /**
     * Returns the total amount of development cards the player has.
     *
     * @return the total amount of development cards the player has
     */
    int getTotalDevelopmentCards();

    /**
     * Returns the amount of {@linkplain DevelopmentCardType#KNIGHT knights} the
     * player has played.
     *
     * @return the amount of {@linkplain DevelopmentCardType#KNIGHT knights} the
     * player has played
     */
    int getKnightsPlayed();
}
