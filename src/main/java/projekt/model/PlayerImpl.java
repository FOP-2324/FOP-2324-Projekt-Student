package projekt.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;
import projekt.Config;
import projekt.model.buildings.Settlement;

import java.util.HashMap;
import java.util.Map;

import static projekt.Config.MAX_CITIES;
import static projekt.Config.MAX_ROADS;
import static projekt.Config.MAX_VILLAGES;

/**
 * Default implementation of {@link Player}.
 */
public class PlayerImpl implements Player {
    private final HexGrid hexGrid;
    private final String name;
    private final int id;
    private final Color color;
    private final boolean ai;
    private final Map<ResourceType, Integer> resources = new HashMap<>();
    private final Map<DevelopmentCardType, Integer> developmentCards = new HashMap<>();
    private final Map<DevelopmentCardType, Integer> playedDevelopmentCards = new HashMap<>();

    @DoNotTouch("Please don't create a public Contructor, use the Builder instead.")
    private PlayerImpl(final HexGrid hexGrid, final Color color, final int id, final String name, final boolean ai) {
        this.hexGrid = hexGrid;
        this.color = color;
        this.id = id;
        this.name = name;
        this.ai = ai;
    }

    @Override
    public HexGrid getHexGrid() {
        return this.hexGrid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public boolean isAi() {
        return this.ai;
    }

    @Override
    public int getVictoryPoints() {
        final int buildingVictoryPoints = getSettlements().stream()
            .mapToInt(settlement -> settlement.type().resourceAmount)
            .sum();
        final int developmentCardsVictoryPoints = developmentCards.getOrDefault(DevelopmentCardType.VICTORY_POINTS, 0);

        return buildingVictoryPoints + developmentCardsVictoryPoints;
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public Map<ResourceType, Integer> getResources() {
        // TODO: H1.1
        return org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public void addResource(final ResourceType resourceType, final int amount) {
        // TODO: H1.1
        org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public void addResources(final Map<ResourceType, Integer> resources) {
        // TODO: H1.1
        org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public boolean hasResources(final Map<ResourceType, Integer> resources) {
        // TODO: H1.1
        return org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public boolean removeResource(final ResourceType resourceType, final int amount) {
        // TODO: H1.1
        return org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public boolean removeResources(final Map<ResourceType, Integer> resources) {
        // TODO: H1.1
        return org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.1")
    public int getTradeRatio(final ResourceType resourceType) {
        // TODO: H1.1
        return org.tudalgo.algoutils.student.Student.crash("H1.1 - Remove if implemented");
    }

    @Override
    public int getRemainingRoads() {
        return MAX_ROADS - getRoads().size();
    }

    @Override
    public int getRemainingVillages() {
        return (int) (
            MAX_VILLAGES - getSettlements().stream()
                .filter(settlement -> settlement.type().equals(Settlement.Type.VILLAGE)).count()
        );
    }

    @Override
    public int getRemainingCities() {
        return (int) (
            MAX_CITIES - getSettlements().stream()
                .filter(settlement -> settlement.type().equals(Settlement.Type.CITY)).count()
        );
    }

    @Override
    @StudentImplementationRequired("H1.2")
    public Map<DevelopmentCardType, Integer> getDevelopmentCards() {
        // TODO: H1.2
        return org.tudalgo.algoutils.student.Student.crash("H1.2 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.2")
    public void addDevelopmentCard(final DevelopmentCardType developmentCardType) {
        // TODO: H1.2
        org.tudalgo.algoutils.student.Student.crash("H1.2 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.2")
    public boolean removeDevelopmentCard(final DevelopmentCardType developmentCardType) {
        // TODO: H1.2
        return org.tudalgo.algoutils.student.Student.crash("H1.2 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.2")
    public int getTotalDevelopmentCards() {
        // TODO: H1.2
        return org.tudalgo.algoutils.student.Student.crash("H1.2 - Remove if implemented");
    }

    @Override
    @StudentImplementationRequired("H1.2")
    public int getKnightsPlayed() {
        // TODO: H1.2
        return org.tudalgo.algoutils.student.Student.crash("H1.2 - Remove if implemented");
    }

    /**
     * Builder for {@link PlayerImpl}.
     * Allows to create a new player and modify its properties before building it.
     */
    @DoNotTouch
    public static class Builder {
        private int id;
        private Color color;
        private @Nullable String name;
        private final SimpleBooleanProperty ai = new SimpleBooleanProperty(false);

        /**
         * Creates a new builder for a player with the given id.
         *
         * @param id the id of the player to create
         */
        public Builder(final int id) {
            this.id = id;
            color(null);
        }

        /**
         * Returns the color of the player.
         *
         * @return the color of the player
         */
        public Color getColor() {
            return this.color;
        }

        /**
         * Sets the color of the player.
         *
         * @param playerColor the color of the player
         * @return this builder
         */
        public Builder color(final Color playerColor) {
            this.color = playerColor == null
                         ? new Color(
                Config.RANDOM.nextDouble(),
                Config.RANDOM.nextDouble(),
                Config.RANDOM.nextDouble(),
                1
            )
                         : playerColor;
            return this;
        }

        /**
         * Returns the name of the player.
         *
         * @return the name of the player
         */
        public @Nullable String getName() {
            return this.name;
        }

        /**
         * Sets the name of the player.
         *
         * @param playerName the name of the player
         * @return this builder
         */
        public Builder name(final @Nullable String playerName) {
            this.name = playerName;
            return this;
        }

        /**
         * Returns the name of the player or a default name if no name was set.
         * The default name is "Player" followed by the id of the player.
         *
         * @return the name of the player or a default name if no name was set
         */
        public String nameOrDefault() {
            return this.name == null ? String.format("Player%d", this.id) : this.name;
        }

        /**
         * Sets the id of the player.
         *
         * @param newId the id of the player
         * @return this builder
         */
        public Builder id(final int newId) {
            this.id = newId;
            return this;
        }

        /**
         * Returns the id of the player.
         *
         * @return the id of the player
         */
        public int getId() {
            return this.id;
        }

        /**
         * Returns whether the player is an AI.
         *
         * @return whether the player is an AI
         */
        public boolean isAi() {
            return this.ai.get();
        }

        /**
         * Returns the property indicating whether the player is an AI.
         *
         * @return the property indicating whether the player is an AI
         */
        public SimpleBooleanProperty aiProperty() {
            return this.ai;
        }

        /**
         * Sets whether the player is an AI.
         *
         * @param ai whether the player is an AI
         * @return this builder
         */
        public Builder ai(final boolean ai) {
            this.ai.set(ai);
            return this;
        }

        /**
         * Builds the player with the properties set in this builder.
         *
         * @param grid the grid the player is on
         * @return the player with the properties set in this builder
         */
        public Player build(final HexGrid grid) {
            return new PlayerImpl(grid, this.color, this.id, nameOrDefault(), this.ai.get());
        }
    }

    @Override
    public String toString() {
        return String.format("Player %d %s (%s)", getID(), getName(), getColor());
    }
}
