package projekt.view.gameControls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.ResourceType;
import projekt.model.TradePayload;
import projekt.view.CardPane;
import projekt.view.IntegerField;
import projekt.view.ResourceCardPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A dialog to prompt the user to make a trade with the bank or another player.
 * The dialog shows the resources the player can offer and request and lets the
 * user select a number of each resource.
 * If the player is trading with the bank, the dialog also shows the trade ratio
 * for each resource.
 * <p>
 * The result of the dialog is a {@link TradePayload}.
 */
public class TradeDialog extends Dialog<TradePayload> {
    private final ObjectProperty<ResourceType> selectedBankOffer = new SimpleObjectProperty<>();
    private final ObjectProperty<ResourceType> selectedBankRequest = new SimpleObjectProperty<>();
    private final Map<ResourceType, Integer> playerOffer = new HashMap<>();
    private final Map<ResourceType, Integer> playerRequest = new HashMap<>();

    /**
     * Creates a new TradeDialog for the given trade payload.
     *
     * @param payload The trade payload to create the dialog for.
     */
    public TradeDialog(final TradePayload payload) {
        final TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.getStylesheets().add("css/hexmap.css");
        tabPane.getTabs().addAll(createBankTradeTab(payload), createPlayerTradeTab());
        setTitle("Trade");

        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(tabPane);
        dialogPane.getButtonTypes().add(ButtonType.OK);
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                if (selectedBankOffer.getValue() == null || selectedBankRequest.getValue() == null) {
                    if (playerOffer.isEmpty() || playerRequest.isEmpty()) {
                        return null;
                    }
                    return new TradePayload(playerOffer, playerRequest, false, payload.player());
                }
                return new TradePayload(
                    Map.of(
                        selectedBankOffer.getValue(),
                        payload.player().getTradeRatio(selectedBankOffer.getValue())
                    ),
                    Map.of(selectedBankRequest.getValue(), 1), true, payload.player()
                );
            }
            return null;
        });
    }

    /**
     * Creates the tab for trading with the bank.
     * The user can select a resource to offer and a resource to request.
     * The offered resource is displayed with the trade ratio for that resource.
     * The requested resource is displayed with a ratio of 1.
     * Only one resource can be selected for each offer and request.
     *
     * @param payload The trade payload to create the tab for.
     * @return The created tab.
     */
    private Tab createBankTradeTab(final TradePayload payload) {
        final Tab bankTradeTab = new Tab("Bank");
        final GridPane mainPane = new GridPane(10, 10);

        mainPane.add(new Label("Offer:"), 0, 0);
        mainPane.add(new Label("Request:"), 0, 1);

        for (final ResourceType resourceType : ResourceType.values()) {
            final HBox offeredResourceCard = new HBox(getSelectableResourceCard(resourceType, selectedBankOffer));
            offeredResourceCard.setAlignment(Pos.CENTER);

            final Label ratio = new Label(String.format("%d", payload.player().getTradeRatio(resourceType)));

            final VBox offeredResourceBox = new VBox(offeredResourceCard, ratio);
            offeredResourceBox.setSpacing(5);
            offeredResourceBox.setAlignment(Pos.CENTER);
            mainPane.add(offeredResourceBox, resourceType.ordinal() + 1, 0);
            GridPane.setHgrow(offeredResourceBox, Priority.ALWAYS);

            final HBox requestedResourceCard = new HBox(getSelectableResourceCard(resourceType, selectedBankRequest));
            requestedResourceCard.setAlignment(Pos.CENTER);

            final VBox requestedResourceBox = new VBox(requestedResourceCard);
            requestedResourceBox.setSpacing(5);
            requestedResourceBox.setAlignment(Pos.CENTER);
            mainPane.add(requestedResourceBox, resourceType.ordinal() + 1, 1);
            GridPane.setHgrow(requestedResourceBox, Priority.ALWAYS);
        }

        bankTradeTab.setContent(mainPane);
        return bankTradeTab;
    }

    /**
     * Creates the tab for trading with another player.
     * The user can select any amount of each resource to offer and request.
     *
     * @return The created tab.
     */
    private Tab createPlayerTradeTab() {
        final Tab playerTradeTab = new Tab("Player");
        final GridPane mainPane = new GridPane(10, 10);
        mainPane.add(new Label("Offer:"), 0, 1);
        mainPane.add(new Label("Request:"), 0, 2);

        for (final ResourceType resourceType : ResourceType.values()) {
            final CardPane resourceCard = new ResourceCardPane(resourceType, "", 50);
            mainPane.add(resourceCard, resourceType.ordinal() + 1, 0);

            final IntegerField offeredResourcesField = new IntegerField();
            offeredResourcesField.valueProperty().subscribe((oldValue, newValue) -> {
                if (newValue == null || newValue.intValue() <= 0) {
                    playerOffer.remove(resourceType);
                    return;
                }
                playerOffer.put(resourceType, newValue.intValue());
            });
            mainPane.add(offeredResourcesField, resourceType.ordinal() + 1, 1);

            final IntegerField requestedResourcesField = new IntegerField();
            requestedResourcesField.valueProperty().subscribe((oldValue, newValue) -> {
                if (newValue == null || newValue.intValue() <= 0) {
                    playerRequest.remove(resourceType);
                    return;
                }
                playerRequest.put(resourceType, newValue.intValue());
            });

            mainPane.add(requestedResourcesField, resourceType.ordinal() + 1, 2);
        }

        playerTradeTab.setContent(mainPane);
        return playerTradeTab;
    }

    /**
     * Returns a card pane for the given resource type that can be selected.
     *
     * @param resourceType         The resource type to create the card for.
     * @param selectedResourceType The property to store the selected resource type
     *                             in.
     * @return The created card pane.
     */
    private CardPane getSelectableResourceCard(
        final ResourceType resourceType,
        final Property<ResourceType> selectedResourceType
    ) {
        final CardPane requestedResourceCard = new ResourceCardPane(resourceType, "", 50);
        requestedResourceCard.getStyleClass().add("selectable");
        requestedResourceCard.setOnMouseClicked(e -> {
            if (Objects.equals(selectedResourceType.getValue(), resourceType)) {
                selectedResourceType.setValue(null);
                return;
            }
            selectedResourceType.setValue(resourceType);
        });
        selectedResourceType.subscribe((oldValue, newValue) -> {
            if (Objects.equals(newValue, resourceType)) {
                requestedResourceCard.getStyleClass().add("selected");
                return;
            }
            requestedResourceCard.getStyleClass().remove("selected");
        });
        return requestedResourceCard;
    }
}
