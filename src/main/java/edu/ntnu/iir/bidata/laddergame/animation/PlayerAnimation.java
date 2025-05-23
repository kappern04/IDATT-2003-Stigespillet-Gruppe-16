package edu.ntnu.iir.bidata.laddergame.animation;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.BoardUtils;
import edu.ntnu.iir.bidata.laddergame.view.board.PlayerView;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Handles animations for player movements on the game board.
 */
public class PlayerAnimation {
    public static final int STEP_DURATION_MS = 300;
    public static final int SPECIAL_JUMP_DURATION_MS = 500;

    private final Board board;
    private final PlayerView playerView;
    private final Map<Player, Timeline> activeAnimations = new HashMap<>();

    /**
     * Constructs a PlayerAnimation for the given board and player view.
     * @param board the game board
     * @param playerView the player view
     */
    public PlayerAnimation(Board board, PlayerView playerView) {
        this.board = board;
        this.playerView = playerView;
    }

    /**
     * Animates player movement from one position to another.
     * @param player the player
     * @param fromPosition starting position
     * @param toPosition ending position
     * @param onComplete callback when animation completes
     */
    public void animatePlayerMovement(Player player, int fromPosition, int toPosition, Runnable onComplete) {
        ImageView sprite = playerView.getPlayerSprite(player);
        if (sprite == null) return;

        // Cancel any existing animation
        Timeline existingAnimation = activeAnimations.get(player);
        if (existingAnimation != null) {
            existingAnimation.stop();
        }

        // Prepare sprite for animation
        playerView.prepareForAnimation(sprite, true);

        Timeline timeline = new Timeline();
        timeline.setOnFinished(e -> {
            activeAnimations.remove(player);

            // Wait for PlayerView cleanup
            playerView.prepareForAnimation(sprite, false);

            // Add delay to ensure all Platform.runLater() calls complete
            PauseTransition cleanupWait = new PauseTransition(Duration.millis(100));
            cleanupWait.setOnFinished(cleanupEvent -> {
                if (onComplete != null) onComplete.run();
            });
            cleanupWait.play();
        });

        // Decide animation type
        if (isSpecialMovement(fromPosition, toPosition)) {
            timeline.getKeyFrames().add(createSpecialMovementKeyFrame(sprite, toPosition));
        } else {
            createStepByStepKeyFrames(timeline, sprite, fromPosition, toPosition);
        }

        // Start animation
        activeAnimations.put(player, timeline);
        timeline.play();
    }

    /**
     * Creates a key frame for special movement animation.
     * @param sprite the player sprite
     * @param targetPosition the target position
     * @return keyframe for the animation
     */
    private KeyFrame createSpecialMovementKeyFrame(ImageView sprite, int targetPosition) {
        Tile targetTile = board.getTiles().get(targetPosition);
        double targetX = BoardUtils.getBoardOffsetX(board, targetTile) - PlayerView.SPRITE_WIDTH/2;
        double targetY = BoardUtils.getBoardOffsetY(board, targetTile) - PlayerView.SPRITE_HEIGHT/2;
        double targetRotation = BoardUtils.getRotationForTile(board, targetTile);

        return new KeyFrame(Duration.millis(SPECIAL_JUMP_DURATION_MS),
                new KeyValue(sprite.translateXProperty(), targetX, Interpolator.EASE_BOTH),
                new KeyValue(sprite.translateYProperty(), targetY, Interpolator.EASE_BOTH),
                new KeyValue(sprite.rotateProperty(), targetRotation, Interpolator.EASE_BOTH));
    }

    /**
     * Creates a series of key frames for step-by-step movement animation.
     * @param timeline the timeline to add frames to
     * @param sprite the player sprite
     * @param currentPosition the starting position
     * @param targetPosition the ending position
     */
    private void createStepByStepKeyFrames(Timeline timeline, ImageView sprite,
                                           int currentPosition, int targetPosition) {
        int direction = targetPosition > currentPosition ? 1 : -1;
        int steps = Math.abs(targetPosition - currentPosition);

        for (int i = 1; i <= steps; i++) {
            int stepPosition = currentPosition + (i * direction);
            Tile stepTile = board.getTiles().get(stepPosition);

            double stepX = BoardUtils.getBoardOffsetX(board, stepTile) - PlayerView.SPRITE_WIDTH/2;
            double stepY = BoardUtils.getBoardOffsetY(board, stepTile) - PlayerView.SPRITE_HEIGHT/2;
            double stepRotation = BoardUtils.getRotationForTile(board, stepTile);

            KeyFrame kf = new KeyFrame(Duration.millis(i * STEP_DURATION_MS),
                    new KeyValue(sprite.translateXProperty(), stepX, Interpolator.EASE_BOTH),
                    new KeyValue(sprite.translateYProperty(), stepY, Interpolator.EASE_BOTH),
                    new KeyValue(sprite.rotateProperty(), stepRotation, Interpolator.EASE_BOTH));

            timeline.getKeyFrames().add(kf);
        }
    }

    /**
     * Determines if a movement should be animated as a special movement.
     * @param currentPosition starting position
     * @param targetPosition ending position
     * @return true if this should be a special movement animation
     */
    private boolean isSpecialMovement(int currentPosition, int targetPosition) {
        Tile currentTile = board.getTiles().get(currentPosition);
        return hasSpecialMovementToPosition(currentTile, targetPosition) ||
                Math.abs(targetPosition - currentPosition) > 6 ||
                targetPosition < currentPosition;
    }

    /**
     * Checks if a tile has a special action that leads to the target position.
     * @param tile the current tile
     * @param targetPosition the target position
     * @return true if this tile has a special action to the target
     */
    private boolean hasSpecialMovementToPosition(Tile tile, int targetPosition) {
        return tile.getTileAction() != null &&
                tile.getTileAction().leadsToPosition(board, targetPosition);
    }

    /**
     * Checks if any player has an active animation.
     * @return true if any player has an active animation
     */
    public boolean hasActiveAnimations() {
        for (Map.Entry<Player, Timeline> entry : activeAnimations.entrySet()) {
            Timeline animation = entry.getValue();
            if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
                return true;
            }
        }
        return false;
    }
}