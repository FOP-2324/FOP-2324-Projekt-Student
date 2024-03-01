package projekt.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import projekt.model.buildings.Edge;
import projekt.model.buildings.EdgeImpl;
import projekt.model.buildings.Port;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class EdgeImplTest {

    private HexGrid hexGrid;

    @BeforeEach
    public void setup() {
        hexGrid = new HexGridImpl(1);
    }

    @ParameterizedTest
    @JsonParameterSetTest("/model/EdgeImpl/edges.json")
    public void testGetIntersections(JsonParameterSet params) {
        List<TilePosition> tilePositions = getTilePositions(params, "edge");
        EdgeImpl instance = new EdgeImpl(hexGrid, tilePositions.get(0), tilePositions.get(1), new SimpleObjectProperty<>(), null);
        Context context = contextBuilder()
            .add("edge", instance)
            .build();

        Set<Set<TilePosition>> expected = Set.of(
            Set.copyOf(getTilePositions(params, "leftIntersection")),
            Set.copyOf(getTilePositions(params, "rightIntersection")));
        Set<Set<TilePosition>> actual = callObject(instance::getIntersections, context, result ->
            "An exception occurred while invoking EdgeImpl.getIntersections")
            .stream()
            .map(Intersection::getAdjacentTilePositions)
            .collect(Collectors.toSet());
        assertEquals(expected, actual, context, result ->
            "The tile positions of the returned intersections differ from the expected values");
    }

    @ParameterizedTest
    @JsonParameterSetTest("/model/EdgeImpl/edges.json")
    public void testConnectsTo(JsonParameterSet params) {
        List<TilePosition> tilePositions = getTilePositions(params, "edge");
        EdgeImpl instance = new EdgeImpl(hexGrid, tilePositions.get(0), tilePositions.get(1), new SimpleObjectProperty<>(), null);
        EdgeMock otherEdge = new EdgeMock(instance, params);
        Context context = contextBuilder()
            .add("edge", instance)
            .add("other edge (parameter)", otherEdge)
            .build();

        assertEquals(otherEdge.connectsToOtherEdge,
            callObject(() -> instance.connectsTo(otherEdge), context, result ->
                "An exception occurred while invoking EdgeImpl.connectsTo"),
            context,
            result -> "The returned value does not match the expected one");
    }

    @ParameterizedTest
    @JsonParameterSetTest("/model/EdgeImpl/edges.json")
    public void testGetConnectedRoads(JsonParameterSet params) {
        List<TilePosition> tilePositions = getTilePositions(params, "edge");
        EdgeImpl instance = new EdgeImpl(hexGrid, tilePositions.get(0), tilePositions.get(1), new SimpleObjectProperty<>(), null);
        Map<Set<TilePosition>, Edge> edges = hexGrid.getEdges();
        Player player = new PlayerImpl.Builder(0).build(hexGrid);
        Set<Set<TilePosition>> roads = params.<List<List<Map<String, Integer>>>>get("roads")
            .stream()
            .map(EdgeImplTest::getTilePositions)
            .map(Set::copyOf)
            .collect(Collectors.toSet());
        roads.stream()
            .map(edges::get)
            .forEach(edge -> edge.getRoadOwnerProperty().setValue(player));
        Context context = contextBuilder()
            .add("edge", instance)
            .add("player", player)
            .add("roads owned by " + player, roads)
            .build();

        assertEquals(roads,
            callObject(() -> instance.getConnectedRoads(player), context, result ->
                "An exception occurred while invoking EdgeImpl.getConnectedRoads")
                .stream()
                .map(Edge::getAdjacentTilePositions)
                .collect(Collectors.toSet()),
            context,
            result -> "The tile positions of the returned edges do not match the expected values");
    }

    private static List<TilePosition> getTilePositions(JsonParameterSet params, String key) {
        return getTilePositions(params.get(key));
    }

    private static List<TilePosition> getTilePositions(List<Map<String, Integer>> serializedTilePositions) {
        return serializedTilePositions.stream()
            .map(map -> new TilePosition(map.get("q"), map.get("r")))
            .toList();
    }

    private class EdgeMock implements Edge {

        private final EdgeImpl edgeImplInstance;
        private final List<TilePosition> tilePositions;
        private final Set<TilePosition> leftIntersection;
        private final Set<TilePosition> rightIntersection;
        private final boolean connectsToOtherEdge;

        @SuppressWarnings("unchecked")
        private EdgeMock(EdgeImpl edgeImplInstance, JsonParameterSet params) {
            Map<String, ?> otherEdge = params.get("otherEdge");

            this.edgeImplInstance = edgeImplInstance;
            this.tilePositions = getTilePositions((List<Map<String, Integer>>) otherEdge.get("edge"));
            this.leftIntersection = Set.copyOf(getTilePositions((List<Map<String, Integer>>) otherEdge.get("leftIntersection")));
            this.rightIntersection = Set.copyOf(getTilePositions((List<Map<String, Integer>>) otherEdge.get("rightIntersection")));
            this.connectsToOtherEdge = (boolean) otherEdge.get("connectsToOtherEdge");
        }

        @Override
        public HexGrid getHexGrid() {
            return hexGrid;
        }

        @Override
        public TilePosition getPosition1() {
            return tilePositions.get(0);
        }

        @Override
        public TilePosition getPosition2() {
            return tilePositions.get(1);
        }

        @Override
        public boolean hasPort() {
            return false;
        }

        @Override
        public Port getPort() {
            return null;
        }

        @Override
        public boolean connectsTo(Edge other) {
            return connectsToOtherEdge && other.getAdjacentTilePositions().equals(edgeImplInstance.getAdjacentTilePositions());
        }

        @Override
        public Set<Intersection> getIntersections() {
            Map<Set<TilePosition>, Intersection> intersections = hexGrid.getIntersections();

            return Stream.of(leftIntersection, rightIntersection)
                .map(intersections::get)
                .collect(Collectors.toSet());
        }

        @Override
        public Property<Player> getRoadOwnerProperty() {
            return new SimpleObjectProperty<>();
        }

        @Override
        public Set<Edge> getConnectedRoads(Player player) {
            return Collections.emptySet();
        }

        @Override
        public String toString() {
            return "EdgeMock{" +
                "tilePositions=" + tilePositions +
                ", leftIntersection=" + leftIntersection +
                ", rightIntersection=" + rightIntersection +
                ", connectsToOtherEdge=" + connectsToOtherEdge +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge edge)) return false;
            return Set.copyOf(tilePositions).equals(edge.getAdjacentTilePositions());
        }

        @Override
        public int hashCode() {
            return Set.copyOf(tilePositions).hashCode();
        }
    }
}
