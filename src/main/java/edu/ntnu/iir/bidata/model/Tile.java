package edu.ntnu.iir.bidata.model;

/**
 * Tile class represents the different tiles in the game. Each tile has a different action.
 */
public class Tile {

  private int index;
  private int x;
  private int y;
  private TileAction tileAction;

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
  }

  public boolean hasLadderAction() {
    return this.tileAction instanceof LadderAction;
  }

  /**
   * Checks if this tile is the destination of any LadderAction on the board.
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
    tileAction.performAction(player);
  }
}

