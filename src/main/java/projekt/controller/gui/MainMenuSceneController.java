package projekt.controller.gui;

import javafx.scene.layout.Region;
import javafx.util.Builder;
import projekt.view.menus.MainMenuBuilder;

/**
 * The controller for the main menu scene.
 */
public class MainMenuSceneController implements SceneController {
    private final Builder<Region> builder;

    /**
     * Creates a new main menu controller.
     */
    public MainMenuSceneController() {
        builder = new MainMenuBuilder(SceneController::quit, SceneController::loadCreateGameScene,
                                      SceneController::loadSettingsScene, SceneController::loadHighscoreScene,
                                      SceneController::loadAboutScene
        );
    }

    @Override
    public String getTitle() {
        return "Hauptmenü";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

}
