package edu.ntnu.iir.bidata.view.board;

import javafx.animation.*;
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
import javafx.util.Duration;
import edu.ntnu.iir.bidata.util.BoardUtils;

/**
 * Responsible for rendering ladder visuals (lines and wormholes) between tiles on the board.
 */
public class LadderView {
    private static final int WORMHOLE_WIDTH = 36;
    private static final int WORMHOLE_HEIGHT = 40;

    /**
     * Creates a visual representation of a ladder between two tile nodes.
     *
     * @param fromTileNode the node representing the start tile
     * @param toTileNode the node representing the destination tile
     * @param isLadderUp true if the ladder goes up, false if down
     * @return a Node containing the ladder visual
     */
    public Node createLadderVisual(Node fromTileNode, Node toTileNode, boolean isLadderUp) {
        Pane container = new Pane();
        container.setPickOnBounds(false);

        Line ladderLine = createLadderLine(fromTileNode, toTileNode, isLadderUp);

        Pane startWormhole = createWormholeContainer(isLadderUp ? "Blue" : "Red", true);
        Pane endWormhole = createWormholeContainer(isLadderUp ? "Blue" : "Red", false);

        BoardUtils.bindNodeToCenter(startWormhole, fromTileNode, WORMHOLE_WIDTH, WORMHOLE_HEIGHT, 0, 0);
        BoardUtils.bindNodeToCenter(endWormhole, toTileNode, WORMHOLE_WIDTH, WORMHOLE_HEIGHT, 0, 0);

        bindWormholeRotation(startWormhole, fromTileNode, toTileNode, true);
        bindWormholeRotation(endWormhole, fromTileNode, toTileNode, false);

        container.getChildren().addAll(ladderLine, startWormhole, endWormhole);
        addPulseAnimation(container, ladderLine, isLadderUp ? Color.web("00A1C5") : Color.web("C50055"));

        return container;
    }

    /**
     * Creates a line representing the ladder and binds its endpoints to the centers of the given nodes.
     */
    private Line createLadderLine(Node fromTileNode, Node toTileNode, boolean isLadderUp) {
        Line line = new Line();
        bindCenter(line.startXProperty(), fromTileNode.layoutXProperty(), fromTileNode.boundsInParentProperty(), true);
        bindCenter(line.startYProperty(), fromTileNode.layoutYProperty(), fromTileNode.boundsInParentProperty(), false);
        bindCenter(line.endXProperty(), toTileNode.layoutXProperty(), toTileNode.boundsInParentProperty(), true);
        bindCenter(line.endYProperty(), toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty(), false);

        line.setStroke(isLadderUp ? Color.web("00A1C5") : Color.web("C50055"));
        line.setStrokeWidth(2);
        return line;
    }

    /**
     * Creates a wormhole image container for the ladder end.
     */
    private Pane createWormholeContainer(String color, boolean pullingIn) {
        Pane wormholeContainer = new Pane();
        wormholeContainer.setPickOnBounds(false);

        ImageView imageView = createImageView("/image/wormhole/" + color + "Wormhole.png");
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        wormholeContainer.getChildren().add(imageView);

        return wormholeContainer;
    }

    /**
     * Loads an image and returns it as an ImageView.
     */
    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }



    /**
     * Binds a line coordinate to the center of a node, either X or Y.
     */
    private void bindCenter(DoubleProperty lineCoord, ReadOnlyDoubleProperty layoutPos, ReadOnlyObjectProperty<Bounds> bounds, boolean isX) {
        lineCoord.bind(Bindings.createDoubleBinding(
                () -> layoutPos.get() + (isX ? bounds.get().getWidth() / 2 : bounds.get().getHeight() / 2),
                layoutPos, bounds
        ));
    }

    /**
     * Binds the rotation of a wormhole container so it faces along the ladder line.
     */
    private void bindWormholeRotation(Pane wormhole, Node fromTileNode, Node toTileNode, boolean isStart) {
        wormhole.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x1 = fromTileNode.getLayoutX() + fromTileNode.getBoundsInParent().getWidth() / 2;
                    double y1 = fromTileNode.getLayoutY() + fromTileNode.getBoundsInParent().getHeight() / 2;
                    double x2 = toTileNode.getLayoutX() + toTileNode.getBoundsInParent().getWidth() / 2;
                    double y2 = toTileNode.getLayoutY() + toTileNode.getBoundsInParent().getHeight() / 2;
                    double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
                    return isStart ? angle + 180 : angle;
                }, fromTileNode.layoutXProperty(), fromTileNode.layoutYProperty(),
                fromTileNode.boundsInParentProperty(), toTileNode.layoutXProperty(),
                toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty()));
    }

    /**
     * Adds a pulsing animation along the ladder line.
     */
    private void addPulseAnimation(Pane container, Line line, Color color) {
        javafx.scene.shape.Circle pulse = new javafx.scene.shape.Circle(10, color);
        pulse.setEffect(new javafx.scene.effect.Glow(1.0));
        pulse.setOpacity(0.0);

        container.getChildren().add(pulse);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(1.5));
        pathTransition.setPath(line);
        pathTransition.setNode(pulse);
        pathTransition.setCycleCount(Animation.INDEFINITE);
        pathTransition.setInterpolator(Interpolator.LINEAR);
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);

        pathTransition.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double frac = newTime.toMillis() / pathTransition.getDuration().toMillis();
            if (frac < 0.15) {
                pulse.setOpacity(frac / 0.15);
            } else if (frac > 0.85) {
                pulse.setOpacity((1 - frac) / 0.15);
            } else {
                pulse.setOpacity(1.0);
            }
        });

        container.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                javafx.application.Platform.runLater(pathTransition::play);
            }
        });
    }
}