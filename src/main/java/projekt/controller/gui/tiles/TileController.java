package projekt.controller.gui.tiles;

import projekt.controller.gui.Controller;
import projekt.model.tiles.Tile;
import projekt.view.tiles.TileBuilder;

import java.util.function.Consumer;

/**
 * The controller for a tile.
 */
public class TileController implements Controller {
    private final TileBuilder builder;

    /**
     * Creates a new tile controller.
     *
     * @param tile the tile to render
     */
    public TileController(final Tile tile) {
        builder = new TileBuilder(tile);
    }

    /**
     * Returns the tile.
     *
     * @return the tile
     */
    public Tile getTile() {
        return builder.getTile();
    }

    /**
     * Highlights the tile.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void highlight(final Consumer<Tile> handler) {
        builder.highlight(() -> handler.accept(getTile()));
    }

    /**
     * Unhighlights the tile.
     */
    public void unhighlight() {
        builder.unhighlight();
    }

    @Override
    public TileBuilder getBuilder() {
        return builder;
    }
}
