// Abstract base class
package edu.ntnu.iir.bidata.view;

import static java.lang.Math.max;
import static java.lang.Math.min;

import edu.ntnu.iir.bidata.object.Observable;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;


public class PlayerView implements Observer{

  private Board board;
  private Map<Player, ImageView> playerSprites;
  private HashMap<Player, Integer> previousPositions;
  private static final int STEP_DURATION_MS = 300;
  private static final int SPRITE_SIZE = 32;

  public PlayerView(Board board, Player[] players) {
    this.board = board;
    this.playerSprites = new HashMap<>();
    this.previousPositions = new HashMap<>();

    for (int i = 0; i < players.length; i++) {
      ImageView playerImage = createPlayerImage("/image/Player_" + (i + 1) + ".png");
      playerSprites.put(players[i], playerImage);
      previousPositions.put(players[i], 0);
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

  private void animatePlayerMovement(Player player, ImageView sprite) {
    int currentPosition = previousPositions.get(player);
    int targetPosition = player.getPositionIndex();

    if (currentPosition == targetPosition) {
      positionPlayerAtTile(sprite, board.getTiles().get(currentPosition));
      return;
    }

    if (Math.abs(targetPosition - currentPosition) > 6 || targetPosition < currentPosition ) {
      Tile endTile = board.getTiles().get(targetPosition);
      positionPlayerAtTile(sprite, endTile);
      previousPositions.put(player, targetPosition);
    } else {
      List<ParallelTransition> movementSteps = createMovementSteps(player, sprite, currentPosition, targetPosition);
      SequentialTransition sequence = new SequentialTransition();
      sequence.getChildren().addAll(movementSteps);
      sequence.setOnFinished(e -> previousPositions.put(player, targetPosition));
      sequence.play();
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

  private List<ParallelTransition> createMovementSteps(Player player, ImageView sprite, int startPos, int endPos) {
    List<ParallelTransition> steps = new ArrayList<>();

    // Determine direction of movement
    int direction = startPos < endPos ? 1 : -1;

    // Create animation for each step
    for (int pos = startPos + direction; direction > 0 ? pos <= endPos : pos >= endPos; pos += direction) {
      Tile tile = board.getTiles().get(pos);

      // Calculate position based on board-specific logic
      double targetX = getTileCenterX(tile);
      double targetY = getTileCenterY(tile);
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

  private void positionPlayerAtTile(ImageView sprite, Tile tile) {
    double targetX = getTileCenterX(tile);
    double targetY = getTileCenterY(tile);
    double targetRotation = getRotationForTile(tile);

    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
    sprite.setRotate(targetRotation);
  }

  private TranslateTransition createTranslateTransition(ImageView sprite, double targetX, double targetY) {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(sprite);
    translate.setToX(targetX);
    translate.setToY(targetY);
    translate.setInterpolator(Interpolator.EASE_BOTH);
    return translate;
  }

  private RotateTransition createRotateTransition(ImageView sprite, double targetRotation) {
    RotateTransition rotate = new RotateTransition();
    rotate.setNode(sprite);
    rotate.setToAngle(targetRotation);
    rotate.setInterpolator(Interpolator.EASE_BOTH);
    return rotate;
  }

  public void movePlayer(Player player, int steps) {
    int currentPosition = previousPositions.get(player);
    int newPosition = Math.max(0, Math.min(board.getTiles().size() - 1, currentPosition + steps));

    // Update the player's position in the Player object
    player.setPositionIndex(newPosition);

    // Update the visual representation
    animatePlayerMovement(player, playerSprites.get(player));
  }

  private double getTileCenterX(Tile tile) {
    int xDimension = board.getX_dimension();
    return tile.getX() * 70 + 70 - (xDimension + 1) * 35;
  }

  private double getTileCenterY(Tile tile) {
    int yDimension = board.getY_dimension();
    return tile.getY() * 70 + 70 - (yDimension + 1) * 35;
  }

  private double getRotationForTile(Tile tile) {
    // find next tile to determine direction
    int currentIndex = tile.getIndex();
    if (currentIndex >= board.getTiles().size() - 1) {
      return 0; // final tile has no rotation
    }

    Tile nextTile = board.getTiles().get(currentIndex + 1);

    // Calculate direction based on tile coordinates
    int dx = nextTile.getX() - tile.getX();
    int dy = nextTile.getY() - tile.getY();

    if (dx > 0) return 90;  // right
    if (dx < 0) return -90; // left
    if (dy > 0) return 180; // down
    return 0; // up
  }

  @Override
  public <T extends Observer> void update(Observable<T> observable, String prompt) {
    updatePlayerPositions();
  }
}
