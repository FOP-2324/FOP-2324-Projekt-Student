package projekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * A Builder to create views for menus.
 * A menu is a view with a title, a button to return to the previous menu and a
 * center area that can be filled with content.
 */
public abstract class MenuBuilder implements Builder<Region> {
    /**
     * The root Region of the menu.
     */
    protected final BorderPane root = new BorderPane();
    private final String returnText;
    private final Runnable returnHandler;
    private final String title;

    /**
     * Creates a new MenuBuilder with the given title, return text and return
     * handler.
     *
     * @param title         The title of the menu.
     * @param returnText    The text of the return button.
     * @param returnHandler The handler for the return button.
     */
    public MenuBuilder(final String title, final String returnText, final Runnable returnHandler) {
        this.returnHandler = returnHandler;
        this.title = title;
        this.returnText = returnText;
    }

    /**
     * Creates a new MenuBuilder with the given title and return handler.
     * The return text is "Return".
     *
     * @param title         The title of the menu.
     * @param returnHandler The handler for the return button.
     */
    public MenuBuilder(final String title, final Runnable returnHandler) {
        this(title, "Return", returnHandler);
    }

    @Override
    public Region build() {
        init(title);
        return root;
    }

    /**
     * Initializes the menu with the given title.
     *
     * @param title The title of the menu.
     */
    protected void init(final String title) {
        final Label titleLabel = new Label(title);
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        titleLabel.setId("Title");
        titleLabel.setStyle("-fx-font-size: 50");

        root.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        final HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 20, 20, 20));
        buttonBox.setSpacing(10);

        final Button returnButton = new Button(returnText);
        returnButton.setMaxWidth(600);
        returnButton.setOnAction(e -> returnHandler.run());
        buttonBox.getChildren().add(returnButton);

        HBox.setHgrow(returnButton, Priority.ALWAYS);

        root.setBottom(buttonBox);

        root.setCenter(initCenter());
    }

    /**
     * Initializes the center of the menu.
     *
     * @return The Node to display in the center of the menu.
     */
    protected abstract Node initCenter();
}
