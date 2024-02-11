package projekt.view.gameControls;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import java.util.function.Consumer;

/**
 * A Builder to create the player actions view.
 * The player actions view contains nodes to perform all actions a player can do
 * during their turn.
 * Has methods to enable and disable the nodes.
 */
@DoNotTouch
public class PlayerActionsBuilder implements Builder<Region> {
    private final Consumer<ActionEvent> buildVillageButtonAction;
    private final Consumer<ActionEvent> upgradeVillageButtonAction;
    private final Consumer<ActionEvent> buildRoadButtonAction;
    private final Consumer<ActionEvent> buyDevelopmentCardButtonAction;
    private final Consumer<ActionEvent> playDevelopmentCardButtonAction;
    private final Consumer<ActionEvent> endTurnButtonAction;
    private final Consumer<ActionEvent> rollDiceButtonAction;
    private final Consumer<ActionEvent> tradeButtonAction;
    private final Consumer<ActionEvent> abortButtonAction;
    private final HBox mainBox = new HBox();
    private Node abortNode;
    private Node buildRoadNode;
    private Node buildVillageNode;
    private Node upgradeVillageNode;
    private Node buyDevelopmentCardNode;
    private Node playDevelopmentCardNode;
    private Node endTurnNode;
    private Node rollDiceNode;
    private Node tradeNode;

    /**
     * Creates a new PlayerActionsBuilder with the given actions.
     *
     * @param buildVillageButtonAction        The handler for the build village
     *                                        action.
     * @param upgradeVillageButtonAction      The handler for the upgrade village
     *                                        action.
     * @param buildRoadButtonAction           The handler for the build road action.
     * @param buyDevelopmentCardButtonAction  The handler for the buy development
     *                                        card action.
     * @param playDevelopmentCardButtonAction The handler for the play development
     *                                        card action.
     * @param endTurnButtonAction             The handler for the end turn action.
     * @param rollDiceButtonAction            The handler for the roll dice action.
     * @param tradeButtonAction               The handler for the trade action.
     * @param abortButtonAction               The handler for the abort action.
     */
    public PlayerActionsBuilder(
        final Consumer<ActionEvent> buildVillageButtonAction,
        final Consumer<ActionEvent> upgradeVillageButtonAction,
        final Consumer<ActionEvent> buildRoadButtonAction,
        final Consumer<ActionEvent> buyDevelopmentCardButtonAction,
        final Consumer<ActionEvent> playDevelopmentCardButtonAction,
        final Consumer<ActionEvent> endTurnButtonAction,
        final Consumer<ActionEvent> rollDiceButtonAction,
        final Consumer<ActionEvent> tradeButtonAction,
        final Consumer<ActionEvent> abortButtonAction
    ) {
        this.buildVillageButtonAction = buildVillageButtonAction;
        this.upgradeVillageButtonAction = upgradeVillageButtonAction;
        this.buildRoadButtonAction = buildRoadButtonAction;
        this.buyDevelopmentCardButtonAction = buyDevelopmentCardButtonAction;
        this.playDevelopmentCardButtonAction = playDevelopmentCardButtonAction;
        this.endTurnButtonAction = endTurnButtonAction;
        this.rollDiceButtonAction = rollDiceButtonAction;
        this.tradeButtonAction = tradeButtonAction;
        this.abortButtonAction = abortButtonAction;
    }

