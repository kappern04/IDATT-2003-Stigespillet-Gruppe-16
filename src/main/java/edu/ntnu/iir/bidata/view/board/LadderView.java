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

public class LadderView {
    private static final int WORMHOLE_WIDTH = 36;
    private static final int WORMHOLE_HEIGHT = 40;

    public Node createLadderVisual(Node fromTileNode, Node toTileNode, boolean isLadderUp) {
        Pane container = new Pane();
        container.setPickOnBounds(false);

        Line line = new Line();
        bindCenter(line.startXProperty(), fromTileNode.layoutXProperty(), fromTileNode.boundsInParentProperty(), true);
        bindCenter(line.startYProperty(), fromTileNode.layoutYProperty(), fromTileNode.boundsInParentProperty(), false);
        bindCenter(line.endXProperty(), toTileNode.layoutXProperty(), toTileNode.boundsInParentProperty(), true);
        bindCenter(line.endYProperty(), toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty(), false);

        String color = isLadderUp ? "Blue" : "Red";
        line.setStroke(isLadderUp ? Color.web("00A1C5") : Color.web("C50055"));
        line.setStrokeWidth(2);

        Pane startWormholeContainer = (Pane) createWormholeImage(color, true);
        Pane endWormholeContainer = (Pane) createWormholeImage(color, false);

        bindNodeToTile(startWormholeContainer, fromTileNode, 0, 0);
        bindNodeToTile(endWormholeContainer, toTileNode, 0, 0);

        startWormholeContainer.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x1 = fromTileNode.getLayoutX() + fromTileNode.getBoundsInParent().getWidth() / 2;
                    double y1 = fromTileNode.getLayoutY() + fromTileNode.getBoundsInParent().getHeight() / 2;
                    double x2 = toTileNode.getLayoutX() + toTileNode.getBoundsInParent().getWidth() / 2;
                    double y2 = toTileNode.getLayoutY() + toTileNode.getBoundsInParent().getHeight() / 2;
                    return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90 + 180;
                }, fromTileNode.layoutXProperty(), fromTileNode.layoutYProperty(),
                fromTileNode.boundsInParentProperty(), toTileNode.layoutXProperty(),
                toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty()));

        endWormholeContainer.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x1 = fromTileNode.getLayoutX() + fromTileNode.getBoundsInParent().getWidth() / 2;
                    double y1 = fromTileNode.getLayoutY() + fromTileNode.getBoundsInParent().getHeight() / 2;
                    double x2 = toTileNode.getLayoutX() + toTileNode.getBoundsInParent().getWidth() / 2;
                    double y2 = toTileNode.getLayoutY() + toTileNode.getBoundsInParent().getHeight() / 2;
                    return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
                }, fromTileNode.layoutXProperty(), fromTileNode.layoutYProperty(),
                fromTileNode.boundsInParentProperty(), toTileNode.layoutXProperty(),
                toTileNode.layoutYProperty(), toTileNode.boundsInParentProperty()));

        container.getChildren().addAll(line, startWormholeContainer, endWormholeContainer);
        addPulseAnimation(container, line, isLadderUp ? Color.web("00A1C5") : Color.web("C50055"));

        return container;
    }

    private void bindNodeToTile(Node node, Node tileNode, double offsetX, double offsetY) {
        node.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> tileNode.getLayoutX() + tileNode.getBoundsInParent().getWidth() / 2 - WORMHOLE_WIDTH / 2 + offsetX,
                tileNode.layoutXProperty(), tileNode.boundsInParentProperty()));

        node.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> tileNode.getLayoutY() + tileNode.getBoundsInParent().getHeight() / 2 - WORMHOLE_HEIGHT / 2 + offsetY,
                tileNode.layoutYProperty(), tileNode.boundsInParentProperty()));
    }

    private void bindCenter(DoubleProperty lineCoord, ReadOnlyDoubleProperty layoutPos, ReadOnlyObjectProperty<Bounds> bounds, boolean isX) {
        lineCoord.bind(Bindings.createDoubleBinding(
                () -> layoutPos.get() + (isX ? bounds.get().getWidth() / 2 : bounds.get().getHeight() / 2),
                layoutPos, bounds
        ));
    }

    private Node createWormholeImage(String color, boolean pullingIn) {
        Pane wormholeContainer = new Pane();
        wormholeContainer.setPickOnBounds(false);

        ImageView imageView = createImageView("/image/wormhole/" + color + "Wormhole.png");
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        wormholeContainer.getChildren().add(imageView);

        // Optionally: animateWormholeImage(imageView, pullingIn);

        return wormholeContainer;
    }

    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }

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