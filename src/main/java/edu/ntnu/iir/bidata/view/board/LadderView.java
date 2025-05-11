package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class LadderView {
    private static final int WORMHOLE_WIDTH = 36;
    private static final int WORMHOLE_HEIGHT = 40;
    private final Board board;

    public LadderView(Board board) {
        this.board = board;
    }

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

        // Get the wormhole containers
        Pane startWormholeContainer = (Pane) createWormholeImage(color, true);  // pulling in
        Pane endWormholeContainer = (Pane) createWormholeImage(color, false);   // pulling out

        // Bind the containers to the tiles
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


// Other wormholes 60x60 px
//        if (isLadderUp) {
//            bindNodeToTile(startWormholeContainer, fromTileNode, 0, -16);
//            bindNodeToTile(endWormholeContainer, toTileNode, 0, 16);
//            startWormholeContainer.setRotate(-90);
//            endWormholeContainer.setRotate(90);
//        } else {
//            // Entry at top of start tile, exit at bottom of end tile
//            bindNodeToTile(startWormholeContainer, fromTileNode, 0, 16);
//            bindNodeToTile(endWormholeContainer, toTileNode, 0, -16);
//            startWormholeContainer.setRotate(90);
//            endWormholeContainer.setRotate(-90);
//        }

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

    /**
     * Creates a wormhole image with the specified color and animation direction.
     *
     * @param color       The color of the wormhole (e.g., "Blue", "Red").
     * @param pullingIn   If true, the wormhole is animated to pull in; otherwise, it spits out.
     * @return A Pane containing the wormhole image and animation.
     */
    private Node createWormholeImage(String color, boolean pullingIn) {
        // Create a container that will hold both the image and particles
        Pane wormholeContainer = new Pane();
        wormholeContainer.setPickOnBounds(false);

        // Add the image to the container
        ImageView imageView = createImageView("/image/wormhole/" + color + "Wormhole.png");
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        wormholeContainer.getChildren().add(imageView);

        // Animate the wormhole
//        animateWormholeImage(imageView, pullingIn);

        return wormholeContainer;
    }

    /**
     * Creates an ImageView for the wormhole image.
     *
     * @param path The path to the image resource.
     * @return An ImageView with the specified image.
     */
    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WORMHOLE_WIDTH);
        imageView.setFitHeight(WORMHOLE_HEIGHT);
        return imageView;
    }

    private void animateWormholeImage(ImageView imageView, boolean pullingIn) {
        // Base values
        double baseScale = 1.0;
        double maxScale = 1.2;
        double minScale = 0.85;

        // Create effects
        ColorAdjust colorAdjust = new ColorAdjust();
        imageView.setEffect(colorAdjust);

        // Create animation timeline
        Timeline animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);

        if (pullingIn) {
            // PULLING IN ANIMATION
            KeyFrame kf1 = new KeyFrame(Duration.ZERO,
                    new KeyValue(imageView.scaleXProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.0, Interpolator.EASE_OUT));

            KeyFrame kf2 = new KeyFrame(Duration.seconds(0.6),
                    new KeyValue(imageView.scaleXProperty(), maxScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), maxScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.3, Interpolator.EASE_OUT));

            KeyFrame kf3 = new KeyFrame(Duration.seconds(1.0),
                    new KeyValue(imageView.scaleXProperty(), minScale, Interpolator.EASE_IN),
                    new KeyValue(imageView.scaleYProperty(), minScale, Interpolator.EASE_IN),
                    new KeyValue(colorAdjust.brightnessProperty(), -0.2, Interpolator.EASE_IN));

            KeyFrame kf4 = new KeyFrame(Duration.seconds(1.6),
                    new KeyValue(imageView.scaleXProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.0, Interpolator.EASE_OUT));

            animation.getKeyFrames().addAll(kf1, kf2, kf3, kf4);
        } else {
            // SPITTING OUT ANIMATION
            KeyFrame kf1 = new KeyFrame(Duration.ZERO,
                    new KeyValue(imageView.scaleXProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.0, Interpolator.EASE_OUT));

            KeyFrame kf2 = new KeyFrame(Duration.seconds(0.6),
                    new KeyValue(imageView.scaleXProperty(), minScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), minScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), -0.2, Interpolator.EASE_OUT));

            KeyFrame kf3 = new KeyFrame(Duration.seconds(1.0),
                    new KeyValue(imageView.scaleXProperty(), maxScale, Interpolator.EASE_IN),
                    new KeyValue(imageView.scaleYProperty(), maxScale, Interpolator.EASE_IN),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.3, Interpolator.EASE_IN));

            KeyFrame kf4 = new KeyFrame(Duration.seconds(1.6),
                    new KeyValue(imageView.scaleXProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(imageView.scaleYProperty(), baseScale, Interpolator.EASE_OUT),
                    new KeyValue(colorAdjust.brightnessProperty(), 0.0, Interpolator.EASE_OUT));

            animation.getKeyFrames().addAll(kf1, kf2, kf3, kf4);
        }

        animation.play();
    }

    private void addPulseAnimation(Pane container, Line line, Color color) {
        javafx.scene.shape.Circle pulse = new javafx.scene.shape.Circle(10, color);
        pulse.setEffect(new javafx.scene.effect.Glow(1.0));
        pulse.setOpacity(0.0); // Start invisible, change to 1.0 temporarily to debug visibility

        container.getChildren().add(pulse); // Add last so it's on top

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(1.5));
        pathTransition.setPath(line); // Use the line directly
        pathTransition.setNode(pulse);
        pathTransition.setCycleCount(Animation.INDEFINITE);
        pathTransition.setInterpolator(Interpolator.LINEAR);
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);

        // Fade in and out during transition
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

        // Delay start until scene is ready
        container.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                javafx.application.Platform.runLater(pathTransition::play);
            }
        });
    }

}