    @Override
    public Region build() {
        mainBox.getChildren().clear();

        final Button buildRoadButton = new Button("Build Road");
        buildRoadButton.setOnAction(buildRoadButtonAction::accept);
        this.buildRoadNode = buildRoadButton;

        final Button buildVillageButton = new Button("Build Village");
        buildVillageButton.setOnAction(buildVillageButtonAction::accept);
        this.buildVillageNode = buildVillageButton;

        final Button upgradeVillageButton = new Button("Build City");
        upgradeVillageButton.setOnAction(upgradeVillageButtonAction::accept);
        this.upgradeVillageNode = upgradeVillageButton;

        final Button buyDevelopmentCardButton = new Button("Buy Development Card");
        buyDevelopmentCardButton.setOnAction(buyDevelopmentCardButtonAction::accept);
        this.buyDevelopmentCardNode = buyDevelopmentCardButton;

        final Button playDevelopmentCardButton = new Button("Play Development Card");
        playDevelopmentCardButton.setOnAction(playDevelopmentCardButtonAction::accept);
        this.playDevelopmentCardNode = playDevelopmentCardButton;

        final Button endTurnButton = new Button("End Turn");
        endTurnButton.setOnAction(endTurnButtonAction::accept);
        this.endTurnNode = endTurnButton;

        final Button rollDiceButton = new Button("Roll Dice");
        rollDiceButton.setOnAction(rollDiceButtonAction::accept);
        this.rollDiceNode = rollDiceButton;

        final Button tradeButton = new Button("Trade");
        tradeButton.setDisable(true);
        tradeButton.setOnAction(tradeButtonAction::accept);
        this.tradeNode = tradeButton;

        final Button abortButton = new Button("Cancel");
        abortButton.setDisable(true);
        abortButton.setOnAction(abortButtonAction::accept);
        this.abortNode = abortButton;

        mainBox.getChildren().addAll(tradeButton, buildRoadButton, buildVillageButton, upgradeVillageButton,
                                     buyDevelopmentCardButton, playDevelopmentCardButton, rollDiceButton, endTurnButton, abortButton
        );
        mainBox.setSpacing(5);
        mainBox.setPadding(new Insets(10));
        return mainBox;
    }

    /**
     * Disables all buttons in the view.
     */
    public void disableAllButtons() {
        mainBox.getChildren().stream().filter(Button.class::isInstance).map(node -> (Button) node)
            .forEach(button -> button.setDisable(true));
    }

    /**
     * Enables all buttons in the view.
     */
    public void enableAllButtons() {
        mainBox.getChildren().stream().filter(Button.class::isInstance).map(node -> (Button) node)
            .forEach(button -> button.setDisable(false));
    }

    /**
     * Disables the abort button.
     */
    public void disableAbortButton() {
        abortNode.setDisable(true);
    }

    /**
     * Enables the abort button.
     */
    public void enableAbortButton() {
        abortNode.setDisable(false);
    }

    /**
     * Disables the build road button.
     */
    public void disableBuildRoadButton() {
        buildRoadNode.setDisable(true);
    }

    /**
     * Enables the build road button.
     */
    public void enableBuildRoadButton() {
        buildRoadNode.setDisable(false);
    }

    /**
     * Disables the build village button.
     */
    public void disableBuildVillageButton() {
        buildVillageNode.setDisable(true);
    }

    /**
     * Enables the build village button.
     */
    public void enableBuildVillageButton() {
        buildVillageNode.setDisable(false);
    }

    /**
     * Disables the upgrade village button.
     */
    public void disableUpgradeVillageButton() {
        upgradeVillageNode.setDisable(true);
    }

    /**
     * Enables the upgrade village button.
     */
    public void enableUpgradeVillageButton() {
        upgradeVillageNode.setDisable(false);
    }

    /**
     * Disables the buy development card button.
     */
    public void disableBuyDevelopmentCardButton() {
        buyDevelopmentCardNode.setDisable(true);
    }

    /**
     * Enables the buy development card button.
     */
    public void enableBuyDevelopmentCardButton() {
        buyDevelopmentCardNode.setDisable(false);
    }

    /**
     * Disables the play development card button.
     */
    public void disablePlayDevelopmentCardButton() {
        playDevelopmentCardNode.setDisable(true);
    }

    /**
     * Enables the play development card button.
     */
    public void enablePlayDevelopmentCardButton() {
        playDevelopmentCardNode.setDisable(false);
    }

    /**
     * Disables the end turn button.
     */
    public void disableEndTurnButton() {
        endTurnNode.setDisable(true);
    }

    /**
     * Enables the end turn button.
     */
    public void enableEndTurnButton() {
        endTurnNode.setDisable(false);
    }

    /**
     * Disables the roll dice button.
     */
    public void disableRollDiceButton() {
        rollDiceNode.setDisable(true);
    }

    /**
     * Enables the roll dice button.
     */
    public void enableRollDiceButton() {
        rollDiceNode.setDisable(false);
    }

    /**
     * Disables the trade button.
     */
    public void disableTradeButton() {
        tradeNode.setDisable(true);
    }

    /**
     * Enables the trade button.
     */
    public void enableTradeButton() {
        tradeNode.setDisable(false);
    }
}
