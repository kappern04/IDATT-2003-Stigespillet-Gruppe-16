// Implementation for a standard snake-like board layout
package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;

public class SnakeBoardPlayerView extends PlayerView {

  private static final int TILE_SIZE = 70;
  private static final int BOARD_OFFSET_X = 350;
  private static final int BOARD_OFFSET_Y = 385;
  private static final int BOARD_WIDTH = 9;

  public SnakeBoardPlayerView(Board board, Player[] players) {
    super(board, players);
  }

  @Override
  protected double getTilePositionX(Tile tile) {
    int col = (tile.getTileNumber() - 1) % BOARD_WIDTH;
    if (isOddRow(tile)) {
      col = BOARD_WIDTH - 1 - col;
    }
    return col * TILE_SIZE + TILE_SIZE - BOARD_OFFSET_X;
  }

  @Override
  protected double getTilePositionY(Tile tile) {
    int row = (tile.getTileNumber() - 1) / BOARD_WIDTH;
    return (BOARD_WIDTH - row) * TILE_SIZE + TILE_SIZE - BOARD_OFFSET_Y;
  }

  @Override
  protected double getRotationForTile(Tile tile) {
    return isOddRow(tile) ? -90 : 90;
  }

  private boolean isOddRow(Tile tile) {
    return ((tile.getTileNumber() - 1) / BOARD_WIDTH) % 2 == 1;
  }
}