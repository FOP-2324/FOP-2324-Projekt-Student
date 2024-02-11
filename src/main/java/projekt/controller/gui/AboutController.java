package projekt.controller.gui;

import javafx.scene.layout.Region;
import javafx.util.Builder;
import projekt.view.menus.AboutBuilder;

/**
 * The controller for the about scene.
 */
public class AboutController implements SceneController {
    @Override
    public Builder<Region> getBuilder() {
        return new AboutBuilder(SceneController::loadMainMenuScene);
    }

    @Override
    public String getTitle() {
        return "About FOP-Project WiSe 23/24";
    }
}
