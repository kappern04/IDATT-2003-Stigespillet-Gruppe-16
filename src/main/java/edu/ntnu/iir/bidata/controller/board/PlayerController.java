package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import edu.ntnu.iir.bidata.view.board.PlayerView;
import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Objects;

public class PlayerController implements Observer {

  private static final int STEP_DURATION_MS = 300;
  private static final int SPECIAL_JUMP_DURATION_MS = 500;
  private static final int LADDER_DELAY_MS = 200;

  private final Board board;
  private final PlayerView playerView;
  private final HashMap<Player, Integer> previousPositions;

  public PlayerController(Board board, Player[] players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.previousPositions = new HashMap<>();
    for (Player player : players) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
      previousPositions.put(player, 0);
    }
    this.playerView = new PlayerView(board, players);
  }

  public void addPlayersToBoard(StackPane boardPane) {
    playerView.addPlayersToBoard(boardPane);
    updatePlayerPositions();
  }

  public void updatePlayerPositions() {
    previousPositions.keySet().forEach(player -> {
      int currentPos = previousPositions.get(player);
      int targetPos = player.getPositionIndex();
      if (currentPos != targetPos) {
        animatePlayerMovement(player, currentPos, targetPos);
      } else {
        Tile tile = board.getTiles().get(currentPos);
        playerView.positionPlayerAtTile(player, tile);
      }
    });
  }

  public void movePlayer(Player player, int steps) {
    Objects.requireNonNull(player, "Player cannot be null");
    int currentPosition = previousPositions.getOrDefault(player, 0);
    int newPosition = Math.max(0, Math.min(board.getTiles().size() - 1, currentPosition + steps));
    player.setPositionIndex(newPosition);
    animatePlayerMovement(player, currentPosition, newPosition);
  }

  private void animatePlayerMovement(Player player, int currentPosition, int targetPosition) {
    boolean isSpecialMovement = isSpecialMovement(currentPosition, targetPosition);
    ImageView sprite = playerView.getPlayerSprite(player);
      if (sprite == null) {
          return;
      }

    SequentialTransition sequence = new SequentialTransition();

    if (isSpecialMovement) {
      TranslateTransition jump = createTranslateTransition(sprite, getBoardOffsetX(targetPosition),
          getBoardOffsetY(targetPosition), SPECIAL_JUMP_DURATION_MS);
      RotateTransition spin = createRotateTransition(sprite, getRotationForTile(targetPosition),
          SPECIAL_JUMP_DURATION_MS);
      sequence.getChildren().add(new ParallelTransition(jump, spin));
    } else {
      int steps = targetPosition - currentPosition;
      for (int i = 1; i <= steps; i++) {
        int stepPosition = currentPosition + i;
        Tile tile = board.getTiles().get(stepPosition);
        TranslateTransition translate = createTranslateTransition(sprite,
            getBoardOffsetX(stepPosition), getBoardOffsetY(stepPosition), STEP_DURATION_MS);
        RotateTransition rotate = createRotateTransition(sprite, getRotationForTile(stepPosition),
            STEP_DURATION_MS);
        sequence.getChildren().add(new ParallelTransition(translate, rotate));
      }
    }

    sequence.setOnFinished(e -> handleAnimationFinished(player, targetPosition));
    sequence.play();
  }

  private TranslateTransition createTranslateTransition(ImageView sprite, double targetX,
      double targetY, int durationMs) {
    TranslateTransition translate = new TranslateTransition(Duration.millis(durationMs), sprite);
    translate.setToX(targetX);
    translate.setToY(targetY);
    translate.setInterpolator(Interpolator.EASE_BOTH);
    return translate;
  }

  private RotateTransition createRotateTransition(ImageView sprite, double targetRotation,
      int durationMs) {
    RotateTransition rotate = new RotateTransition(Duration.millis(durationMs), sprite);
    rotate.setToAngle(targetRotation);
    rotate.setInterpolator(Interpolator.EASE_BOTH);
    return rotate;
  }

  private double getBoardOffsetX(int tileIndex) {
    Tile tile = board.getTiles().get(tileIndex);
    int xDimension = board.getX_dimension();
    return tile.getX() * PlayerView.TILE_SIZE + PlayerView.TILE_SIZE
        - (xDimension + 1) * PlayerView.TILE_CENTER_OFFSET;
  }

  private double getBoardOffsetY(int tileIndex) {
    Tile tile = board.getTiles().get(tileIndex);
    int yDimension = board.getY_dimension();
    return tile.getY() * PlayerView.TILE_SIZE + PlayerView.TILE_SIZE
        - (yDimension + 1) * PlayerView.TILE_CENTER_OFFSET;
  }

  private double getRotationForTile(int tileIndex) {
    Tile tile = board.getTiles().get(tileIndex);
    int currentIndex = tile.getIndex();
      if (currentIndex >= board.getTiles().size() - 1) {
          return 0;
      }
    Tile nextTile = board.getTiles().get(currentIndex + 1);
    int dx = nextTile.getX() - tile.getX();
    int dy = nextTile.getY() - tile.getY();
      if (dx > 0) {
          return 90;
      }
      if (dx < 0) {
          return -90;
      }
      if (dy > 0) {
          return 180;
      }
    return 0;
  }

  private boolean isSpecialMovement(int currentPosition, int targetPosition) {
    Tile currentTile = board.getTiles().get(currentPosition);
    return hasLadderToPosition(currentTile, targetPosition) ||
        Math.abs(targetPosition - currentPosition) > 6 ||
        targetPosition < currentPosition;
  }

  private boolean hasLadderToPosition(Tile tile, int targetPosition) {
    if (tile.getTileAction() instanceof LadderAction ladderAction) {
      return ladderAction.getDestinationTileIndex() == targetPosition;
    }
    return false;
  }

  private boolean hasLadder(Tile tile) {
    return tile.getTileAction() instanceof LadderAction;
  }

  private void handleAnimationFinished(Player player, int targetPosition) {
    int previousPosition = previousPositions.get(player);
    previousPositions.put(player, targetPosition);

    Tile currentTile = board.getTiles().get(targetPosition);
    if (hasLadder(currentTile)) {
      playLadderAnimation(player, currentTile, previousPosition, () -> {
        if (player.getPositionIndex() != targetPosition) {
          animatePlayerMovement(player, targetPosition, player.getPositionIndex());
        }
      });
    } else if (player.getPositionIndex() != targetPosition) {
      animatePlayerMovement(player, targetPosition, player.getPositionIndex());
    }
  }

  private void playLadderAnimation(Player player, Tile tile, int previousPosition,
      Runnable onFinished) {
    PauseTransition pause = new PauseTransition(Duration.millis(LADDER_DELAY_MS));
    pause.setOnFinished(event -> {
      if (tile.getTileAction() instanceof LadderAction ladderAction) {
        ladderAction.playLadderSound(previousPosition);
      }
        if (onFinished != null) {
            onFinished.run();
        }
    });
    pause.play();
  }

  @Override
  public <T extends Observer> void update(Observable<T> observable, String prompt) {
    updatePlayerPositions();
  }

  public PlayerView getPlayerView() {
    return playerView;
  }
}