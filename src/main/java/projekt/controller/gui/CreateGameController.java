package projekt.controller.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Builder;
import projekt.Config;
import projekt.model.GameState;
import projekt.model.PlayerImpl;
import projekt.view.menus.CreateGameBuilder;

/**
 * The controller for the create game scene.
 */
public class CreateGameController implements SceneController {
    private final CreateGameBuilder builder;
    private final GameState gameState;
    private final ObservableList<PlayerImpl.Builder> playerBuilderList = FXCollections.observableArrayList();

    /**
     * Creates a new create game controller.
     *
     * @param gameState the game state to create the game in
     */
    public CreateGameController(final GameState gameState) {
        this.gameState = gameState;
        this.builder = new CreateGameBuilder(
            this.playerBuilderList,
            SceneController::loadMainMenuScene,
            this::startGameHandler
        );
    }

    @Override
    public Region buildView() {
        final var tmp = SceneController.super.buildView();
        // initial Players
        this.playerBuilderList.add(
            this.builder.nextPlayerBuilder()
                .name(System.getProperty("user.name"))
                .color(Color.AQUA));
        this.playerBuilderList.add(this.builder.nextPlayerBuilder());
        return tmp;
    }

    /**
     * The handler for the start game button.
     * <p>
     * Tries to start the game with the current players. If there are not enough
     * players, the game will not start.
     *
     * @return true if the game was started, false if not
     */
    private boolean startGameHandler() {
        if (this.playerBuilderList.size() < Config.MIN_PLAYERS) {
            return false;
        }
        this.playerBuilderList.forEach(p -> this.gameState.addPlayer(p.build(this.gameState.getGrid())));
        SceneController.loadGameScene();
        return true;
    }

    @Override
    public Builder<Region> getBuilder() {
        return this.builder;
    }

    @Override
    public String getTitle() {
        return "Create Game";
    }

}
