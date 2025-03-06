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
import javafx.scene.transform.Rotate;


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

        //calculate the polar angle from the angle between the start and end tile
        double angle = Math.toDegrees(Math.atan2(endY - startY, endX - startX)) + 90;

        double offset = 0;
        double startEdgeX = startX + offset * Math.cos(Math.toRadians(angle - 90));
        double startEdgeY = startY + offset * Math.sin(Math.toRadians(angle - 90));
        double endEdgeX = endX + offset * Math.cos(Math.toRadians(angle + 90));
        double endEdgeY = endY + offset * Math.sin(Math.toRadians(angle + 90));

        ImageView start = createImageView("/image/" + color + "Wormhole.png");
        ImageView end = createImageView("/image/" + color + "Wormhole.png");

        //positions, angles and flips the start wormhole
        start.setRotate(angle + 180);
        start.setTranslateX(startEdgeX - 350);
        start.setTranslateY(startEdgeY - 385);

        //positions and angles the start wormhole
        end.setRotate(angle);
        end.setTranslateX(endEdgeX - 350);
        end.setTranslateY(endEdgeY - 385);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(start, end);

        return stackPane;
    }

    private double getTileCenterX(Tile tile) {
        int col = (tile.getTileNumber() - 1) % 9; // Get column index (0 to 8)

        if (((tile.getTileNumber() - 1) / 9) % 2 == 1) {
            col = 8 - col; // Reverse column order for odd rows (zig-zag pattern)
        }

        return col * 70 + 70; // Center of tile
    }


    private double getTileCenterY(Tile tile) {
        int row = (tile.getTileNumber() - 1) / 9; // Get row index (0 to 9)
        return (9 - row) * 70 + 70; // Center of tile
    }


    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        return imageView;
    }
}