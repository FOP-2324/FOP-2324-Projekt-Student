package projekt.view.gameControls;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import projekt.model.Player;
import projekt.model.ResourceType;
import projekt.model.TradePayload;
import projekt.view.CardPane;
import projekt.view.ResourceCardPane;

/**
 * A dialog to prompt the user to accept or decline a trade offer.
 * The dialog shows the offer and request of the trade and has buttons to accept
 * or decline the trade.
 * The result of the dialog is true if the trade is accepted and false if it is
 * declined.
 */
public class AcceptTradeDialog extends Dialog<Boolean> {
    /**
     * Creates a new AcceptTradeDialog for the given trade and player.
     *
     * @param trade  The trade to accept or decline.
     * @param player The player that is prompted to accept or decline the trade.
     */
    public AcceptTradeDialog(final TradePayload trade, final Player player) {
        setTitle("Accept trade offer");
        setHeaderText(
            String.format("%s wants to trade with you (%s).", trade.player().getName(), player.getName()));
        final GridPane mainPane = new GridPane(10, 10);
        mainPane.add(new Label("Offer:"), 0, 0);
        mainPane.add(new Label("Request:"), 0, 1);

        int cardCount = 0;
        for (final ResourceType resourceType : trade.offer().keySet()) {
            final CardPane resourceCard = new ResourceCardPane(resourceType, trade.offer().get(resourceType));
            final HBox resourceBox = new HBox(resourceCard);
            resourceBox.setAlignment(Pos.CENTER);
            GridPane.setHgrow(resourceBox, Priority.ALWAYS);
            cardCount++;
            mainPane.add(resourceBox, cardCount + 1, 0);
        }
        cardCount = 0;
        for (final ResourceType resourceType : trade.request().keySet()) {
            final CardPane resourceCard = new ResourceCardPane(resourceType, trade.request().get(resourceType));
            final HBox resourceBox = new HBox(resourceCard);
            resourceBox.setAlignment(Pos.CENTER);
            GridPane.setHgrow(resourceBox, Priority.ALWAYS);
            cardCount++;
            mainPane.add(resourceBox, cardCount + 1, 1);
        }

        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(mainPane);
        dialogPane.getButtonTypes().add(ButtonType.YES);
        dialogPane.getButtonTypes().add(ButtonType.NO);

        setResultConverter(buttonType -> {
            return ButtonType.YES.equals(buttonType);
        });
    }
}
