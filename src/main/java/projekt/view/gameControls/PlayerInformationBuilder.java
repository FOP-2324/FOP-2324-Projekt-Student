package projekt.view.gameControls;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import projekt.model.DevelopmentCardType;
import projekt.model.Player;
import projekt.model.ResourceType;
import projekt.view.CardPane;
import projekt.view.DevelopmentCardPane;
import projekt.view.PlayerLabel;
import projekt.view.ResourceCardPane;

import java.util.Map;

/**
 * A Builder to create the player information view.
 * The player information view contains information about the player's
 * resources, development cards, remaining roads/villages/cities and victory
 * points.
 * Highlights resources that have changed since the last update.
 */
public class PlayerInformationBuilder implements Builder<Region> {
    private final Player player;
    private final Map<ResourceType, Integer> changedResources;

    /**
     * Creates a new PlayerInformationBuilder with the given player and changed
     * resources.
     *
     * @param player           the player to display information for
     * @param changedResources the resources that have changed since the last update
     */
    public PlayerInformationBuilder(final Player player, final Map<ResourceType, Integer> changedResources) {
        this.player = player;
        this.changedResources = changedResources;
    }

    @Override
    public Region build() {
        final VBox mainBox = new VBox();
        mainBox.getStylesheets().add("css/hexmap.css");
        final Label playerName = new PlayerLabel(player);

        final Label resourcesLabel = new Label("Your Resources:");
        final FlowPane resourcesBox = new FlowPane(5, 5);
        for (final ResourceType resourceType : player.getResources().keySet()) {
            if (player.getResources().get(resourceType) == 0) {
                continue;
            }

            final ResourceCardPane resourceCard = new ResourceCardPane(
                resourceType,
                player.getResources().get(resourceType)
            );
            resourcesBox.getChildren().add(resourceCard);
            if (changedResources.containsKey(resourceType)) {
                resourceCard.getStyleClass().add("highlighted");
            }
        }

        final Label developmentCardsLabel = new Label("Your Development Cards:");
        final FlowPane developmentCardsBox = new FlowPane(5, 5);
        for (final DevelopmentCardType developmentCardType : player.getDevelopmentCards().keySet()) {
            if (player.getDevelopmentCards().get(developmentCardType) == 0) {
                continue;
            }

            final CardPane developmentCardTypeCard = new DevelopmentCardPane(
                developmentCardType,
                player.getDevelopmentCards().get(developmentCardType)
            );
            developmentCardsBox.getChildren().add(developmentCardTypeCard);
        }

        final Label remainingRoadsLabel = new Label(
            String.format("Your remaining Roads: %d", player.getRemainingRoads()));
        final Label remainingVillagesLabel = new Label(
            String.format("Your remaining Villages: %d", player.getRemainingVillages()));
        final Label remainingCitiesLabel = new Label(
            String.format("Your remaining Cities: %d", player.getRemainingCities()));

        final Label victoryPointsLabel = new Label(String.format("Your Victory Points: %d", player.getVictoryPoints()));

        mainBox.getChildren().addAll(playerName, resourcesLabel, resourcesBox, developmentCardsLabel,
                                     developmentCardsBox, remainingRoadsLabel, remainingVillagesLabel, remainingCitiesLabel,
                                     victoryPointsLabel
        );
        mainBox.setPadding(new Insets(5));
        mainBox.setSpacing(5);
        return mainBox;
    }
}
