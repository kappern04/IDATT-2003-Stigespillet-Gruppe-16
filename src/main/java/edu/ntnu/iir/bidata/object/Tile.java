package edu.ntnu.iir.bidata.object;

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

  public void landOn(Player player) {
    tileAction.performAction(player);
  }
}

