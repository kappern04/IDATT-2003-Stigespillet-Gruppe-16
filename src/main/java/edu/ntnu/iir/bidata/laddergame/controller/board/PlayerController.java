package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.animation.PlayerAnimation;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.BoardUtils;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import edu.ntnu.iir.bidata.laddergame.view.board.PlayerView;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls player movements and animations on the game board.
 */
public class PlayerController implements Observer {

    private static final int LADDER_DELAY_MS = 200;
    private static final int EFFECT_DELAY_MS = 300;

    private final Board board;
    private final PlayerView playerView;
    private final PlayerAnimation playerAnimation;
    private final HashMap<Player, Integer> previousPositions;
    private final AtomicBoolean playerAnimating = new AtomicBoolean(false);


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
        this.playerAnimation = new PlayerAnimation(board, playerView);
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
     * Moves a player to a new position.
     * @param player the player to move
     * @param newPosition the new position
     */
    public void movePlayerToPosition(Player player, int newPosition) {
        int lastTile = board.getLastTile();
        newPosition = Math.max(1, Math.min(newPosition, lastTile));

        if (player.getPositionIndex() == newPosition) {
            return;
        }

        int oldPosition = player.getPositionIndex();
        player.setPositionIndex(newPosition);
        animatePlayerMovement(player, oldPosition, newPosition);
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
     * @param player the player
     * @param fromPosition starting position
     * @param toPosition ending position
     */
    public void animatePlayerMovement(Player player, int fromPosition, int toPosition) {
        playerAnimating.set(true);
        playerAnimation.animatePlayerMovement(player, fromPosition, toPosition, () -> {
            handleMovementComplete(player, fromPosition, toPosition);
            playerAnimating.set(false);
        });
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
        return playerAnimating.get();
    }

    public Set<Player> getPlayers() {
        return previousPositions.keySet();
    }

    @Override
    public void update(Observable observable, String prompt) {
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