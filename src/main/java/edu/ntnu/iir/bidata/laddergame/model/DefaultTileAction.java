package edu.ntnu.iir.bidata.laddergame.model;

/**
 * Default implementation of TileAction that does nothing when a player lands on it.
 */
public class DefaultTileAction implements TileAction {
    @Override
    public void execute(Player player) {
        // Default action does nothing
    }

    @Override
    public String getDescription() {
        return "Regular tile";
    }
}