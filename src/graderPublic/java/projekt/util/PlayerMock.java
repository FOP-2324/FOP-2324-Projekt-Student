package projekt.util;

import javafx.scene.paint.Color;
import projekt.model.DevelopmentCardType;
import projekt.model.HexGrid;
import projekt.model.Player;
import projekt.model.ResourceType;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class PlayerMock implements MockClass<Player>, Player {

    private final Player delegate;
    private Predicate<String> useDelegate;
    private BiFunction<String, Object[], ?> methodAction;

    public PlayerMock(Player delegate) {
        this(delegate, s -> true, null);
    }

    public PlayerMock(Player delegate, Predicate<String> useDelegate, BiFunction<String, Object[], ?> methodAction) {
        this.delegate = delegate;
        this.useDelegate = useDelegate;
        this.methodAction = methodAction;
    }

    @Override
    public Player getDelegate() {
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
    public HexGrid getHexGrid () {
        if(useDelegate.test("getHexGrid")) {
            return delegate.getHexGrid();
        } else {
            return (HexGrid) methodAction.apply("getHexGrid", new Object[] {this});
        }
    }

    @Override
    public String getName () {
        if(useDelegate.test("getName")) {
            return delegate.getName();
        } else {
            return (String) methodAction.apply("getName", new Object[] {this});
        }
    }

    @Override
    public int getID () {
        if(useDelegate.test("getID")) {
            return delegate.getID();
        } else {
            return (int) methodAction.apply("getID", new Object[] {this});
        }
    }

    @Override
    public Color getColor () {
        if(useDelegate.test("getColor")) {
            return delegate.getColor();
        } else {
            return (Color) methodAction.apply("getColor", new Object[] {this});
        }
    }

    @Override
    public int getVictoryPoints () {
        if(useDelegate.test("getVictoryPoints")) {
            return delegate.getVictoryPoints();
        } else {
            return (int) methodAction.apply("getVictoryPoints", new Object[] {this});
        }
    }

    @Override
    public Map<ResourceType, Integer> getResources () {
        if(useDelegate.test("getResources")) {
            return delegate.getResources();
        } else {
            return (Map<ResourceType, Integer>) methodAction.apply("getResources", new Object[] {this});
        }
    }

    @Override
    public void addResource (ResourceType resourceType, int amount) {
        if(useDelegate.test("addResource")) {
            delegate.addResource(resourceType, amount);
        } else {
            methodAction.apply("addResource", new Object[] {this, resourceType, amount});
        }
    }

    @Override
    public void addResources (Map<ResourceType, Integer> resources) {
        if(useDelegate.test("addResources")) {
            delegate.addResources(resources);
        } else {
            methodAction.apply("addResources", new Object[] {this, resources});
        }
    }

    @Override
    public boolean hasResources (Map<ResourceType, Integer> resources) {
        if(useDelegate.test("hasResources")) {
            return delegate.hasResources(resources);
        } else {
            return (boolean) methodAction.apply("hasResources", new Object[] {this, resources});
        }
    }

    @Override
    public boolean removeResource (ResourceType resourceType, int amount) {
        if(useDelegate.test("removeResource")) {
            return delegate.removeResource(resourceType, amount);
        } else {
            return (boolean) methodAction.apply("removeResource", new Object[] {this, resourceType, amount});
        }
    }

    @Override
    public boolean removeResources (Map<ResourceType, Integer> resources) {
        if(useDelegate.test("removeResources")) {
            return delegate.removeResources(resources);
        } else {
            // WHY DOES A SWITCH STATEMENT RETURN AN OPTIONAL; WTF!??!?
            Object result = methodAction.apply("removeResources", new Object[] {this, resources});
            if (result instanceof Optional<?> optional) {
                return (boolean) optional.get();
            } else {
                return (boolean) result;
            }
        }
    }

    @Override
    public int getTradeRatio (ResourceType resourceType) {
        if(useDelegate.test("getTradeRatio")) {
            return delegate.getTradeRatio(resourceType);
        } else {
            return (int) methodAction.apply("getTradeRatio", new Object[] {this, resourceType});
        }
    }

    @Override
    public int getRemainingRoads () {
        if(useDelegate.test("getRemainingRoads")) {
            return delegate.getRemainingRoads();
        } else {
            return (int) methodAction.apply("getRemainingRoads", new Object[] {this});
        }
    }

    @Override
    public int getRemainingVillages () {
        if(useDelegate.test("getRemainingVillages")) {
            return delegate.getRemainingVillages();
        } else {
            return (int) methodAction.apply("getRemainingVillages", new Object[] {this});
        }
    }

    @Override
    public int getRemainingCities () {
        if(useDelegate.test("getRemainingCities")) {
            return delegate.getRemainingCities();
        } else {
            return (int) methodAction.apply("getRemainingCities", new Object[] {this});
        }
    }

    @Override
    public Map<DevelopmentCardType, Integer> getDevelopmentCards () {
        if(useDelegate.test("getDevelopmentCards")) {
            return delegate.getDevelopmentCards();
        } else {
            return (Map<DevelopmentCardType, Integer>) methodAction.apply("getDevelopmentCards", new Object[] {this});
        }
    }

    @Override
    public void addDevelopmentCard (DevelopmentCardType developmentCardType) {
        if(useDelegate.test("addDevelopmentCard")) {
            delegate.addDevelopmentCard(developmentCardType);
        } else {
            methodAction.apply("addDevelopmentCard", new Object[] {this, developmentCardType});
        }
    }

    @Override
    public boolean removeDevelopmentCard (DevelopmentCardType developmentCardType) {
        if(useDelegate.test("removeDevelopmentCard")) {
            return delegate.removeDevelopmentCard(developmentCardType);
        } else {
            return (boolean) methodAction.apply("removeDevelopmentCard", new Object[] {this, developmentCardType});
        }
    }

    @Override
    public int getTotalDevelopmentCards () {
        if(useDelegate.test("getTotalDevelopmentCards")) {
            return delegate.getTotalDevelopmentCards();
        } else {
            return (int) methodAction.apply("getTotalDevelopmentCards", new Object[] {this});
        }
    }

    @Override
    public int getKnightsPlayed () {
        if(useDelegate.test("getKnightsPlayed")) {
            return delegate.getKnightsPlayed();
        } else {
            return (int) methodAction.apply("getKnightsPlayed", new Object[] {this});
        }
    }
}
