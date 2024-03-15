package projekt.controller.gui;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Subscription;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;
import projekt.controller.PlayerController;
import projekt.controller.PlayerObjective;
import projekt.controller.actions.AcceptTradeAction;
import projekt.controller.actions.BuyDevelopmentCardAction;
import projekt.controller.actions.EndTurnAction;
import projekt.controller.actions.PlayDevelopmentCardAction;
import projekt.controller.actions.RollDiceAction;
import projekt.controller.actions.SelectCardsAction;
import projekt.controller.actions.SelectRobberTileAction;
import projekt.controller.actions.StealCardAction;
import projekt.controller.actions.TradeAction;
import projekt.model.DevelopmentCardType;
import projekt.model.Player;
import projekt.model.PlayerState;
import projekt.model.ResourceType;
import projekt.model.TradePayload;
import projekt.model.tiles.Tile;
import projekt.view.gameControls.AcceptTradeDialog;
import projekt.view.gameControls.PlayerActionsBuilder;
import projekt.view.gameControls.SelectCardToStealDialog;
import projekt.view.gameControls.SelectResourcesDialog;
import projekt.view.gameControls.TradeDialog;
import projekt.view.gameControls.UseDevelopmentCardDialog;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is responsible for handling all player actions performed through
 * the UI. It ensures that the correct buttons are enabled and disabled based on
 * the current player objective and state.
 * It also ensures that the correct actions are triggered when a button is
 * clicked and that the user is prompted when a action requires user input.
 * Additionally it triggers the respective actions based on the user input.
 *
 * <b>Do not touch any of the given attributes these are constructed in a way to
 * ensure thread safety.</b>
 */
public class PlayerActionsController implements Controller {
    private final PlayerActionsBuilder builder;
    private final GameBoardController gameBoardController;
    private final Property<PlayerController> playerControllerProperty = new SimpleObjectProperty<>();
    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();
    private Subscription playerStateSubscription = Subscription.EMPTY;

