
package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Board; import edu.ntnu.iir.bidata.object.LadderAction; import edu.ntnu.iir.bidata.object.Tile;

import javafx.scene.Node; import javafx.scene.image.Image; import javafx.scene.image.ImageView; import javafx.scene.layout.StackPane;

public class LadderView {


    private Board board;

    public LadderView(Board board) {
        this.board = board;
    }

    public Node createLadder(Tile tile) {
        Tile startTile = board.getTile(tile.getIndex());
        Tile endTile = (tile.getTileAction() instanceof LadderAction action) ? board.getTile(action.getDestinationTileIndex()) : board.getTile(0);

        String color = (startTile.getIndex() < endTile.getIndex()) ? "Blue" : "Red";

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

        int x_tile = board.getX_dimension();
        int y_tile = board.getY_dimension();

        //positions, angles and flips the start wormhole
        start.setRotate(angle + 180);
        start.setTranslateX(startEdgeX -  (x_tile + 1) * 35);
        start.setTranslateY(startEdgeY - (y_tile +1) * 35);

        //positions and angles the start wormhole
        end.setRotate(angle);
        end.setTranslateX(endEdgeX - (x_tile +1) * 35);
        end.setTranslateY(endEdgeY - (y_tile + 1) * 35);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(start, end);

        return stackPane;
    }

    private double getTileCenterX(Tile tile) {
        return tile.getX() * 70 + 70;
    }

    private double getTileCenterY(Tile tile) {
        return tile.getY() * 70 + 70;
    }


    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        return imageView;
    }
}