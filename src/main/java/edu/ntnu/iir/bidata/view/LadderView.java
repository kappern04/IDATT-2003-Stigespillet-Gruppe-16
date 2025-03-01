package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class LadderView {

    private Board board;

    public LadderView(Board board) {
        this.board = board;
    }

    public Node createLadder(String color, int actionTile, int destinationTile) {
        Tile startTile = board.getTiles()[actionTile];
        Tile endTile = board.getTiles()[destinationTile];

        double startX = getTileCenterX(startTile);
        double startY = getTileCenterY(startTile);
        double endX = getTileCenterX(endTile);
        double endY = getTileCenterY(endTile);

        ImageView start = createImageView("/image/" + color + "Wormhole.png");
        ImageView end = createImageView("/image/" + color + "Wormhole.png");

        // Adjust positions relative to StackPane's center
        start.setTranslateX(startX - 320); // Half of 640px width
        start.setTranslateY(startY - 352); // Half of 704px height
        end.setTranslateX(endX - 320);
        end.setTranslateY(endY - 352);


        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(start, end);

        return stackPane;
    }

    private double getTileCenterX(Tile tile) {
        int col = (tile.getTileNumber() - 1) % 9; // Get column index (0 to 8)

        if (((tile.getTileNumber() - 1) / 9) % 2 == 1) {
            col = 8 - col; // Reverse column order for odd rows (zig-zag pattern)
        }

        return col * 64 + 64; // Center of tile
    }


    private double getTileCenterY(Tile tile) {
        int row = (tile.getTileNumber() - 1) / 9; // Get row index (0 to 9)
        return (9 - row) * 64 + 64; // Center of tile
    }


    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        return imageView;
    }
}