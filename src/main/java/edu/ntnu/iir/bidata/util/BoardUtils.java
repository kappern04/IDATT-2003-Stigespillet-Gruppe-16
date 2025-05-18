package edu.ntnu.iir.bidata.util;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;

public class BoardUtils {
    public static final int TILE_SIZE = 70;
    public static final int TILE_CENTER_OFFSET = TILE_SIZE/2;

    public static double getBoardOffsetX(Board board, Tile tile) {
        // Get actual min/max x values to center the board properly
        int minX = board.getTiles().stream().mapToInt(Tile::getX).filter(x -> x >= 0).min().orElse(0);
        int maxX = board.getTiles().stream().mapToInt(Tile::getX).max().orElse(0);
        int actualWidth = maxX - minX + 1;

        return (tile.getX()-minX) * TILE_SIZE + TILE_SIZE / 2 - actualWidth * TILE_SIZE / 2;
    }

    public static double getBoardOffsetY(Board board, Tile tile) {
        // Get actual min/max y values to center the board properly
        int minY = board.getTiles().stream().mapToInt(Tile::getY).filter(y -> y >= 0).min().orElse(0);
        int maxY = board.getTiles().stream().mapToInt(Tile::getY).max().orElse(0);
        int actualHeight = maxY - minY + 1;

        return (tile.getY()-minY) * TILE_SIZE + TILE_SIZE / 2 - actualHeight * TILE_SIZE / 2;
    }

    public static double getRotationForTile(Board board, Tile tile) {
        int currentIndex = tile.getIndex();
        if (currentIndex >= board.getTiles().size() - 1) return 0;
        Tile nextTile = board.getTiles().get(currentIndex + 1);
        int dx = nextTile.getX() - tile.getX();
        int dy = nextTile.getY() - tile.getY();
        if (dx > 0) return 90;
        if (dx < 0) return -90;
        if (dy > 0) return 180;
        return 0;
    }
}