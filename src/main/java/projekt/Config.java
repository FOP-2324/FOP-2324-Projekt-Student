package projekt;

import org.tudalgo.algoutils.student.io.PropertyUtils;
import projekt.model.DevelopmentCardType;
import projekt.model.ResourceType;
import projekt.model.TilePosition;
import projekt.model.buildings.Port;
import projekt.model.buildings.Settlement;
import projekt.model.tiles.Tile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

/**
 * Global configuration; mainly for {@link projekt.model.HexGrid} implementations and the {@link projekt.controller controller}.
 */
public final class Config {

    // Properties and global stuff

    /**
     * The properties file containing the ratio of each tile type.
     *
     * @see #TILE_RATIOS
     */
    private static final Properties TILE_RATIO_PROPERTIES = PropertyUtils.getProperties("tile_ratios.properties");

    /**
     * The properties file containing the ratio of each development card type.
     *
     * @see #DEVELOPMENT_CARD_RATIOS
     */
    private static final Properties DEVELOPMENT_CARD_RATIO_PROPERTIES = PropertyUtils.getProperties("development_card_ratios.properties");

    /**
     * The global source of randomness.
     */
    public static final Random RANDOM = new Random();

    /**
     * The minimum required number of players in a game.
     */
    public static final int MIN_PLAYERS = 2;

    /**
     * The maximum allowed number of players in a game.
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * How many victory points a player must have to win.
     */
    public static final int REQUIRED_VICTORY_POINTS = 10;

    /**
     * The number of dice rolled each round.
     */
    public static final int NUMBER_OF_DICE = 2;

    /**
     * The number of sides on each die.
     */
    public static final int DICE_SIDES = 6;

    /**
     * The radius of the grid, center is included.
     */
    public static final int GRID_RADIUS = 3;


    // Roads and settlements

    /**
     * Maximum amount of roads a player can place / own.
     */
    public static final int MAX_ROADS = 15;

    /**
     * The amount of resources needed to build a road.
     */
    public static final Map<ResourceType, Integer> ROAD_BUILDING_COST = Map.of(
        ResourceType.WOOD, 1,
        ResourceType.CLAY, 1
    );

    /**
     * Maximum amount of villages a player can place / own.
     */
    public static final int MAX_VILLAGES = 5;

    /**
     * Maximum amount of cities a player can place / own.
     */
    public static final int MAX_CITIES = 4;

    /**
     * The amount of resources needed to build each settlement type.
     */
    public static final Map<Settlement.Type, Map<ResourceType, Integer>> SETTLEMENT_BUILDING_COST = Map.of(
        Settlement.Type.VILLAGE, Map.of(
            ResourceType.WOOD, 1,
            ResourceType.CLAY, 1,
            ResourceType.GRAIN, 1,
            ResourceType.WOOL, 1
        ),
        Settlement.Type.CITY, Map.of(
            ResourceType.GRAIN, 2,
            ResourceType.ORE, 3
        )
    );


    // Tiles

    /**
     * The ratio of each {@link projekt.model.tiles.Tile.Type} to the total amount of tiles in the grid.
     */
    public static final SortedMap<Tile.Type, Integer> TILE_RATIOS = Collections.unmodifiableSortedMap(new TreeMap<>() {{
        for (final Tile.Type tileType : Tile.Type.values()) {
            put(tileType, Integer.parseInt(TILE_RATIO_PROPERTIES.getProperty(tileType.name(), "0")));
        }
    }});

    /**
     * Create a new generator for tile types.
     * The supplier returned by this method returns a randomly picked
     * tile type from an "endless stack" of {@link Tile.Type}.
     * The probability of a tile type to be picked is the same as defined by the rules of the base game.
     *
     * @return A supplier returning randomly picked tile types
     * @see #makeSupplier(SortedMap, boolean)
     */
    public static Supplier<Tile.Type> generateTileTypes() {
        return makeSupplier(TILE_RATIOS, true);
    }

    /**
     * Creates a new supplier returning randomly picked roll numbers.
     * Roll numbers range from 2 to 12 (both inclusive), excluding 7.
     * The probability of a number to be picked is about the same
     * as defined by the rules of the base game.
     *
     * @return A supplier returning randomly picked roll numbers
     * @see #makeSupplier(SortedMap, boolean)
     */
    public static Supplier<Integer> generateRollNumbers() {
        final Map<Integer, Integer> ratios = IntStream.iterate(NUMBER_OF_DICE, i -> i >= NUMBER_OF_DICE && i <= NUMBER_OF_DICE * DICE_SIDES, i -> i + 1)
            .filter(i -> i != 7)
            .mapToObj(i -> Map.entry(i, i == NUMBER_OF_DICE || i == NUMBER_OF_DICE * DICE_SIDES ? 1 : 2))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        return makeSupplier(new TreeMap<>(ratios), true);
    }

