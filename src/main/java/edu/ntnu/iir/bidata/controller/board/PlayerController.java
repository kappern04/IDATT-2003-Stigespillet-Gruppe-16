package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.BoardUtils;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import edu.ntnu.iir.bidata.view.board.PlayerView;
import java.util.*;
import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PlayerController implements Observer {

    private static final int STEP_DURATION_MS = 300;
    private static final int SPECIAL_JUMP_DURATION_MS = 500;
    private static final int LADDER_DELAY_MS = 200;

    private final Board board;
    private final PlayerView playerView;
    private final HashMap<Player, Integer> previousPositions;

    public PlayerController(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.previousPositions = new HashMap<>();
        for (Player player : players) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
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

    private void animatePlayerMovement(Player player, int currentPosition, int targetPosition) {
        boolean isSpecialMovement = isSpecialMovement(currentPosition, targetPosition);
        ImageView sprite = playerView.getPlayerSprite(player);
        if (sprite == null) return;

        SequentialTransition sequence = new SequentialTransition();

        if (isSpecialMovement) {
            sequence.getChildren().add(createSpecialMovementAnimation(sprite, targetPosition));
        } else {
            sequence.getChildren().addAll(createStepByStepAnimation(sprite, currentPosition, targetPosition));
        }

        sequence.setOnFinished(e -> handleAnimationFinished(player, targetPosition));
        sequence.play();
    }

    private ParallelTransition createSpecialMovementAnimation(ImageView sprite, int targetPosition) {
        TranslateTransition jump = createTranslateTransition(sprite, getBoardOffsetX(targetPosition),
                getBoardOffsetY(targetPosition), SPECIAL_JUMP_DURATION_MS);
        RotateTransition spin = createRotateTransition(sprite, getRotationForTile(targetPosition),
                SPECIAL_JUMP_DURATION_MS);
        return new ParallelTransition(jump, spin);
    }

    private Animation[] createStepByStepAnimation(ImageView sprite, int currentPosition, int targetPosition) {
        int steps = targetPosition - currentPosition;
        Animation[] animations = new Animation[steps];

        for (int i = 1; i <= steps; i++) {
            int stepPosition = currentPosition + i;
            TranslateTransition translate = createTranslateTransition(sprite,
                    getBoardOffsetX(stepPosition),
                    getBoardOffsetY(stepPosition),
                    STEP_DURATION_MS);
            RotateTransition rotate = createRotateTransition(sprite,
                    getRotationForTile(stepPosition),
                    STEP_DURATION_MS);
            animations[i-1] = new ParallelTransition(translate, rotate);
        }

        return animations;
    }

    private TranslateTransition createTranslateTransition(ImageView sprite, double targetX, double targetY, int durationMs) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(durationMs), sprite);
        translate.setToX(targetX);
        translate.setToY(targetY);
        translate.setInterpolator(Interpolator.EASE_BOTH);
        return translate;
    }

    private RotateTransition createRotateTransition(ImageView sprite, double targetRotation, int durationMs) {
        RotateTransition rotate = new RotateTransition(Duration.millis(durationMs), sprite);
        rotate.setToAngle(targetRotation);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        return rotate;
    }

    private double getBoardOffsetX(int tileIndex) {
        Tile tile = board.getTiles().get(tileIndex);
        return BoardUtils.getBoardOffsetX(board, tile);
    }

    private double getBoardOffsetY(int tileIndex) {
        Tile tile = board.getTiles().get(tileIndex);
        return BoardUtils.getBoardOffsetY(board, tile);
    }

    private double getRotationForTile(int tileIndex) {
        Tile tile = board.getTiles().get(tileIndex);
        return BoardUtils.getRotationForTile(board, tile);
    }

    private boolean isSpecialMovement(int currentPosition, int targetPosition) {
        Tile currentTile = board.getTiles().get(currentPosition);
        return hasSpecialMovementToPosition(currentTile, targetPosition) ||
                Math.abs(targetPosition - currentPosition) > 6 ||
                targetPosition < currentPosition;
    }

    private boolean hasSpecialMovementToPosition(Tile tile, int targetPosition) {
        return tile.getTileAction() != null &&
                tile.getTileAction().leadsToPosition(board, targetPosition);
    }

    private void handleAnimationFinished(Player player, int targetPosition) {
        int previousPosition = previousPositions.get(player);
        previousPositions.put(player, targetPosition);

        Tile currentTile = board.getTiles().get(targetPosition);
        processSpecialTileEffects(player, currentTile, previousPosition, () -> {
            // After all special effects, check if position changed and animate again if needed
            if (player.getPositionIndex() != targetPosition) {
                animatePlayerMovement(player, targetPosition, player.getPositionIndex());
            }
        });
    }

    private void processSpecialTileEffects(Player player, Tile tile, int previousPosition, Runnable onComplete) {
        if (tile.getTileAction() == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.millis(LADDER_DELAY_MS));
        pause.setOnFinished(event -> {
            if (tile.getTileAction() instanceof LadderAction ladderAction) {
                ladderAction.playLadderSound(previousPosition);
            }
            if (onComplete != null) onComplete.run();
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