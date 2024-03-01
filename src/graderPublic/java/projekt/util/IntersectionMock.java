package projekt.util;

import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.TilePosition;
import projekt.model.buildings.Edge;
import projekt.model.buildings.Port;
import projekt.model.buildings.Settlement;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class IntersectionMock implements MockClass<Intersection>, Intersection {

    private final Intersection delegate;
    private Predicate<String> useDelegate;
    private BiFunction<String, Object[], ?> methodAction;

    public IntersectionMock(Intersection delegate) {
        this(delegate, s -> true, (methodName, params) -> null);
    }

    public IntersectionMock(Intersection delegate, Predicate<String> useDelegate, BiFunction<String, Object[], ?> methodAction) {
        this.delegate = delegate;
        this.useDelegate = useDelegate;
        this.methodAction = methodAction;
    }

    @Override
    public Intersection getDelegate() {
        return delegate;
    }

    @Override
    public Predicate<String> getUseDelegate() {
        return useDelegate;
    }

    @Override
    public void setUseDelegate(Predicate<String> useDelegate) {
        this.useDelegate = useDelegate;
    }

    @Override
    public BiFunction<String, Object[], ?> getMethodAction() {
        return methodAction;
    }

    @Override
    public void setMethodAction(BiFunction<String, Object[], ?> methodAction) {
        this.methodAction = methodAction;
    }

    @Override
    public HexGrid getHexGrid() {
        if (useDelegate.test("getHexGrid")) {
            return delegate.getHexGrid();
        } else {
            return (HexGrid) methodAction.apply("getHexGrid", new Object[] {this});
        }
    }

    @Override
    public Settlement getSettlement() {
        if (useDelegate.test("getSettlement")) {
            return delegate.getSettlement();
        } else {
            return (Settlement) methodAction.apply("getSettlement", new Object[] {this});
        }
    }

    @Override
    public boolean hasSettlement() {
        if (useDelegate.test("hasSettlement")) {
            return delegate.hasSettlement();
        } else {
            return (boolean) methodAction.apply("hasSettlement", new Object[] {this});
        }
    }

    @Override
    public boolean playerHasSettlement(Player player) {
        if (useDelegate.test("playerHasSettlement")) {
            return delegate.playerHasSettlement(player);
        } else {
            return (boolean) methodAction.apply("playerHasSettlement", new Object[] {this, player});
        }
    }

    @Override
    public boolean placeVillage(Player player, boolean ignoreRoadCheck) {
        if (useDelegate.test("placeVillage")) {
            return delegate.placeVillage(player, ignoreRoadCheck);
        } else {
            return (boolean) methodAction.apply("placeVillage", new Object[] {this, player, ignoreRoadCheck});
        }
    }

    @Override
    public boolean upgradeSettlement(Player player) {
        if (useDelegate.test("upgradeSettlement")) {
            return delegate.upgradeSettlement(player);
        } else {
            return (boolean) methodAction.apply("upgradeSettlement", new Object[] {this, player});
        }
    }

    @Override
    public Port getPort() {
        if (useDelegate.test("getPort")) {
            return delegate.getPort();
        } else {
            return (Port) methodAction.apply("getPort", new Object[] {this});
        }
    }

    @Override
    public Set<Edge> getConnectedEdges() {
        if (useDelegate.test("getConnectedEdges")) {
            return delegate.getConnectedEdges();
        } else {
            return (Set<Edge>) methodAction.apply("getConnectedEdges", new Object[] {this});
        }
    }

    @Override
    public boolean playerHasConnectedRoad(Player player) {
        if (useDelegate.test("playerHasConnectedRoad")) {
            return delegate.playerHasConnectedRoad(player);
        } else {
            return (boolean) methodAction.apply("playerHasConnectedRoad", new Object[] {this, player});
        }
    }

    @Override
    public Set<Intersection> getAdjacentIntersections() {
        if (useDelegate.test("getAdjacentIntersections")) {
            return delegate.getAdjacentIntersections();
        } else {
            return (Set<Intersection>) methodAction.apply("getAdjacentIntersections", new Object[] {this});
        }
    }

    @Override
    public Set<TilePosition> getAdjacentTilePositions() {
        if (useDelegate.test("getAdjacentTilePositions")) {
            return delegate.getAdjacentTilePositions();
        } else {
            return (Set<TilePosition>) methodAction.apply("getAdjacentTilePositions", new Object[] {this});
        }
    }

    @Override
    public boolean isConnectedTo(TilePosition... position) {
        if (useDelegate.test("isConnectedTo")) {
            return delegate.isConnectedTo(position);
        } else {
            return (boolean) methodAction.apply("isConnectedTo", new Object[] {this, position});
        }
    }
}
