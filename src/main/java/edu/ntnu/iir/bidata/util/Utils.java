package edu.ntnu.iir.bidata.util;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;

public class Utils {

  public static double getTileCenterX(Tile tile) {
    return tile.getX() * 70 + 70;
  }

  public static double getPlayerTileCenterY(Tile tile) {
    return tile.getY() * 70 + 70;
  }

  public static double getTileCenterX(Tile tile, Board board) {
    int xDimension = board.getX_dimension();
    return tile.getX() * 70 + 70 - (xDimension + 1) * 35;
  }

  public static double getPlayerTileCenterY(Tile tile, Board board) {
    int yDimension = board.getY_dimension();
    return tile.getY() * 70 + 70 - (yDimension + 1) * 35;
  }

  public static double calculateRotation(Tile currentTile, Tile nextTile) {
    int dx = nextTile.getX() - currentTile.getX();
    int dy = nextTile.getY() - currentTile.getY();

    if (dx > 0) return 90;  // right
    if (dx < 0) return -90; // left
    if (dy > 0) return 180; // down
    return 0; // up
  }
}