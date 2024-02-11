package projekt.controller.gui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * The controller for a scene.
 */
@DoNotTouch
public interface SceneController extends Controller {
    /**
     * Specifies the title of the {@link Stage}.
     *
     * @return The title of the {@link Stage}.
     */
    String getTitle();

    // --Setup Methods-- //

    /**
     * Terminates the application.
     */
    static void quit() {
        Platform.exit();
    }

    /**
     * Loads the main menu scene.
     */
    static void loadMainMenuScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.MAIN_MENU);
    }

    /**
     * Loads the create game scene.
     */
    static void loadCreateGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.CREATE_GAME);
    }

    /**
     * Loads the settings scene.
     */
    static void loadSettingsScene() {
        System.out.println("Loading settings");
    }

    /**
     * Loads the highscore scene.
     */
    static void loadHighscoreScene() {
        System.out.println("Loading highscores");
    }

    /**
     * Loads the game scene.
     */
    static void loadGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.GAME_BOARD);
    }

    /**
     * Loads the about scene.
     */
    static void loadAboutScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.ABOUT);
    }
}
