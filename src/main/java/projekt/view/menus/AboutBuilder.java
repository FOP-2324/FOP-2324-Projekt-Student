package projekt.view.menus;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

/**
 * A Builder to create the about page.
 * The about page contains information about the project and copyright notices.
 */
public class AboutBuilder extends MenuBuilder {
    /**
     * Creates a new AboutBuilder with the given return handler.
     *
     * @param returnHandler The handler for the return button.
     */
    public AboutBuilder(final Runnable returnHandler) {
        super("About FOP-Project WiSe 23/24", returnHandler);
    }

    @Override
    protected Node initCenter() {
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setText(
            """
                Project for the subject "Functional and Objectoriented programming concepts" in the winter semester 2023/24.
                Based on the game: Die Siedler von Catan
                Icons from:
                    - https://github.com/Templarian/MaterialDesign
                    - Rocks/Ore: Created by Jan Niklas Prause from the Noun Project
                    - Roads: Created by Pause08 from the Noun Project
                    - Light bulb/Invention: Created by Pavitra from the Noun Project
                    - Knight: Created by Alex Tai from the Noun Project
                    - Monopoly: Created by icon trip from Noun Project
                    - All settlement icons: Created by Per Göttlicher
                All Icons from the Noun Project are licensed under CC BY 3.0
                """);
        return textArea;
    }

}
