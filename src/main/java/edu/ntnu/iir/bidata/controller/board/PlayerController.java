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
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Controls player movements and animations on the game board.
 */
public class PlayerController implements Observer {

    private static final int STEP_DURATION_MS = 300;
    private static final int SPECIAL_JUMP_DURATION_MS = 500;
    private static final int LADDER_DELAY_MS = 200;
    private static final int EFFECT_DELAY_MS = 300;

    private final Board board;
    private final PlayerView playerView;
    private final HashMap<Player, Integer> previousPositions;
    private final Map<Player, Timeline> activeAnimations = new HashMap<>();

    /**
     * Constructs a PlayerController for the given board and players.
     * @param board the game board
     * @param players the list of players
     */
    public PlayerController(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.previousPositions = new HashMap<>();
        for (Player player : players) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            previousPositions.put(player, 0);
        }
        this.playerView = new PlayerView(board, players);
    }

    /**
     * Adds player sprites to the board and initializes their positions.
     * @param boardPane the board pane
     */
    public void addPlayersToBoard(Pane boardPane) {
        playerView.addPlayersToBoard(boardPane);
        updatePlayerPositions();
    }

    /**
     * Updates the positions of all players on the board.
     */
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

    /**
     * Animates player movement from one position to another.
     * @param player the player to move
     * @param currentPosition the starting position
     * @param targetPosition the ending position
     */
    private void animatePlayerMovement(Player player, int currentPosition, int targetPosition) {
        boolean isSpecialMovement = isSpecialMovement(currentPosition, targetPosition);
        ImageView sprite = playerView.getPlayerSprite(player);
        if (sprite == null) return;

        // Cancel any active animation for this player
        Timeline existingAnimation = activeAnimations.get(player);
        if (existingAnimation != null && existingAnimation.getStatus() == Animation.Status.RUNNING) {
            existingAnimation.stop();
        }

        // Prepare the sprite for animation
        playerView.prepareForAnimation(sprite);

        Timeline timeline = new Timeline();
        if (isSpecialMovement) {
            // For special movements (ladders, snakes, etc.), perform a direct animation
            KeyFrame kf = createSpecialMovementKeyFrame(sprite, targetPosition);
            timeline.getKeyFrames().add(kf);
        } else {
            // For normal movement, animate step by step
            createStepByStepKeyFrames(timeline, sprite, currentPosition, targetPosition);
        }

        // When animation completes, process any special tile effects
        timeline.setOnFinished(e -> handleMovementComplete(player, currentPosition, targetPosition));

        // Store and play the animation
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
     * Sets the proper rotation for a player's sprite based on the current tile position.
     * @param player the player
     * @param tile the current tile
     */
    private void setRotationForTile(Player player, Tile tile) {
        ImageView sprite = playerView.getPlayerSprite(player);
        if (sprite == null) return;

        double rotation = BoardUtils.getRotationForTile(board, tile);
        sprite.setRotate(rotation);
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
     * Handles completion of a movement animation.
     * @param player the player
     * @param fromPosition the starting position
     * @param toPosition the ending position
     */
    private void handleMovementComplete(Player player, int fromPosition, int toPosition) {
        // Update previous position
        previousPositions.put(player, toPosition);

        // Check if we're within bounds
        if (toPosition >= board.getTiles().size()) return;

        Tile targetTile = board.getTiles().get(toPosition);

        // Schedule tile effect processing with a slight delay
        PauseTransition pause = new PauseTransition(Duration.millis(EFFECT_DELAY_MS));
        pause.setOnFinished(e -> {
            // Process tile effects
            if (targetTile.getTileAction() != null) {
                // Explicitly process special tile effects (with sound)
                processSpecialTileEffects(player, targetTile, fromPosition, () -> {
                    // If position changed due to a tile action
                    if (player.getPositionIndex() != toPosition) {
                        // Get final position after tile action
                        int finalPosition = player.getPositionIndex();
                        Tile finalTile = board.getTiles().get(finalPosition);

                        // Position at final location with binding (not animation)
                        Platform.runLater(() -> {
                            // If position changed significantly, animate the special move
                            if (Math.abs(finalPosition - toPosition) > 1) {
                                Platform.runLater(() -> animatePlayerMovement(player, toPosition, finalPosition));
                            } else {
                                playerView.positionPlayerAtTile(player, finalTile);
                                setRotationForTile(player, finalTile);
                            }
                        });
                    } else {
                        // No position change from tile action, just bind to final position
                        Platform.runLater(() -> {
                            playerView.positionPlayerAtTile(player, targetTile);
                            setRotationForTile(player, targetTile);
                        });
                    }
                });
            } else {
                // No tile action, just bind to final position
                Platform.runLater(() -> {
                    playerView.positionPlayerAtTile(player, targetTile);
                    setRotationForTile(player, targetTile);
                });
            }
        });
        pause.play();
    }

    /**
     * Processes special tile effects.
     * @param player the player
     * @param tile the tile with effects
     * @param previousPosition the player's previous position
     * @param onComplete callback when effect processing is complete
     */
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

    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        updatePlayerPositions();
    }

    /**
     * Gets the player view.
     * @return the player view
     */
    public PlayerView getPlayerView() {
        return playerView;
    }
}