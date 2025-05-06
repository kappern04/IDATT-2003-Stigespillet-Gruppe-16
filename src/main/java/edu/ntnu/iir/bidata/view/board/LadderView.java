package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LadderView {
    private static final int WORMHOLE_WIDTH = 36;
    private static final int WORMHOLE_HEIGHT = 40;
    private final Board board;

    public LadderView(Board board) {
        this.board = board;
    }

    /**
     * Creates a ladder between two tiles.
     * @param fromTile
     * @param toTile
     * @param fromTileNode
     * @param toTileNode
     * @return
     */
    public Node createLadder(Tile fromTile, Tile toTile, Node fromTileNode, Node toTileNode) {
        Pane container = new Pane();
        container.setPickOnBounds(false);

        Line line = new Line();
        bindCenter(line.startXProperty(), fromTileNode.layoutXProperty(), fromTileNode.boundsInParentProperty(), true);
        bindCenter(line.startYProperty(), fromTileNode.layoutYProperty(), fromTileNode.boundsInParentProperty(), false);
        bindCenter(line.endXProperty(), toTileNode.layoutXProperty(), toTileNode.boundsInParentProperty(), true);
        bindCenter(line.endYProperty(), toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty(), false);

        boolean isLadderUp = fromTile.getIndex() < toTile.getIndex();
        String color = isLadderUp ? "Blue" : "Red";
        line.setStroke(isLadderUp ? Color.web("00A1C5") : Color.web("C50055"));
        line.setStrokeWidth(2);

        ImageView startWormhole = createWormholeImage(color);
        ImageView endWormhole = createWormholeImage(color);

        bindImageToTile(startWormhole, fromTileNode);
        bindImageToTile(endWormhole, toTileNode);

        startWormhole.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x1 = fromTileNode.getLayoutX() + fromTileNode.getBoundsInParent().getWidth() / 2;
                    double y1 = fromTileNode.getLayoutY() + fromTileNode.getBoundsInParent().getHeight() / 2;
                    double x2 = toTileNode.getLayoutX() + toTileNode.getBoundsInParent().getWidth() / 2;
                    double y2 = toTileNode.getLayoutY() + toTileNode.getBoundsInParent().getHeight() / 2;
                    return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90 + 180;
                }, fromTileNode.layoutXProperty(), fromTileNode.layoutYProperty(),
                fromTileNode.boundsInParentProperty(), toTileNode.layoutXProperty(),
                toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty()));

        endWormhole.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x1 = fromTileNode.getLayoutX() + fromTileNode.getBoundsInParent().getWidth() / 2;
                    double y1 = fromTileNode.getLayoutY() + fromTileNode.getBoundsInParent().getHeight() / 2;
                    double x2 = toTileNode.getLayoutX() + toTileNode.getBoundsInParent().getWidth() / 2;
                    double y2 = toTileNode.getLayoutY() + toTileNode.getBoundsInParent().getHeight() / 2;
                    return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
                }, fromTileNode.layoutXProperty(), fromTileNode.layoutYProperty(),
                fromTileNode.boundsInParentProperty(), toTileNode.layoutXProperty(),
                toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty()));

        container.getChildren().addAll(line, startWormhole, endWormhole);
        return container;
    }

    /**
     * Binds the center of a line to the center of a tile.
     * @param lineCoord
     * @param layoutPos
     * @param bounds
     * @param isX
     */
    private void bindCenter(DoubleProperty lineCoord, ReadOnlyDoubleProperty layoutPos, ReadOnlyObjectProperty<Bounds> bounds, boolean isX) {
        lineCoord.bind(Bindings.createDoubleBinding(
                () -> layoutPos.get() + (isX ? bounds.get().getWidth() / 2 : bounds.get().getHeight() / 2),
                layoutPos, bounds
        ));
    }

    /**
     * Binds the image to the center of a tile.
     * @param imageView
     * @param tileNode
     */
    private void bindImageToTile(ImageView imageView, Node tileNode) {
        imageView.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> tileNode.getLayoutX() + tileNode.getBoundsInParent().getWidth() / 2 - imageView.getFitWidth() / 2,
                tileNode.layoutXProperty(), tileNode.boundsInParentProperty(), imageView.fitWidthProperty()));

        imageView.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> tileNode.getLayoutY() + tileNode.getBoundsInParent().getHeight() / 2 - imageView.getFitHeight() / 2,
                tileNode.layoutYProperty(), tileNode.boundsInParentProperty(), imageView.fitHeightProperty()));
    }

    /**
     * Creates a wormhole image.
     * @param color
     * @return
     */
    private ImageView createWormholeImage(String color) {
        ImageView imageView = createImageView("/image/wormhole/" + color + "Wormhole.png");
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }

    /**
     * Creates an image view for the wormhole.
     * @param path
     * @return
     */
    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }
}