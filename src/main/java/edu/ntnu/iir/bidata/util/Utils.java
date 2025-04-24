//package edu.ntnu.iir.bidata.util;
//
//import edu.ntnu.iir.bidata.model.Board;
//import edu.ntnu.iir.bidata.model.Tile;
//
//public class Utils {
//  private static final int TILE_SIZE = 70;
//  private static final int TILE_OFFSET = 35;
//  private static Board board;
//
//  public double getTileCenterX(Tile tile) {
//    return tile.getX() * TILE_SIZE + TILE_OFFSET;
//  }
//
//  public double getTileCenterY(Tile tile) {
//    return tile.getY() * TILE_SIZE + TILE_OFFSET;
//  }
//
//  public static double getBoardOffsetX(Tile tile) {
//    int xDimension = board.getX_dimension();
//    return tile.getX() * TILE_SIZE + TILE_SIZE - (xDimension + 1) * TILE_OFFSET;
//  }
//
//  public static double getBoardOffsetY(Tile tile) {
//    int yDimension = board.getY_dimension();
//    return tile.getY() * TILE_SIZE + TILE_SIZE - (yDimension + 1) * TILE_OFFSET;
//  }
//
//  public static double calculateRotation(Tile currentTile, Tile nextTile) {
//    int dx = nextTile.getX() - currentTile.getX();
//    int dy = nextTile.getY() - currentTile.getY();
//
//    if (dx > 0) return 90;  // right
//    if (dx < 0) return -90; // left
//    if (dy > 0) return 180; // down
//    return 0; // up
//  }
//}