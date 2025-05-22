package edu.ntnu.iir.bidata.laddergame.util;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.beans.binding.Bindings;

/**
 * Utility class for board-related calculations and positioning.
 */
public class BoardUtils {
    /** Standard size for tiles on the board */
    public static final int TILE_SIZE = 70;

    /** Offset to center items on tiles */
    public static final int TILE_CENTER_OFFSET = TILE_SIZE / 2;

    public static void bindNodeToCenter(Node node, Node targetNode, double nodeWidth, double nodeHeight, double offsetX, double offsetY) {
        node.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> targetNode.getLayoutX() + (targetNode.getBoundsInParent().getWidth() / 2) - (nodeWidth / 2) + offsetX,
                targetNode.layoutXProperty(), targetNode.boundsInParentProperty()));

        node.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> targetNode.getLayoutY() + (targetNode.getBoundsInParent().getHeight() / 2) - (nodeHeight / 2) + offsetY,
                targetNode.layoutYProperty(), targetNode.boundsInParentProperty()));
    }

    /**
     * Calculates the x-coordinate offset for a given tile on the board.
     * Uses the tile's node position when available.
     *
     * @param board the game board
     * @param tile the tile to position
     * @return the x-coordinate offset for the tile
     */
    public static double getBoardOffsetX(Board board, Tile tile) {
        // Try to use the tile's node if available
        Node tileNode = board.getTileNode(tile);
        if (tileNode != null && tileNode.getParent() != null) {
            Bounds bounds = tileNode.getBoundsInParent();
            return bounds.getMinX() + bounds.getWidth() / 2;
        }

        // Fall back to calculation based on logical coordinates
        int minX = board.getTiles().stream().mapToInt(Tile::getX).filter(x -> x >= 0).min().orElse(0);
        int maxX = board.getTiles().stream().mapToInt(Tile::getX).max().orElse(0);
        int actualWidth = maxX - minX + 1;

        return (tile.getX() - minX) * TILE_SIZE + TILE_SIZE / 2 - actualWidth * TILE_SIZE / 2;
    }

    /**
     * Calculates the y-coordinate offset for a given tile on the board.
     * Uses the tile's node position when available.
     *
     * @param board the game board
     * @param tile the tile to position
     * @return the y-coordinate offset for the tile
     */
    public static double getBoardOffsetY(Board board, Tile tile) {
        // Try to use the tile's node if available
        Node tileNode = board.getTileNode(tile);
        if (tileNode != null && tileNode.getParent() != null) {
            Bounds bounds = tileNode.getBoundsInParent();
            return bounds.getMinY() + bounds.getHeight() / 2;
        }

        // Fall back to calculation based on logical coordinates
        int minY = board.getTiles().stream().mapToInt(Tile::getY).filter(y -> y >= 0).min().orElse(0);
        int maxY = board.getTiles().stream().mapToInt(Tile::getY).max().orElse(0);
        int actualHeight = maxY - minY + 1;

        return (tile.getY() - minY) * TILE_SIZE + TILE_SIZE / 2 - actualHeight * TILE_SIZE / 2;
    }

    /**
     * Calculates the rotation angle for a player sprite at a given tile.
     * The rotation is based on the direction to the next tile on the board.
     *
     * @param board the game board
     * @param tile the tile where the player is positioned
     * @return the rotation angle in degrees (0, 90, -90, or 180)
     */
    public static double getRotationForTile(Board board, Tile tile) {
        int currentIndex = tile.getIndex();
        if (currentIndex >= board.getTiles().size() - 1) {
            return 0; // At the last tile, use default rotation
        }

        Tile nextTile = board.getTiles().get(currentIndex + 1);
        int dx = nextTile.getX() - tile.getX();
        int dy = nextTile.getY() - tile.getY();

        // Calculate rotation based on direction to next tile
        if (dx > 0) return 90;      // Moving right
        if (dx < 0) return -90;     // Moving left
        if (dy > 0) return 180;     // Moving down
        return 0;                   // Moving up (default)
    }
}