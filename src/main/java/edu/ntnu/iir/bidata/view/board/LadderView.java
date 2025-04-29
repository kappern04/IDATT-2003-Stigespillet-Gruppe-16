package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Tile;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Handles the visual representation of ladders (wormholes) on the game board.
 * Creates visual elements that connect tiles with ladder actions.
 */
public class LadderView {

    private static final int WORMHOLE_WIDTH = 36;
    private static final int WORMHOLE_HEIGHT = 40;
    private static final int TILE_SIZE = 70;
    private static final int TILE_CENTER_OFFSET = 35;
    private static final int TILE_VERTICAL_OFFSET = 35;

    private final Board board;

    /**
     * Creates a new LadderView for visualizing ladder connections.
     *
     * @param board the game board containing tile information
     */
    public LadderView(Board board) {
        this.board = board;
    }

    /**
     * Creates a visual representation of a ladder/wormhole between connected tiles.
     *
     * @param tile the starting tile with a ladder action
     * @return a Node containing both ends of the wormhole
     */
    public Node createLadder(Tile tile) {
        if (!(tile.getTileAction() instanceof LadderAction action)) {
            return new StackPane(); // Return empty pane if no ladder action
        }

        Tile startTile = board.getTile(tile.getIndex());
        Tile endTile = board.getTile(action.getDestinationTileIndex());

        // Determine color based on whether it's a ladder up or down
        String color = (startTile.getIndex() < endTile.getIndex()) ? "Blue" : "Red";

        // Calculate positions and angle
        double startX = getTileCenterX(startTile);
        double startY = getTileCenterY(startTile);
        double endX = getTileCenterX(endTile);
        double endY = getTileCenterY(endTile);

        // Calculate the angle between start and end tiles
        double angle = Math.toDegrees(Math.atan2(endY - startY, endX - startX)) + 90;

        // Create and position wormhole images
        ImageView startWormhole = createWormholeImage(color, angle + 180, startX, startY);
        ImageView endWormhole = createWormholeImage(color, angle, endX, endY);

        // Combine both wormholes into one container
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(startWormhole, endWormhole);

        return stackPane;
    }

    /**
     * Creates and positions a wormhole image with the specified rotation and position.
     *
     * @param color the color of the wormhole ("Blue" or "Red")
     * @param angle the rotation angle of the wormhole
     * @param x the x-coordinate of the wormhole
     * @param y the y-coordinate of the wormhole
     * @return the configured wormhole ImageView
     */
    private ImageView createWormholeImage(String color, double angle, double x, double y) {
        ImageView wormhole = createImageView("/image/wormhole/" + color + "Wormhole.png");
        wormhole.setRotate(angle);
        double boardOffsetX = getBoardOffsetX();
        double boardOffsetY = getBoardOffsetY();
        wormhole.setTranslateX(x - boardOffsetX);
        wormhole.setTranslateY(y - boardOffsetY);
        return wormhole;
    }

    /**
     * Calculates the center x-coordinate of a tile.
     *
     * @param tile the tile
     * @return the center x-coordinate
     */
    private double getTileCenterX(Tile tile) {
        return tile.getX() * TILE_SIZE + TILE_CENTER_OFFSET;
    }

    /**
     * Calculates the center y-coordinate of a tile.
     *
     * @param tile the tile
     * @return the center y-coordinate
     */
    private double getTileCenterY(Tile tile) {
        return tile.getY() * TILE_SIZE + TILE_VERTICAL_OFFSET;
    }

    private double getBoardOffsetX(){
        return (double) (TILE_SIZE * board.getX_dimension()) /2;
    }

    private double getBoardOffsetY(){
        return (double) (TILE_SIZE * board.getY_dimension()) /2;
    }

    /**
     * Creates an ImageView from the specified resource path.
     *
     * @param path the resource path to the image
     * @return the configured ImageView
     */
    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }
}