    /**
     * Creates a BiFunction that takes a tile position and an edge direction and returns
     * a port if all conditions and the probability (65%) requirement are met.
     * The conditions are as follows:
     * <ul>
     *     <li>The tile position is on the edge of the grid</li>
     *     <li>The edge direction is pointing outwards, away from the center</li>
     *     <li>There are no other surrounding ports for at least one intersection</li>
     * </ul>
     *
     * @return the BiFunction
     * @see TilePosition
     */
    public static BiFunction<TilePosition, TilePosition.EdgeDirection, Port> generatePortMapper() {
        final Iterator<ResourceType> resourceTypes = Spliterators.iterator(Arrays.spliterator(ResourceType.values()));
        final Set<Set<TilePosition>> visitedIntersections = new HashSet<>();
        final Predicate<TilePosition> isOutsideGrid = tilePosition -> abs(tilePosition.q()) >= GRID_RADIUS
            || abs(tilePosition.r()) >= GRID_RADIUS
            || abs(tilePosition.s()) >= GRID_RADIUS;
        final Predicate<TilePosition> isOnEdge = tilePosition -> !(
            abs(tilePosition.q()) < GRID_RADIUS - 1
                && abs(tilePosition.r()) < GRID_RADIUS - 1
                && abs(tilePosition.s()) < GRID_RADIUS - 1
        )
            && !isOutsideGrid.test(tilePosition);
        final BiFunction<TilePosition, TilePosition.EdgeDirection, Set<Set<TilePosition>>> mapToIntersectionsPositions =
            (tilePosition, edgeDirection) -> Set.of(
                Set.of(tilePosition, TilePosition.add(tilePosition, edgeDirection.position), TilePosition.add(tilePosition, edgeDirection.left().position)),
                Set.of(tilePosition, TilePosition.add(tilePosition, edgeDirection.position), TilePosition.add(tilePosition, edgeDirection.right().position))
            );

        return (tilePosition, edgeDirection) -> {
            final Set<Set<TilePosition>> intersectionPositions = mapToIntersectionsPositions.apply(tilePosition, edgeDirection);
            if (!isOnEdge.test(tilePosition)
                || !isOutsideGrid.test(TilePosition.add(tilePosition, edgeDirection.position))
                || intersectionPositions.stream().anyMatch(visitedIntersections::contains)) {
                return null;
            }

            if (RANDOM.nextDouble() < 0.65) {  // place port?
                visitedIntersections.addAll(intersectionPositions);
                if (resourceTypes.hasNext() && RANDOM.nextBoolean()) { // place specialized port?
                    return new Port(2, resourceTypes.next());
                } else {
                    return new Port(3);
                }
            } else {
                return null;
            }
        };
    }


    // Development cards

    /**
     * The cost to buy a single development card of any type
     */
    public static final Map<ResourceType, Integer> DEVELOPMENT_CARD_COST = Map.of(
        ResourceType.GRAIN, 1,
        ResourceType.WOOL, 1,
        ResourceType.ORE, 1
    );

    /**
     * The ratio / frequency of occurrence of each {@link projekt.model.DevelopmentCardType}.
     */
    public static final SortedMap<DevelopmentCardType, Integer> DEVELOPMENT_CARD_RATIOS = Collections.unmodifiableSortedMap(new TreeMap<>() {{
        for (final DevelopmentCardType developmentCardType : DevelopmentCardType.values()) {
            put(developmentCardType, Integer.parseInt(DEVELOPMENT_CARD_RATIO_PROPERTIES.getProperty(developmentCardType.name(), "0")));
        }
    }});

    /**
     * Create a new generator for development cards.
     * The supplier returned by this method returns a randomly picked
     * development card from an "endless stack" of {@link DevelopmentCardType}.
     * The probability of a card to be picked is the same as defined by the rules of the base game.
     *
     * @return A supplier returning randomly picked development cards
     * @see #makeSupplier(SortedMap, boolean)
     */
    public static Supplier<DevelopmentCardType> developmentCardGenerator() {
        return makeSupplier(DEVELOPMENT_CARD_RATIOS, false);
    }


    // Misc

    /**
     * Creates a supplier for the keys of the given map depending on the key's mapping (ratio).
     * Optionally, the supplier can log the keys it returned to ensure that their frequency of occurrence
     * is not warped too much, even if the law of large numbers does not apply.
     *
     * @param ratios        mappings of keys to their respective ratio
     * @param enableCounter whether to enable the counter / log
     * @return a supplier returning chosen keys
     */
    private static <T> Supplier<T> makeSupplier(final SortedMap<T, Integer> ratios, final boolean enableCounter) {
        final Map<T, Integer> counter = new HashMap<>();
        final int sum = ratios.values().stream().mapToInt(i -> i).sum();
        return () -> {
            T result = null;
            while (result == null || (enableCounter && counter.getOrDefault(result, 0) >= ratios.get(result))) {
                if (enableCounter && counter.equals(ratios)) {
                    counter.clear();
                }
                final int d = RANDOM.nextInt(sum);
                int start = 0;
                int bound = 0;

                for (final Map.Entry<T, Integer> entry : ratios.entrySet()) {
                    final int ratio = entry.getValue();
                    bound += ratio;
                    if (d >= start && d < bound) {
                        result = entry.getKey();
                        break;
                    }
                    start += ratio;
                }
            }
            if (enableCounter) {
                counter.merge(result, 1, (oldValue, value) -> oldValue + 1);
            }
            return result;
        };
    }
}
