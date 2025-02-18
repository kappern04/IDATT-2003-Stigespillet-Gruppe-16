package edu.ntnu.iir.bidata.object;

/** Tile class represents the different tiles in the game. Each tile has a different action. */
public class Tile {
  private int tileNumber;
  private TileAction tileAction;

  public Tile(int tileNumber, TileAction tileAction) {
    this.tileNumber = tileNumber;
    this.tileAction = tileAction;
  }

  public int getTileNumber() {
    return tileNumber;
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
