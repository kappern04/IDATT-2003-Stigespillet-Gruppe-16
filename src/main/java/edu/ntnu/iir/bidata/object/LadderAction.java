package edu.ntnu.iir.bidata.object;

/**
 * This class represents the action of moving up or down a ladder.
 */
public class LadderAction extends TileAction {
  private int destinationTile;

  public LadderAction(int destinationTile) {
    this.destinationTile = destinationTile;
  }

  @Override
  public void performAction(Player player) {
    player.setPosition(destinationTile);
  }

  public int getDestinationTile() {
    return destinationTile;
  }

  public void setDestinationTile(int destinationTile) {
    this.destinationTile = destinationTile;
  }

  @Override
  public String toString() {
    return "LadderAction{" +
        "destinationTile=" + destinationTile +
        '}';
  }
}