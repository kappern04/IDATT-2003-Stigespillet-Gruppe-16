package edu.ntnu.iir.bidata.laddergame.model;

/**
 * Tile class represents the different tiles in the game. Each tile has a different action.
 */
public class Tile {

  private int index;
  private int x;
  private int y;
  private TileAction tileAction;
  private String type = "normal"; // Default type is normal

  public Tile(int index, int x, int y, TileAction tileAction) {
    this.index = index;
    this.x = x;
    this.y = y;
    this.tileAction = tileAction;
  }

  public int getIndex() {
    return index;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public TileAction getTileAction() {
    return tileAction;
  }

  public void setTileAction(TileAction tileAction) {
    this.tileAction = tileAction;

    // Update tile type based on action
    if (tileAction instanceof LadderAction) {
      this.type = "ladder";
    } else if (tileAction instanceof CosmicChanceAction) {
      this.type = "chance";
    }
  }

  /**
   * Gets the type of the tile.
   *
   * @return The tile type ("normal", "ladder", "chance", etc.)
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of the tile.
   *
   * @param type The new tile type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Checks if this tile has a ladder action.
   *
   * @return true if this tile has a ladder action, false otherwise
   */
  public boolean hasLadderAction() {
    return this.tileAction instanceof LadderAction;
  }

  /**
   * Checks if this tile has a chance action.
   *
   * @return true if this tile has a chance action, false otherwise
   */
  public boolean hasChanceAction() {
    return this.tileAction instanceof CosmicChanceAction;
  }

  /**
   * Checks if this tile is the destination of a ladder.
   *
   * @param board The game board
   * @return true if this tile is the destination of a ladder, false otherwise
   */
  public boolean isDestinationOfLadder(Board board) {
    for (Tile tile : board.getTiles()) {
      if (tile.getTileAction() instanceof LadderAction action) {
        if (action.getDestinationTileIndex() == this.index) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns the destination tile of the ladder if this tile is a ladder.
   *
   * @param board The game board
   * @return The destination tile of the ladder, or null if this tile is not a ladder
   */
  public Tile getLadderDestination(Board board) {
    if (this.tileAction instanceof LadderAction action) {
      int destIndex = action.getDestinationTileIndex();
      for (Tile t : board.getTiles()) {
        if (t.getIndex() == destIndex) {
          return t;
        }
      }
    }
    return null;
  }

  public void landOn(Player player) {
    tileAction.execute(player);
  }
}