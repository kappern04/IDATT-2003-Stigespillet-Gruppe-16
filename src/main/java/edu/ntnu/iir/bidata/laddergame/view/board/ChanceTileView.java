package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.util.BoardUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ChanceTileView {

  private static final int INDICATOR_SIZE = 32;

  /**
   * Creates a visual indicator for a chance tile, centered and bound to the tile node.
   * @param tileNode the visual node representing the tile
   * @return a node containing the chance tile visual
   */
  public Node createChanceTileVisual(Node tileNode) {
    if (tileNode == null) return null;

    Pane indicatorContainer = new Pane();
    indicatorContainer.setPickOnBounds(false);

    ImageView questionMarkIcon = createQuestionMarkIcon();
    questionMarkIcon.setFitWidth(INDICATOR_SIZE);
    questionMarkIcon.setFitHeight(INDICATOR_SIZE);
    indicatorContainer.getChildren().add(questionMarkIcon);

    // Center the indicator on the tile using property bindings
    BoardUtils.bindNodeToCenter(indicatorContainer, tileNode, INDICATOR_SIZE, INDICATOR_SIZE, 0, 0);

    addPulsingEffect(indicatorContainer);

    return indicatorContainer;
  }

  private ImageView createQuestionMarkIcon() {
    Image questionMarkImage = new Image(getClass().getResourceAsStream("/image/question_mark.png"));
    ImageView imageView = new ImageView(questionMarkImage);
    imageView.setPreserveRatio(true);
    return imageView;
  }

  private void addPulsingEffect(Node node) {
    Glow glow = new Glow(0.5);
    node.setEffect(glow);

    Timeline pulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.seconds(1.2), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulseAnimation.setAutoReverse(true);
    pulseAnimation.setCycleCount(Timeline.INDEFINITE);
    pulseAnimation.play();
  }
}