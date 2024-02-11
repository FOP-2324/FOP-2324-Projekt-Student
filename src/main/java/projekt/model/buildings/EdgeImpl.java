package projekt.model.buildings;

import javafx.beans.property.Property;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.TilePosition;

import java.util.Set;

/**
 * Default implementation of {@link Edge}.
 *
 * @param grid      the HexGrid instance this edge is placed in
 * @param position1 the first position
 * @param position2 the second position
 * @param roadOwner the road's owner, if a road has been built on this edge
 * @param port      a port this edge provides access to, if any
 */
public record EdgeImpl(
    HexGrid grid, TilePosition position1, TilePosition position2, Property<Player> roadOwner, Port port
) implements Edge {
    @Override
    public HexGrid getHexGrid() {
        return grid;
    }

    @Override
    public TilePosition getPosition1() {
        return position1;
    }

    @Override
    public TilePosition getPosition2() {
        return position2;
    }

    @Override
    public boolean hasPort() {
        return port != null;
    }

    @Override
    public Port getPort() {
        return port;
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public boolean connectsTo(final Edge other) {
        // TODO: H1.3
        return org.tudalgo.algoutils.student.Student.crash("H1.3 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public Set<Intersection> getIntersections() {
        // TODO: H1.3
        return org.tudalgo.algoutils.student.Student.crash("H1.3 - Remove if implemented");
    }

    @Override
    public Property<Player> getRoadOwnerProperty() {
        return roadOwner;
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public Set<Edge> getConnectedRoads(final Player player) {
        // TODO: H1.3
        return org.tudalgo.algoutils.student.Student.crash("H1.3 - Remove if implemented");
    }
}
