package edu.ntnu.iir.bidata.laddergame.model;

/**
 * Tile action that moves a player to a fixed destination tile (a ladder or snake).
 * This is a pure model action; presentation concerns such as sound are handled by
 * the view layer.
 */
public class LadderAction extends DefaultTileAction {
  private int destinationTileIndex;

  public LadderAction(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
  }

  @Override
  public void execute(Player player) {
    player.setPositionIndex(destinationTileIndex);
  }

  @Override
  public String getDescription() {
    if (destinationTileIndex > 0) {
      return "Ladder to position " + destinationTileIndex;
    } else {
      return "Snake to position " + destinationTileIndex;
    }
  }

  public int getDestinationTileIndex() {
    return destinationTileIndex;
  }

  public void setDestinationTileIndex(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
  }

  @Override
  public String toString() {
    return "LadderAction{" + "destinationTile=" + destinationTileIndex + '}';
  }

  @Override
  public boolean leadsToPosition(Board board, int targetPosition) {
    return getDestinationTileIndex() == targetPosition;
  }
}
