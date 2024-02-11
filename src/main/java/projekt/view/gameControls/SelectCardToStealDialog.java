package projekt.view.gameControls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import projekt.Config;
import projekt.model.Player;
import projekt.model.ResourceType;
import projekt.view.CardPane;
import projekt.view.PlayerLabel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A dialog to prompt the user to select a card to steal from a player.
 * The dialog shows the cards of all given players and lets the user select one.
 * Does not show which type of resource the cards represent.
 * The result of the dialog is the player and the resource type of the selected
 * card.
 */
public class SelectCardToStealDialog extends Dialog<Entry<Player, ResourceType>> {
    private final ObservableMap<Player, ResourceType> selectedResource = FXCollections.observableMap(new HashMap<>());
    private final ObjectProperty<CardPane> selectedCard = new SimpleObjectProperty<>();

    /**
     * Creates a new SelectCardToStealDialog for the given players.
     *
     * @param players The players a card can be stolen from.
     */
    public SelectCardToStealDialog(final List<Player> players) {
        this.setTitle("Select card to steal");
        this.setHeaderText("Select a card to steal from a player");
        final GridPane mainPane = new GridPane(10, 10);
        mainPane.getStylesheets().add("css/hexmap.css");

        for (final Player player : players) {
            mainPane.add(new PlayerLabel(player), 0, players.indexOf(player));
            final List<ResourceType> resourceTypes = player.getResources().keySet().stream()
                .collect(Collectors.toList());
            Collections.shuffle(resourceTypes, Config.RANDOM);
            int cardCount = 0;
            for (int i = 0; i < resourceTypes.size(); i++) {
                final ResourceType resourceType = resourceTypes.get(i);
                for (int j = 0; j < player.getResources().get(resourceType); j++) {
                    final CardPane resourceCard = new CardPane(Color.LIGHTGRAY, null, "", 40.0);
                    resourceCard.getStyleClass().add("selectable");
                    resourceCard.setOnMouseClicked(e -> {
                        selectedCard.set(resourceCard);
                        if (selectedResource.containsValue(resourceType)) {
                            return;
                        }
                        selectedResource.put(player, resourceType);
                    });
                    selectedCard.subscribe((oldValue, newValue) -> {
                        if (resourceCard.equals(newValue)) {
                            resourceCard.getStyleClass().add("selected");
                            return;
                        }
                        resourceCard.getStyleClass().remove("selected");
                    });
                    final HBox resourceBox = new HBox(resourceCard);
                    GridPane.setHgrow(resourceBox, Priority.ALWAYS);
                    mainPane.add(resourceBox, j + cardCount + 1, players.indexOf(player));
                    cardCount++;
                }
            }
        }

        final DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(mainPane);
        dialogPane.getButtonTypes().add(ButtonType.OK);

        setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                if (selectedResource.isEmpty()) {
                    return null;
                }
                return selectedResource.entrySet().iterator().next();
            }
            return null;
        });
    }
}
