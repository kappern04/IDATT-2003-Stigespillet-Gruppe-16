package edu.ntnu.iir.bidata.laddergame.model;

/**
 * Interface for the different actions that can be performed on a tile.
 * The actions are determined by the type of tile the player lands on.
 */
public interface TileAction {
  /**
   * Execute the action for the given player.
   * @param player the player who landed on the tile
   */
  void execute(Player player);

  /**
   * Get a description of this tile action.
   * @return a string describing this tile action
   */
  String getDescription();

  /**
   * Determines if this action leads to a specific position on the board.
   * @param board the game board
   * @param targetPosition the target position to check
   * @return true if this action leads to the target position
   */
  default boolean leadsToPosition(Board board, int targetPosition) {
    return false; // Default implementation returns false
  }
}