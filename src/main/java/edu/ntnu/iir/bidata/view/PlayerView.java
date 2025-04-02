// Abstract base class
package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlayerView {

  protected static final double SPRITE_SIZE = 32.0;
  protected static final int STEP_DURATION_MS = 300; // Duration for each step

  protected Board board;
  protected Map<Player, ImageView> playerSprites;
  protected Map<Player, Integer> previousPositions;

  public PlayerView(Board board, Player[] players) {
    this.board = board;
    this.playerSprites = new HashMap<>();
    this.previousPositions = new HashMap<>();

    for (int i = 0; i < players.length; i++) {
      ImageView playerImage = createPlayerImage("/image/Player_" + (i + 1) + ".png");
      playerSprites.put(players[i], playerImage);
      previousPositions.put(players[i], 0); // Start at position 0
    }
  }

  protected ImageView createPlayerImage(String imagePath) {
    Image image = new Image(getClass().getResourceAsStream(imagePath));
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(SPRITE_SIZE);
    imageView.setFitHeight(SPRITE_SIZE);
    return imageView;
  }

  public void addPlayersToBoard(StackPane boardPane) {
    playerSprites.values().forEach(boardPane.getChildren()::add);
    updatePlayerPositions();
  }

  public void updatePlayerPositions() {
    playerSprites.forEach(this::animatePlayerMovement);
  }

  protected void animatePlayerMovement(Player player, ImageView sprite) {
    int currentPosition = previousPositions.get(player);
    int targetPosition = player.getPosition();

    // If no movement needed, just update position
    if (currentPosition == targetPosition) {
      positionPlayerAtTile(sprite, board.getTiles()[currentPosition]);
      return;
    }

    // Create a sequence of animations to move through each tile
    List<ParallelTransition> movementSteps = createMovementSteps(player, sprite, currentPosition, targetPosition);

    // Combine all steps into a sequential animation
    SequentialTransition sequence = new SequentialTransition();
    sequence.getChildren().addAll(movementSteps);

    // Update the player's previous position after animation completes
    sequence.setOnFinished(e -> previousPositions.put(player, targetPosition));

    // Play the animation
    sequence.play();
  }

  protected List<ParallelTransition> createMovementSteps(Player player, ImageView sprite, int startPos, int endPos) {
    List<ParallelTransition> steps = new ArrayList<>();

    // Determine direction of movement
    int direction = startPos < endPos ? 1 : -1;

    // Create animation for each step
    for (int pos = startPos + direction; direction > 0 ? pos <= endPos : pos >= endPos; pos += direction) {
      Tile tile = board.getTiles()[pos];

      // Calculate position based on board-specific logic
      double targetX = getTilePositionX(tile);
      double targetY = getTilePositionY(tile);
      double targetRotation = getRotationForTile(tile);

      // Create movement animation
      TranslateTransition translate = createTranslateTransition(sprite, targetX, targetY);
      translate.setDuration(Duration.millis(STEP_DURATION_MS));

      // Create rotation animation
      RotateTransition rotate = createRotateTransition(sprite, targetRotation);
      rotate.setDuration(Duration.millis(STEP_DURATION_MS));

      // Combine translate and rotate into a parallel animation
      ParallelTransition step = new ParallelTransition(translate, rotate);
      steps.add(step);
    }

    return steps;
  }

  protected void positionPlayerAtTile(ImageView sprite, Tile tile) {
    double targetX = getTilePositionX(tile);
    double targetY = getTilePositionY(tile);
    double targetRotation = getRotationForTile(tile);

    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
    sprite.setRotate(targetRotation);
  }

  protected TranslateTransition createTranslateTransition(ImageView sprite, double targetX, double targetY) {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(sprite);
    translate.setToX(targetX);
    translate.setToY(targetY);
    translate.setInterpolator(Interpolator.EASE_BOTH);
    return translate;
  }

  protected RotateTransition createRotateTransition(ImageView sprite, double targetRotation) {
    RotateTransition rotate = new RotateTransition();
    rotate.setNode(sprite);
    rotate.setToAngle(targetRotation);
    rotate.setInterpolator(Interpolator.EASE_BOTH);
    return rotate;
  }

  public void movePlayer(Player player, int steps) {
    int currentPosition = previousPositions.get(player);
    int newPosition = Math.max(0, Math.min(board.getTiles().length - 1, currentPosition + steps));

    // Update the player's position in the Player object
    player.setPosition(newPosition);

    // Update the visual representation
    animatePlayerMovement(player, playerSprites.get(player));
  }

  // Abstract methods that must be implemented by subclasses
  protected abstract double getTilePositionX(Tile tile);
  protected abstract double getTilePositionY(Tile tile);
  protected abstract double getRotationForTile(Tile tile);
}
