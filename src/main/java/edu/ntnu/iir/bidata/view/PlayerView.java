// Abstract base class
package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Observable;
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

    // Create a sequential animation for multiple movement phases
    SequentialTransition sequence = new SequentialTransition();

    // Handle large jumps or backward movement differently
    if (Math.abs(targetPosition - currentPosition) > 6 || targetPosition < currentPosition) {
      // Special jump animation (ladder/portal)
      Tile endTile = board.getTiles().get(targetPosition);

      // Create a special animation with arc effect
      TranslateTransition jump = createTranslateTransition(sprite,
          getTileCenterX(endTile),
          getTileCenterY(endTile));
      jump.setDuration(Duration.millis(500));

      RotateTransition spin = createRotateTransition(sprite, getRotationForTile(endTile));
      spin.setDuration(Duration.millis(500));

      ParallelTransition specialMove = new ParallelTransition(jump, spin);
      sequence.getChildren().add(specialMove);
    } else {
      // Regular dice-based movement
      int steps = targetPosition - currentPosition;

      for (int i = 1; i <= steps; i++) {
        int stepPosition = currentPosition + i;
        Tile tile = board.getTiles().get(stepPosition);

        TranslateTransition translate = createTranslateTransition(sprite,
            getTileCenterX(tile),
            getTileCenterY(tile));
        translate.setDuration(Duration.millis(STEP_DURATION_MS));

        RotateTransition rotate = createRotateTransition(sprite, getRotationForTile(tile));
        rotate.setDuration(Duration.millis(STEP_DURATION_MS));

        ParallelTransition step = new ParallelTransition(translate, rotate);
        sequence.getChildren().add(step);
      }
    }

    sequence.setOnFinished(e -> {
      int previousPosition = previousPositions.get(player);
      previousPositions.put(player, targetPosition);

      // Check if player landed on a ladder
      Tile currentTile = board.getTiles().get(targetPosition);
      if (currentTile.getTileAction() instanceof LadderAction ladderAction) {
        // Play the ladder sound after movement animation
        ladderAction.playLadderSound(previousPosition);
      }

      // Check if position changed during animation (due to ladder)
      if (player.getPositionIndex() != targetPosition) {
        animatePlayerMovement(player, sprite);
      }
    });

    sequence.play();
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

  public Image getPlayerImage(Player player) {
    if (playerSprites.containsKey(player)) {
      return playerSprites.get(player).getImage();
    }
    return playerSprites.values().iterator().next().getImage();
  }

  @Override
  public <T extends Observer> void update(Observable<T> observable, String prompt) {
    updatePlayerPositions();
  }
}
