package projekt.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Settlement;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class HexGridImplTest {

    private HexGrid hexGrid;
    private Player player;

    @BeforeEach
    public void setup() {
        setup(1);
    }

    private void setup(int radius) {
        hexGrid = new HexGridImpl(radius);
        player = new PlayerImpl.Builder(0).build(hexGrid);
    }

    @ParameterizedTest
    @JsonParameterSetTest("/model/HexGridImpl/roads.json")
    public void testGetRoads(JsonParameterSet params) {
        List<Set<TilePosition>> roads = parseRoads(params);
        Map<Set<TilePosition>, Edge> expected = hexGrid.getEdges()
            .entrySet()
            .stream()
            .filter(entry -> roads.contains(entry.getKey()))
            .peek(entry -> entry.getValue().getRoadOwnerProperty().setValue(player))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Context context = contextBuilder()
            .add("player", player)
            .add("owned road positions", roads)
            .build();

        assertCallEquals(expected, () -> hexGrid.getRoads(player), context, result ->
            "The return value of getRoads(Player) did not match the expected one");
    }

    @Disabled
    @ParameterizedTest
    @JsonParameterSetTest("/model/HexGridImpl/roads.json")
    public void testGetLongestRoad(JsonParameterSet params) {
        List<Set<TilePosition>> roads = parseRoads(params);
        List<Edge> expected = hexGrid.getEdges()
            .entrySet()
            .stream()
            .filter(entry -> roads.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .peek(edge -> edge.getRoadOwnerProperty().setValue(player))
            .toList();
        Context context = contextBuilder()
            .add("player", player)
            .add("owned road positions", roads)
            .build();

        List<Edge> actual = assertCallNotNull(() -> hexGrid.getLongestRoad(player), context, result ->
            "An exception occurred while invoking HexGridImpl.getLongestRoad(Player) or return value is null");
        assertEquals(expected.size(), actual.size(), context, result ->
            "The list returned by getLongestRoad(Player) does not have the expected size");
        assertTrue(actual.containsAll(expected), context, result ->
            "The list returned by getLongestRoad(Player) does not contain all expected elements");
    }

    @Test
//    @JsonParameterSetTest("/HexGridImpl/roads.json")
    public void testAddRoad() throws ReflectiveOperationException {
//        List<List<TilePosition>> roads = parseRoads(params).stream()
//            .map(List::copyOf)
//            .toList();
        List<List<TilePosition>> roads = List.of(
            List.of(new TilePosition(0, 0), new TilePosition(0, 1)),
            List.of(new TilePosition(0, 0), new TilePosition(0, -1))
        );

        for (boolean checkVillages : new boolean[] {true, false}) {
            setup();

            for (List<TilePosition> road : roads) {
                Context context = contextBuilder()
                    .add("position0", road.get(0))
                    .add("position1", road.get(1))
                    .add("player", player)
                    .add("checkVillages", checkVillages)
                    .build();
                assertCallFalse(() -> hexGrid.addRoad(road.get(0), road.get(1), player, checkVillages), context, result ->
                    "The return value of addRoad is incorrect");

                Intersection intersection = hexGrid.getEdge(road.get(0), road.get(1)).getIntersections().iterator().next();
                Field settlementField = IntersectionImpl.class.getDeclaredField("settlement");
                settlementField.trySetAccessible();
                settlementField.set(intersection, new Settlement(player, Settlement.Type.VILLAGE, intersection));
                context = contextBuilder()
                    .add(context)
                    .add("owned village", intersection.getAdjacentTilePositions())
                    .build();
                assertCallEquals(checkVillages, () -> hexGrid.addRoad(road.get(0), road.get(1), player, checkVillages), context, result ->
                    "The return value of addRoad is incorrect");
                if (checkVillages) {
                    assertEquals(player, hexGrid.getEdge(road.get(0), road.get(1)).getRoadOwner(), context, result ->
                        "The added road is not owned by the expected player");
                }

                Edge ownedRoad = intersection.getConnectedEdges()
                    .stream()
                    .filter(edge -> !edge.getAdjacentTilePositions().equals(Set.of(road.get(0), road.get(1))))
                    .findAny()
                    .get();
                ownedRoad.getRoadOwnerProperty().setValue(player);
                context = contextBuilder()
                    .add(context)
                    .add("owned road", ownedRoad)
                    .build();
                assertCallEquals(!checkVillages, () -> hexGrid.addRoad(road.get(0), road.get(1), player, checkVillages), context, result ->
                    "The return value of addRoad is incorrect");
                if (!checkVillages) {
                    assertEquals(player, hexGrid.getEdge(road.get(0), road.get(1)).getRoadOwner(), context, result ->
                        "The added road is not owned by the expected player");
                }
            }
        }
    }

    @Test
    public void testAddRoadThrows() {
        setup(2);
        List<TilePosition> tilePositions = List.of(new TilePosition(100, 100), new TilePosition(100, 101));
        Context context = contextBuilder()
            .add("tile positions", tilePositions)
            .add("player", player)
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> hexGrid.addRoad(tilePositions.get(0), tilePositions.get(1), player, false),
            contextBuilder().add(context).add("checkVillages", false).build(),
            result -> "Expected IllegalArgumentException to be thrown");
        assertThrows(IllegalArgumentException.class,
            () -> hexGrid.addRoad(tilePositions.get(0), tilePositions.get(1), player, true),
            contextBuilder().add(context).add("checkVillages", true).build(),
            result -> "Expected IllegalArgumentException to be thrown");
    }

    private static List<Set<TilePosition>> parseRoads(JsonParameterSet params) {
        return params.<List<Map<String, Map<String, Integer>>>>get("pairs")
            .stream()
            .map(pair -> pair.values()
                .stream()
                .map(tilePosition -> new TilePosition(tilePosition.get("q"), tilePosition.get("r")))
                .collect(Collectors.toSet()))
            .toList();
    }
}
