package projekt.view.menus;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * A Builder to create the main menu.
 * The main menu has buttons to start a new game, open the settings, view the
 * highscores and open the about page.
 * The return button is used to quit the application.
 */
public class MainMenuBuilder extends MenuBuilder {
    private final Runnable loadGameScene;
    private final Runnable loadSettingsScene;
    private final Runnable loadHighscoreScene;
    private final Runnable loadAboutScene;

    /**
     * Creates a new MainMenuBuilder with the given handlers.
     *
     * @param quitHandler        The handler for the return button. Exits the
     *                           application.
     * @param createGameScene    The handler for the create game button. Opens the
     *                           game scene.
     * @param loadSettingsScene  The handler for the settings button. Opens the
     *                           settings scene.
     * @param loadHighscoreScene The handler for the highscores button. Opens the
     *                           highscores scene.
     * @param loadAboutScene     The handler for the about button. Opens the about
     *                           scene.
     */
    public MainMenuBuilder(
        final Runnable quitHandler, final Runnable createGameScene, final Runnable loadSettingsScene,
        final Runnable loadHighscoreScene, final Runnable loadAboutScene
    ) {
        super("Main Menu", "Quit", quitHandler);
        this.loadGameScene = createGameScene;
        this.loadSettingsScene = loadSettingsScene;
        this.loadHighscoreScene = loadHighscoreScene;
        this.loadAboutScene = loadAboutScene;
    }

    @Override
    protected Node initCenter() {
        final VBox mainBox = new VBox();
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setSpacing(10);

        final Button startButton = new Button("Create Game");
        startButton.setOnAction(e -> loadGameScene.run());

        final Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> loadSettingsScene.run());

        final Button scoresButton = new Button("Highscores");
        scoresButton.setOnAction(e -> loadHighscoreScene.run());

        final Button aboutButton = new Button("About");
        aboutButton.setOnAction(e -> loadAboutScene.run());

        mainBox.getChildren().addAll(startButton, settingsButton, scoresButton, aboutButton);

        return mainBox;
    }
}
