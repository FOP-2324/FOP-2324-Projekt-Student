package projekt.controller.actions;

import projekt.controller.PlayerController;
import projekt.model.TilePosition;

/**
 * An action to select the robber tile.
 *
 * @param tilePosition the position of the tile the robber should be placed on
 */
public record SelectRobberTileAction(TilePosition tilePosition) implements PlayerAction {

    /**
     * Sets the robber position.
     */
    @Override
    public void execute(final PlayerController pc) throws IllegalActionException {
        pc.setRobberPosition(tilePosition);
    }

}
