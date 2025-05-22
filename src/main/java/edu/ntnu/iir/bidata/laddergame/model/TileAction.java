package edu.ntnu.iir.bidata.laddergame.model;

/**
 * This class is a placeholder for the different actions that can be performed on a tile. The
 * actions are determined by the type of tile the player lands on.
 */
public class TileAction {
  public void performAction(Player player) {
    // Default action (can be overridden by subclasses)
  }

  public boolean leadsToPosition(Board board, int targetPosition) {
    return false; // Base implementation returns false
  }
}
