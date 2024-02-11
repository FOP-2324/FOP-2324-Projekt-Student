package projekt.controller.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.controller.GameController;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A SceneSwitcher is responsible for switching between the different
 * {@link Scene}s.
 * It is a singleton and can be accessed via {@link #getInstance()}.
 */
@DoNotTouch
public class SceneSwitcher {
    private final Stage stage;
    private GameController gameController;
    private static SceneSwitcher INSTANCE;
    private final Consumer<GameController> gameLoopStarter;

    /**
     * Creates a new SceneSwitcher.
     *
     * @param stage           The {@link Stage} to show the {@link Scene} on.
     * @param gameLoopStarter The consumer that starts the game loop.
     */
    @DoNotTouch
    private SceneSwitcher(final Stage stage, final Consumer<GameController> gameLoopStarter) {
        this.stage = stage;
        this.gameLoopStarter = gameLoopStarter;
    }

    /**
     * Returns the instance of the SceneSwitcher.
     * Creates a new instance if it does not exist yet.
     *
     * @param stage           The {@link Stage} to show the {@link Scene} on.
     * @param gameLoopStarter The consumer that starts the game loop.
     * @return The instance of the SceneSwitcher.
     */
    @DoNotTouch
    public static SceneSwitcher getInstance(final Stage stage, final Consumer<GameController> gameLoopStarter) {
        if (INSTANCE == null) {
            INSTANCE = new SceneSwitcher(stage, gameLoopStarter);
        }
        return INSTANCE;
    }

    /**
     * Returns the instance of the SceneSwitcher.
     * Throws an {@link IllegalStateException} if the instance does not exist yet.
     *
     * @return The instance of the SceneSwitcher.
     */
    @DoNotTouch
    public static SceneSwitcher getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SceneSwitcher has not been initialized yet.");
        }
        return INSTANCE;
    }

    /**
     * The different types of scenes that can be loaded.
     */
    public enum SceneType {
        MAIN_MENU(MainMenuSceneController::new),
        CREATE_GAME(() -> {
            SceneSwitcher.getInstance().gameController = new GameController();
            return new CreateGameController(SceneSwitcher.getInstance().gameController.getState());
        }),
        GAME_BOARD(() -> {
            SceneSwitcher.getInstance().gameLoopStarter.accept(SceneSwitcher.getInstance().gameController);
            return new GameBoardController(
                getInstance().gameController.getState(),
                getInstance().gameController.getActivePlayerControllerProperty(),
                getInstance().gameController.getCurrentDiceRollProperty(),
                getInstance().gameController.getState().getWinnerProperty(),
                getInstance().gameController.getRoundCounterProperty()
            );
        }),
        ABOUT(AboutController::new);

        private final Supplier<SceneController> controller;

        /**
         * Creates a new SceneType.
         *
         * @param controller The controller to use for the scene.
         */
        SceneType(final Supplier<SceneController> controller) {
            this.controller = controller;
        }
    }

    /**
     * Loads the given {@link SceneType} and shows it on the {@link Stage}.
     *
     * @param sceneType The type of the scene to load.
     */
    @DoNotTouch
    public void loadScene(final SceneType sceneType) {
        System.out.println("Loading scene: " + sceneType);
        final SceneController controller = sceneType.controller.get();
        final Scene scene = new Scene(controller.buildView());
        scene.getStylesheets().add("css/hexmap.css");
        stage.setScene(scene);
        stage.setTitle(controller.getTitle());
        stage.show();
    }
}