    /**
     * Creates a new PlayerActionsController.
     * It attaches listeners to populate the playerController, playerState and
     * playerObjective properties. This is necessary to ensure these properties are
     * always on the correct thread.
     * Additionally the PlayerActionsBuilder is created with all necessary event
     * handlers.
     *
     * <b>Do not touch this constructor.</b>
     *
     * @param gameBoardController      the game board controller
     * @param playerControllerProperty the property that contains the player
     *                                 controller that is currently active
     */
    @DoNotTouch
    public PlayerActionsController(
        final GameBoardController gameBoardController,
        final Property<PlayerController> playerControllerProperty
    ) {
        this.playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                playerStateSubscription.unsubscribe();
                playerStateSubscription = newValue.getPlayerStateProperty().subscribe(
                        (oldState, newState) -> Platform.runLater(() -> this.playerStateProperty.setValue(newState)));
                this.playerStateProperty.setValue(newValue.getPlayerStateProperty().getValue());
            });
        });
        this.gameBoardController = gameBoardController;
        playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue == null) {
                    return;
                }
                this.playerControllerProperty.setValue(newValue);
            });
        });
        Platform.runLater(() -> {
            this.playerControllerProperty.setValue(playerControllerProperty.getValue());
        });

        this.builder = new PlayerActionsBuilder(
            actionWrapper(this::buildVillageButtonAction, true),
            actionWrapper(this::upgradeVillageButtonAction, true),
            actionWrapper(this::buildRoadButtonAction, true),
            actionWrapper(this::buyDevelopmentCardButtonAction, false),
            actionWrapper(this::useDevelopmentCardButtonAction, false),
            actionWrapper(this::endTurnButtonAction, false),
            actionWrapper(this::rollDiceButtonAction, false),
            actionWrapper(this::tradeButtonAction, false),
            this::abortButtonAction
        );
    }

    /**
     * Updates the UI based on the given objective. This includes enabling and
     * disabling buttons and prompting the user if necessary.
     * Also redraws the game board and updates the player information.
     *
     * @param objective the objective to check
     */
    @StudentImplementationRequired("H3.2")
    private void updateUIBasedOnObjective(final PlayerObjective objective) {
        // TODO: H3.2
        org.tudalgo.algoutils.student.Student.crash("H3.2 - Remove if implemented");
    }

    /**
     * Returns the player controller that is currently active.
     * Please do not use this method to get the playerState or playerObjective.
     * Use the {@link #getPlayerState()} and {@link #getPlayerObjective()} instead.
     *
     * @return the player controller that is currently active
     */
    @DoNotTouch
    private PlayerController getPlayerController() {
        return playerControllerProperty.getValue();
    }

    /**
     * Returns the player state of the player that is currently active.
     *
     * @return the player state of the player that is currently active
     */
    @DoNotTouch
    private PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Returns the player objective of the player that is currently active.
     *
     * @return the player objective of the player that is currently active
     */
    @DoNotTouch
    private PlayerObjective getPlayerObjective() {
        return getPlayerState().playerObjective();
    }

    /**
     * Returns the HexGridController of the game board.
     *
     * @return the HexGridController of the game board
     */
    @DoNotTouch
    private HexGridController getHexGridController() {
        return gameBoardController.getHexGridController();
    }

    /**
     * Returns the player that is currently active.
     *
     * @return the player that is currently active
     */
    @DoNotTouch
    private Player getPlayer() {
        return getPlayerController().getPlayer();
    }

    /**
     * ReDraws the intersections.
     */
    @DoNotTouch
    private void drawIntersections() {
        getHexGridController().drawIntersections();
    }

    /**
     * ReDraws the edges.
     */
    @DoNotTouch
    private void drawEdges() {
        getHexGridController().drawEdges();
    }

    /**
     * Removes all highlights from the game board.
     */
    @DoNotTouch
    private void removeAllHighlights() {
        getHexGridController().getEdgeControllers().forEach(EdgeController::unhighlight);
        getHexGridController().getIntersectionControllers().forEach(IntersectionController::unhighlight);
        getHexGridController().unhighlightTiles();
    }

    /**
     * Updates the player information in the game board.
     */
    @DoNotTouch
    private void updatePlayerInformation() {
        gameBoardController.updatePlayerInformation(getPlayer(), getPlayerState().changedResources());
    }

    /**
     * Wraps a event handler (primarily button Actions) to ensure that all
     * highlights are removed, intersections are redrawn and all buttons except the
     * abort button (if abortable) are disabled.
     * This method is intended to be used when a button is clicked, to ensure a
     * common state before a action is performed.
     *
     * @param handler   the handler to wrap
     * @param abortable whether the action is abortable
     * @return the wrapped handler
     */
    @DoNotTouch
    private Consumer<ActionEvent> actionWrapper(final Consumer<ActionEvent> handler, final boolean abortable) {
        return e -> {
            removeAllHighlights();
            drawIntersections();
            if (abortable) {
                builder.disableAllButtons();
                builder.enableAbortButton();
            }

            handler.accept(e);
            // gameBoardController.updatePlayerInformation(getPlayer());
        };
    }

    // Build actions

    /**
     * Wraps a event handler to ensure that all highlights are removed, the correct
     * buttons are reenabled and the player information is up to date.
     * <p>
     * This method is intended to be used when a action is triggered on the
     * player controller to ensure a common state after the action is performed.
     *
     * @param handler the handler to wrap
     * @return the wrapped handler
     */
    @DoNotTouch
    private Consumer<MouseEvent> buildActionWrapper(final Consumer<MouseEvent> handler) {
        return e -> {
            handler.accept(e);

            removeAllHighlights();
            if (getPlayerController() != null) {
                updateUIBasedOnObjective(getPlayerObjective());
            }
        };
    }

    /**
     * Enables or disable the build village button based on the currently allowed
     * actions and if there are any buildable intersections.
     */
    @StudentImplementationRequired("H3.1")
    private void updateBuildVillageButtonState() {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * Attaches the logic to build a village to all buildable intersections and
     * highlights them.
     * When an intersection is selected, it triggers the BuildVillageAction.
     * The logic is wrapped in a buildActionWrapper to ensure a common state after a
     * village is built.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @StudentImplementationRequired("H3.1")
    private void buildVillageButtonAction(final ActionEvent event) {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * Enables or disable the upgrade village button based on the currently allowed
     * actions and if there are any upgradeable villages.
     */
    @StudentImplementationRequired("H3.1")
    private void updateUpgradeVillageButtonState() {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * Attaches the logic to upgrade a village to all upgradeable intersections and
     * highlights them.
     * When an intersection is selected, it triggers the UpgradeVillageAction.
     * The logic is wrapped in a buildActionWrapper to ensure a common state after a
     * village is upgraded.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @StudentImplementationRequired("H3.1")
    private void upgradeVillageButtonAction(final ActionEvent event) {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * Enables or disable the build road button based on the currently allowed
     * actions and if there are any edges to build on.
     */
    @StudentImplementationRequired("H3.1")
    private void updateBuildRoadButtonState() {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * Attaches the logic to build a road to all buildable edges and highlights
     * them.
     * When an edge is selected, it triggers the BuildRoadAction.
     * The logic is wrapped in a buildActionWrapper to ensure a common state after a
     * road is built.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @StudentImplementationRequired("H3.1")
    private void buildRoadButtonAction(final ActionEvent event) {
        // TODO: H3.1
        org.tudalgo.algoutils.student.Student.crash("H3.1 - Remove if implemented");
    }

    /**
     * The action that is triggered when the end turn button is clicked.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    private void endTurnButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new EndTurnAction());
    }

    /**
     * The action that is triggered when the roll dice button is clicked.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    private void rollDiceButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new RollDiceAction());
    }

    // Robber actions

    /**
     * Triggers the SelectRobberTileAction with the selected tile and unhighlights
     * all tiles. After the action is triggered, the tiles are redrawn.
     *
     * @param tile the tile that was clicked
     */
    @DoNotTouch
    private void selectRobberTileAction(final Tile tile) {
        getHexGridController().unhighlightTiles();
        getPlayerController().triggerAction(new SelectRobberTileAction(tile.getPosition()));
        getHexGridController().drawTiles();
    }

    /**
     * Performs the action of selecting a card to steal from another player.
     * If there are no players to steal from, triggers the EndTurnAction.
     * Prompts the user to select a card to steal from a player and triggers the
     * StealCardAction with the selected card.
     * If no card is selected, triggers the EndTurnAction.
     */
    @DoNotTouch
    private void selectCardToStealAction() {
        if (getPlayerState().playersToStealFrom().isEmpty()) {
            getPlayerController().triggerAction(new EndTurnAction());
            return;
        }
        final SelectCardToStealDialog dialog = new SelectCardToStealDialog(getPlayerState().playersToStealFrom());
        dialog.showAndWait().ifPresentOrElse(
            result -> getPlayerController().triggerAction(new StealCardAction(result.getValue(), result.getKey())),
            () -> getPlayerController().triggerAction(new EndTurnAction())
        );
    }

    /**
     * Prompts the user to select resource cards.
     * If the current player objective is DROP_HALF_CARDS, the user can only select
     * cards from the players resources.
     * If the user cancels or an invalid amount of cards is selected, the user is
     * prompted again.
     * <p>
     * Triggers the SelectCardsAction with the selected cards.
     *
     * @param amountToSelect the amount of cards to select
     */
    @DoNotTouch
    private void selectResources(final int amountToSelect) {
        final SelectResourcesDialog dialog = new SelectResourcesDialog(amountToSelect, getPlayer(),
                                                                       PlayerObjective.DROP_CARDS.equals(getPlayerObjective()) ? getPlayer().getResources() : null,
                                                                       PlayerObjective.DROP_CARDS.equals(getPlayerObjective())
        );
        Optional<Map<ResourceType, Integer>> result = dialog.showAndWait();
        while (result.isEmpty() || result.get() == null) {
            result = dialog.showAndWait();
        }
        getPlayerController().triggerAction(new SelectCardsAction(result.get()));
    }

    // Development card actions

    /**
     * Enables or disable the buy development card button based on the currently
     * allowed actions and whether the player can buy a development card.
     */
    @DoNotTouch
    private void updateBuyDevelopmentCardButtonState() {
        if (getPlayerObjective().getAllowedActions().contains(BuyDevelopmentCardAction.class)
            && getPlayerController().canBuyDevelopmentCard()) {
            builder.enableBuyDevelopmentCardButton();
            return;
        }
        builder.disableBuyDevelopmentCardButton();
    }

    /**
     * Performs the action of buying a development card.
     * Triggers the BuyDevelopmentCardAction.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    private void buyDevelopmentCardButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new BuyDevelopmentCardAction());
        updateUIBasedOnObjective(getPlayerObjective());
    }

    /**
     * Enables or disable the use development card button based on the currently
     * allowed actions and whether the player has any development cards to play.
     */
    @DoNotTouch
    private void updateUseDevelopmentCardButtonState() {
        if (getPlayerObjective().getAllowedActions().contains(PlayDevelopmentCardAction.class)
            && getPlayer().getDevelopmentCards().entrySet().stream().anyMatch(
            entry -> entry.getKey() != DevelopmentCardType.VICTORY_POINTS && entry.getValue() > 0)) {
            builder.enablePlayDevelopmentCardButton();
            return;
        }
        builder.disablePlayDevelopmentCardButton();
    }

    /**
     * Performs the action of playing a development card.
     * Prompts the user to select a development card to play.
     * If the user cancels, the action is cancelled.
     * Triggers the PlayDevelopmentCardAction with the selected card.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    public void useDevelopmentCardButtonAction(final ActionEvent event) {
        final UseDevelopmentCardDialog dialog = new UseDevelopmentCardDialog(getPlayer());
        dialog.showAndWait()
            .ifPresent(result -> getPlayerController().triggerAction(new PlayDevelopmentCardAction(result)));
        updateUIBasedOnObjective(getPlayerObjective());
    }

    // Trade actions

    /**
     * Performs the trading action.
     * Prompts the user to select the cards to offer and the cards to request.
     * If the user cancels, the trade is cancelled.
     * Triggers the TradeAction with the selected cards.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    private void tradeButtonAction(final ActionEvent event) {
        System.out.println("Trading");
        final TradeDialog dialog = new TradeDialog(new TradePayload(null, null, false, getPlayer()));
        dialog.showAndWait().ifPresentOrElse(payload -> {
            getPlayerController().triggerAction(new TradeAction(payload));
        }, () -> System.out.println("Trade cancelled"));
        updateUIBasedOnObjective(getPlayerObjective());
    }

    /**
     * Performs the action of accepting a trade offer.
     * Prompts the user to accept or decline the trade offer.
     * If the user cancels, the trade is declined.
     * Triggers the AcceptTradeAction with a boolean representing the players
     * decision.
     */
    @DoNotTouch
    private void acceptTradeOffer() {
        final Optional<Boolean> optionalResult = new AcceptTradeDialog(getPlayerState().offeredTrade(), getPlayer())
            .showAndWait();
        optionalResult.ifPresent(result -> getPlayerController().triggerAction(new AcceptTradeAction(result)));
    }

    /**
     * Aborts the current action by remove all highlights and reenabling the correct
     * buttons. Disables the abort button.
     * <p>
     * This method is prepared to be used with a button.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    private void abortButtonAction(final ActionEvent event) {
        removeAllHighlights();
        updateUIBasedOnObjective(getPlayerObjective());
        builder.disableAbortButton();
    }

    @Override
    @DoNotTouch
    public Builder<Region> getBuilder() {
        return builder;
    }

    @Override
    @DoNotTouch
    public Region buildView() {
        final Region view = builder.build();

        playerStateProperty.subscribe((oldValue, newValue) -> updateUIBasedOnObjective(getPlayerObjective()));
        builder.disableAllButtons();
        if (getPlayerController() != null) {
            updateUIBasedOnObjective(getPlayerObjective());
        }
        return view;
    }
}
