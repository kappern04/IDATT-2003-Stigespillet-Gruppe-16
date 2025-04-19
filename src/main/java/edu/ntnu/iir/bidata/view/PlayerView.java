package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Observable;
import edu.ntnu.iir.bidata.view.Observer;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the visual representation and animation of players on the game board.
 */
public class PlayerView implements Observer {

  private static final int STEP_DURATION_MS = 300;
  private static final int SPRITE_SIZE = 32;
  private static final int TILE_SIZE = 70;
  private static final int TILE_CENTER_OFFSET = 35;
  private static final int SPECIAL_JUMP_DURATION_MS = 500;
  private static final int LADDER_DELAY_MS = 200;
  private static final String PLAYER_IMAGE_PATH = "/image/Player_";
  private static final String PLAYER_IMAGE_EXTENSION = ".png";

  private final Board board;
  private final Map<Player, ImageView> playerSprites;
  private final HashMap<Player, Integer> previousPositions;

  /**
   * Creates a new PlayerView for the given board and players.
   *
   * @param board   the game board
   * @param players array of players in the game
   */
  public PlayerView(Board board, Player[] players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.playerSprites = new HashMap<>();
    this.previousPositions = new HashMap<>();

    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) {
        throw new IllegalArgumentException("Player at index " + i + " is null");
      }

      ImageView playerImage = createPlayerImage(PLAYER_IMAGE_PATH + (i + 1) + PLAYER_IMAGE_EXTENSION);
      playerSprites.put(players[i], playerImage);
      previousPositions.put(players[i], 0);
    }
  }

  /**
   * Creates an ImageView for a player with the specified image path.
   *
   * @param imagePath path to the player image resource
   * @return the created ImageView
   */
  protected ImageView createPlayerImage(String imagePath) {
    try {
      Image image = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream(imagePath),
          "Could not load image: " + imagePath));

      ImageView imageView = new ImageView(image);
      imageView.setFitWidth(SPRITE_SIZE);
      imageView.setFitHeight(SPRITE_SIZE);
      return imageView;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load player image: " + imagePath, e);
    }
  }

  /**
   * Adds all player sprites to the provided board pane.
   *
   * @param boardPane the StackPane representing the game board
   */
  public void addPlayersToBoard(StackPane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    playerSprites.values().forEach(boardPane.getChildren()::add);
    updatePlayerPositions();
  }

  /**
   * Updates the positions of all players on the board.
   */
  public void updatePlayerPositions() {
    playerSprites.forEach(this::animatePlayerMovement);
  }

  /**
   * Animates the movement of a player sprite to match the player's position.
   *
   * @param player the player to animate
   * @param sprite the sprite representing the player
   */
  private void animatePlayerMovement(Player player, ImageView sprite) {
    int currentPosition = previousPositions.getOrDefault(player, 0);
    int targetPosition = player.getPositionIndex();

    if (currentPosition == targetPosition) {
      positionPlayerAtTile(sprite, board.getTiles().get(currentPosition));
      return;
    }

    SequentialTransition sequence = new SequentialTransition();

    // Check if this is a ladder movement by checking if the tile has a ladder action
    boolean isLadderMovement = false;
    Tile currentTile = board.getTiles().get(currentPosition);

    if (currentTile.getTileAction() instanceof LadderAction ladderAction) {
      if (ladderAction.getDestinationTileIndex() == targetPosition) {
        isLadderMovement = true;
      }
    }

    // Use special animation for ladders, large jumps, or backward movement
    if (isLadderMovement || Math.abs(targetPosition - currentPosition) > 6 || targetPosition < currentPosition) {
      animateSpecialJump(sprite, targetPosition, sequence);
    } else {
      animateRegularMovement(sprite, currentPosition, targetPosition, sequence);
    }

    sequence.setOnFinished(e -> handleAnimationFinished(player, targetPosition));
    sequence.play();
  }

  private void animateSpecialJump(ImageView sprite, int targetPosition, SequentialTransition sequence) {
    Tile endTile = board.getTiles().get(targetPosition);

    TranslateTransition jump = createTranslateTransition(sprite,
        getTileCenterX(endTile),
        getTileCenterY(endTile));
    jump.setDuration(Duration.millis(SPECIAL_JUMP_DURATION_MS));

    RotateTransition spin = createRotateTransition(sprite, getRotationForTile(endTile));
    spin.setDuration(Duration.millis(SPECIAL_JUMP_DURATION_MS));

    ParallelTransition specialMove = new ParallelTransition(jump, spin);
    sequence.getChildren().add(specialMove);
  }

  private void animateRegularMovement(ImageView sprite, int currentPosition, int targetPosition,
      SequentialTransition sequence) {
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

  private void handleAnimationFinished(Player player, int targetPosition) {
    int previousPosition = previousPositions.get(player);
    previousPositions.put(player, targetPosition);

    // Check if player landed on a ladder
    Tile currentTile = board.getTiles().get(targetPosition);
    if (currentTile.getTileAction() instanceof LadderAction ladderAction) {
      // Add delay before playing ladder sound and starting next animation
      PauseTransition pause = new PauseTransition(Duration.millis(LADDER_DELAY_MS));
      pause.setOnFinished(event -> {
        // Play ladder sound after delay
        ladderAction.playLadderSound(previousPosition);

        // Check if position changed during animation (due to ladder)
        if (player.getPositionIndex() != targetPosition) {
          animatePlayerMovement(player, playerSprites.get(player));
        }
      });
      pause.play();
    } else {
      // No ladder, so check immediately if position changed for any other reason
      if (player.getPositionIndex() != targetPosition) {
        animatePlayerMovement(player, playerSprites.get(player));
      }
    }
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

  /**
   * Moves a player forward by the specified number of steps.
   *
   * @param player the player to move
   * @param steps  the number of steps to move
   */
  public void movePlayer(Player player, int steps) {
    Objects.requireNonNull(player, "Player cannot be null");

    int currentPosition = previousPositions.getOrDefault(player, 0);
    int newPosition = Math.max(0, Math.min(board.getTiles().size() - 1, currentPosition + steps));

    player.setPositionIndex(newPosition);
    animatePlayerMovement(player, playerSprites.get(player));
  }

  private double getTileCenterX(Tile tile) {
    int xDimension = board.getX_dimension();
    return tile.getX() * TILE_SIZE + TILE_SIZE - (xDimension + 1) * TILE_CENTER_OFFSET;
  }

  private double getTileCenterY(Tile tile) {
    int yDimension = board.getY_dimension();
    return tile.getY() * TILE_SIZE + TILE_SIZE - (yDimension + 1) * TILE_CENTER_OFFSET;
  }

  private double getRotationForTile(Tile tile) {
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

  /**
   * Gets the image for the specified player.
   *
   * @param player the player
   * @return the player's image
   */
  public Image getPlayerImage(Player player) {
    if (player != null && playerSprites.containsKey(player)) {
      return playerSprites.get(player).getImage();
    }

    // Fallback to first available image
    return playerSprites.values().iterator().next().getImage();
  }

  @Override
  public <T extends Observer> void update(Observable<T> observable, String prompt) {
    updatePlayerPositions();
  }
}