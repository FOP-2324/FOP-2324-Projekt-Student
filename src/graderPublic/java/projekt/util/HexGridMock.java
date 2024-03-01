package projekt.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import projekt.model.HexGrid;
import projekt.model.Intersection;
import projekt.model.Player;
import projekt.model.TilePosition;
import projekt.model.buildings.Edge;
import projekt.model.tiles.Tile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class HexGridMock implements MockClass<HexGrid>, HexGrid {

    private final HexGrid delegate;
    private Predicate<String> useDelegate;
    private BiFunction<String, Object[], ?> methodAction;

    public HexGridMock(HexGrid delegate) {
        this(delegate, s -> true, (methodName, params) -> null);
    }

    public HexGridMock(HexGrid delegate, Predicate<String> useDelegate, BiFunction<String, Object[], ?> methodAction) {
        this.delegate = delegate;
        this.useDelegate = useDelegate;
        this.methodAction = methodAction;
    }

    @Override
    public HexGrid getDelegate() {
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
    public double getTileWidth() {
        if (useDelegate.test("getTileWidth")) {
            return delegate.getTileWidth();
        } else {
            return (double) methodAction.apply("getTileWidth", new Object[] {this});
        }
    }

    @Override
    public double getTileHeight() {
        if (useDelegate.test("getTileWidth")) {
            return delegate.getTileWidth();
        } else {
            return (double) methodAction.apply("getTileWidth", new Object[] {this});
        }
    }

    @Override
    public double getTileSize() {
        if (useDelegate.test("getTileWidth")) {
            return delegate.getTileWidth();
        } else {
            return (double) methodAction.apply("getTileWidth", new Object[] {this});
        }
    }

    @Override
    public ObservableDoubleValue tileWidthProperty() {
        if (useDelegate.test("tileWidthProperty")) {
            return delegate.tileWidthProperty();
        } else {
            return (ObservableDoubleValue) methodAction.apply("tileWidthProperty", new Object[] {this});
        }
    }

    @Override
    public ObservableDoubleValue tileHeightProperty() {
        if (useDelegate.test("tileHeightProperty")) {
            return delegate.tileHeightProperty();
        } else {
            return (ObservableDoubleValue) methodAction.apply("tileHeightProperty", new Object[] {this});
        }
    }

    @Override
    public DoubleProperty tileSizeProperty() {
        if (useDelegate.test("tileSizeProperty")) {
            return delegate.tileSizeProperty();
        } else {
            return (DoubleProperty) methodAction.apply("tileSizeProperty", new Object[] {this});
        }
    }

    @Override
    public Map<TilePosition, Tile> getTiles() {
        if (useDelegate.test("getTiles")) {
            return delegate.getTiles();
        } else {
            return (Map<TilePosition, Tile>) methodAction.apply("getTiles", new Object[] {this});
        }
    }

    @Override
    public Set<Tile> getTiles(int diceRoll) {
        if (useDelegate.test("getTiles")) {
            return delegate.getTiles(diceRoll);
        } else {
            return (Set<Tile>) methodAction.apply("getTiles", new Object[] {this, diceRoll});
        }
    }

    @Override
    public Tile getTileAt(int q, int r) {
        if (useDelegate.test("getTileAt")) {
            return delegate.getTileAt(q, r);
        } else {
            return (Tile) methodAction.apply("getTileAt", new Object[] {this, q, r});
        }
    }

    @Override
    public Tile getTileAt(TilePosition position) {
        if (useDelegate.test("getTileAt")) {
            return delegate.getTileAt(position);
        } else {
            return (Tile) methodAction.apply("getTileAt", new Object[] {this, position});
        }
    }

    @Override
    public Map<Set<TilePosition>, Intersection> getIntersections() {
        if (useDelegate.test("getIntersections")) {
            return delegate.getIntersections();
        } else {
            return (Map<Set<TilePosition>, Intersection>) methodAction.apply("getIntersections", new Object[] {this});
        }
    }

    @Override
    public Intersection getIntersectionAt(TilePosition position0, TilePosition position1, TilePosition position2) {
        if (useDelegate.test("getIntersectionAt")) {
            return delegate.getIntersectionAt(position0, position1, position2);
        } else {
            return (Intersection) methodAction.apply("getIntersectionAt", new Object[] {this, position0, position1, position2});
        }
    }

    @Override
    public Map<Set<TilePosition>, Edge> getEdges() {
        if (useDelegate.test("getEdges")) {
            return delegate.getEdges();
        } else {
            return (Map<Set<TilePosition>, Edge>) methodAction.apply("getEdges", new Object[] {this});
        }
    }

    @Override
    public Edge getEdge(TilePosition position0, TilePosition position1) {
        if (useDelegate.test("getEdge")) {
            return delegate.getEdge(position0, position1);
        } else {
            return (Edge) methodAction.apply("getEdge", new Object[] {this, position0, position1});
        }
    }

    @Override
    public Map<Set<TilePosition>, Edge> getRoads(Player player) {
        if (useDelegate.test("getRoads")) {
            return delegate.getRoads(player);
        } else {
            return (Map<Set<TilePosition>, Edge>) methodAction.apply("getRoads", new Object[] {this, player});
        }
    }

    @Override
    public List<Edge> getLongestRoad(Player player) {
        if (useDelegate.test("getLongestRoad")) {
            return delegate.getLongestRoad(player);
        } else {
            return (List<Edge>) methodAction.apply("getLongestRoad", new Object[] {this, player});
        }
    }

    @Override
    public boolean addRoad(TilePosition position0, TilePosition position1, Player player, boolean checkVillages) {
        if (useDelegate.test("addRoad")) {
            return delegate.addRoad(position0, position1, player, checkVillages);
        } else {
            return (boolean) methodAction.apply("addRoad", new Object[] {this, position0, position1, player, checkVillages});
        }
    }

    @Override
    public boolean removeRoad(TilePosition position0, TilePosition position1) {
        if (useDelegate.test("removeRoad")) {
            return delegate.removeRoad(position0, position1);
        } else {
            return (boolean) methodAction.apply("removeRoad", new Object[] {this, position0, position1});
        }
    }

    @Override
    public TilePosition getRobberPosition() {
        if (useDelegate.test("getRobberPosition")) {
            return delegate.getRobberPosition();
        } else {
            return (TilePosition) methodAction.apply("getRobberPosition", new Object[] {this});
        }
    }

    @Override
    public void setRobberPosition(TilePosition position) {
        if (useDelegate.test("setRobberPosition")) {
            delegate.setRobberPosition(position);
        } else {
            methodAction.apply("setRobberPosition", new Object[] {this, position});
        }
    }
}
