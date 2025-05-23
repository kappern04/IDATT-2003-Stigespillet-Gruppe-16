package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.model.CosmicChanceAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.BoardUtils;
import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ChanceTileView {

  private static final int INDICATOR_SIZE = 30;

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

  /**
   * Shows a chance effect popup and returns a runnable that will be executed when the popup is closed.
   * @param player The player who landed on the chance tile
   * @param chanceAction The chance action to be executed
   * @param onComplete Callback to run after the popup is dismissed
   */
  public void showChancePopup(Player player, CosmicChanceAction chanceAction, Runnable onComplete) {
    CSS css = new CSS();

    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initStyle(StageStyle.UNDECORATED);

    VBox popupContent = new VBox(20);
    popupContent.setAlignment(Pos.CENTER);
    popupContent.setPadding(new javafx.geometry.Insets(30));
    popupContent.getStyleClass().add("space-popup");

    // Add a style class for custom background
    popupContent.setStyle("-fx-background-color: linear-gradient(to bottom, #121e3d, #0a0f1f); " +
            "-fx-border-color: #4a90e2; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;");

    // Title
    Text title = new Text("COSMIC CHANCE");
    title.setFont(css.getOrbitronFont(24, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    Glow glow = new Glow(0.8);
    title.setEffect(glow);

    Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulse.setCycleCount(Timeline.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    // Description
    ChanceEffectType effectType = chanceAction.getEffectType();
    Text playerText = new Text(player.getName() + " has triggered:");
    playerText.setFont(css.getOrbitronFont(16, FontWeight.NORMAL));
    playerText.setFill(css.getSpaceBlue());

    Text effectTypeText = new Text(effectType.getName());
    effectTypeText.setFont(css.getOrbitronFont(18, FontWeight.BOLD));
    effectTypeText.setFill(css.getSpaceBlue());

    Text descriptionText = new Text(effectType.getDescription());
    descriptionText.setFont(css.getOrbitronFont(14, FontWeight.NORMAL));
    descriptionText.setFill(css.getSpaceBlue());

    // Continue button
    Button continueButton = css.createSpaceButton("Continue");
    continueButton.setOnAction(e -> {
      popupStage.close();
      if (onComplete != null) {
        onComplete.run();
      }
    });

    // Create icon for effect type
    ImageView effectIcon = createEffectTypeIcon(effectType);
    if (effectIcon != null) {
      effectIcon.setFitWidth(48);
      effectIcon.setFitHeight(48);
    }

    // Add all elements to the popup
    popupContent.getChildren().addAll(
            title,
            playerText,
            effectTypeText,
            (effectIcon != null ? effectIcon : new Pane()),
            descriptionText,
            continueButton
    );

    // Add a drop shadow effect to the popup content
    javafx.scene.effect.DropShadow dropShadow = new javafx.scene.effect.DropShadow();
    dropShadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.5));
    dropShadow.setRadius(10);
    popupContent.setEffect(dropShadow);

    Scene scene = new Scene(popupContent, 350, 350);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());

    popupStage.setScene(scene);
    popupStage.show();
  }

  private ImageView createEffectTypeIcon(ChanceEffectType effectType) {
    String imagePath = "/image/effects/";

    switch (effectType) {
      case FORWARD_SMALL:
      case FORWARD_MEDIUM:
      case FORWARD_LARGE:
        imagePath += "forward.png";
        break;
      case BACKWARD_SMALL:
      case BACKWARD_MEDIUM:
      case BACKWARD_LARGE:
        imagePath += "backward.png";
        break;
      case TELEPORT_RANDOM:
        imagePath += "teleport.png";
        break;
      case RETURN_START:
        imagePath += "return_start.png";
        break;
      default:
        return null;
    }

    try {
      Image image = new Image(getClass().getResourceAsStream(imagePath));
      ImageView imageView = new ImageView(image);
      imageView.setPreserveRatio(true);
      return imageView;
    } catch (Exception e) {
      // If the image doesn't exist, return null
      return null;
    }
  }
}