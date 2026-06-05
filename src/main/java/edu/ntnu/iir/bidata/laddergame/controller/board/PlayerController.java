package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.animation.PlayerAnimation;
import edu.ntnu.iir.bidata.laddergame.controller.GameController;
import edu.ntnu.iir.bidata.laddergame.model.*;
import edu.ntnu.iir.bidata.laddergame.view.util.BoardUtils;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import edu.ntnu.iir.bidata.laddergame.view.board.ChanceTileView;
import edu.ntnu.iir.bidata.laddergame.view.board.PlayerView;
import edu.ntnu.iir.bidata.laddergame.view.util.SoundEffects;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Controls player movement, animation, and tile interactions on the board.
 */
public class PlayerController implements Observer<Player> {

    private static final int LADDER_DELAY_MS = 200;
    private static final int EFFECT_DELAY_MS = 300;

    private final Board board;
    private final PlayerView playerView;
    private final PlayerAnimation playerAnimation;
    private final Map<Player, Integer> previousPositions;
    private final AtomicBoolean playerAnimating = new AtomicBoolean(false);
    private Runnable onTurnSettled;

    private static final Logger LOGGER = Logger.getLogger(PlayerController.class.getName());


    /**
     * Constructs the PlayerController with board and player list.
     */
    public PlayerController(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.previousPositions = new HashMap<>();
        players.forEach(player -> {
            Objects.requireNonNull(player, "Player cannot be null");
            // Seed with the player's current position so the initial placement is a
            // direct positioning (no movement animation). For a loaded save this puts
            // players straight onto their saved tiles instead of replaying the path.
            previousPositions.put(player, player.getPositionIndex());
        });

        this.playerView = new PlayerView(board, players);
        this.playerAnimation = new PlayerAnimation(board, playerView);
    }

    /**
     * Registers a callback invoked once a player's movement/effect chain has fully
     * settled (walk, tile effect, and any resulting slide are all finished). This is
     * the authoritative end-of-turn signal.
     *
     * @param onTurnSettled the callback to run when movement fully settles
     */
    public void setOnTurnSettled(Runnable onTurnSettled) {
        this.onTurnSettled = onTurnSettled;
    }

    private void notifyTurnSettled() {
        if (onTurnSettled != null) {
            runLater(onTurnSettled);
        }
    }

    /**
     * Adds player sprites to the board UI and updates initial positions.
     */
    public void addPlayersToBoard(Pane boardPane) {
        playerView.addPlayersToBoard(boardPane);
        updatePlayerPositions();
    }

    /**
     * Updates visual player positions on the board.
     */
    public void updatePlayerPositions() {
        previousPositions.keySet().forEach(player -> {
            int currentPos = previousPositions.get(player);
            int targetPos = player.getPositionIndex();

            if (currentPos != targetPos) {
                animatePlayerMovement(player, currentPos, targetPos);
            } else {
                positionPlayer(player, board.getTiles().get(currentPos));
            }
        });
    }

    /**
     * Animates movement from one tile to another.
     */
    public void animatePlayerMovement(Player player, int fromPosition, int toPosition) {
        playerAnimating.set(true);
        playerAnimation.animatePlayerMovement(player, fromPosition, toPosition, () -> {
            handleMovementComplete(player, fromPosition, toPosition);
            PauseTransition cleanupWait = new PauseTransition(Duration.millis(100));
            cleanupWait.setOnFinished(e -> playerAnimating.set(false));
            cleanupWait.play();

        });
    }

    /**
     * Called after a player's movement animation finishes.
     */
    private void handleMovementComplete(Player player, int from, int to) {
        previousPositions.put(player, to);
        if (to >= board.getTiles().size()) return;

        Tile targetTile = board.getTiles().get(to);
        PauseTransition pause = new PauseTransition(Duration.millis(EFFECT_DELAY_MS));
        pause.setOnFinished(e -> {
            if (targetTile.getTileAction() != null) {
                processSpecialTileEffects(player, targetTile, from, () -> handlePostEffectPositioning(player, to));
            } else {
                positionPlayer(player, targetTile);
                player.finishMove();
                LOGGER.info("Normal DONE");
                notifyTurnSettled();
            }
        });
        pause.play();
    }

    /**
     * Handles visual repositioning after tile effects are applied.
     */
    private void handlePostEffectPositioning(Player player, int originalTo) {
        int newPos = player.getPositionIndex();
        Tile newTile = board.getTiles().get(newPos);

        if (newPos != originalTo && Math.abs(newPos - originalTo) > 1) {
            animatePlayerMovement(player, originalTo, newPos);
        } else {
            positionPlayer(player, newTile);
            // The player has stopped moving and all tile effects have resolved:
            // this is the true end of the movement chain for this turn.
            notifyTurnSettled();
        }
    }

    /**
     * Sets a player's image on a tile and rotates it accordingly.
     */
    private void positionPlayer(Player player, Tile tile) {
        Platform.runLater(() -> {
            playerView.positionPlayerAtTile(player, tile);
            setRotationForTile(player, tile);
        });
    }

    /**
     * Sets the rotation for a player's sprite based on tile layout.
     */
    private void setRotationForTile(Player player, Tile tile) {
        ImageView sprite = playerView.getPlayerSprite(player);
        if (sprite != null) {
            sprite.setRotate(BoardUtils.getRotationForTile(board, tile));
        }
    }

    /**
     * Handles the logic when a player lands on a special tile.
     */
    private void processSpecialTileEffects(Player player, Tile tile, int prevPos, Runnable onComplete) {
        PauseTransition pause = new PauseTransition(Duration.millis(LADDER_DELAY_MS));
        pause.setOnFinished(e -> {
            TileAction action = tile.getTileAction();
            if (action instanceof LadderAction ladder) {
                // Move the player along the ladder/snake. This runs for every landing
                // on a ladder tile, so a chance effect that lands a player on a ladder
                // triggers the ladder too.
                SoundEffects.playLadder(ladder.getDestinationTileIndex() > prevPos);
                ladder.execute(player);
                runLater(onComplete);
            } else if (action instanceof CosmicChanceAction chance) {
                if (player.isChanceActivatedThisTurn()) {
                    // This chance tile was reached by another chance effect during the
                    // same turn. A chance effect triggers only once per turn, so the
                    // destination tile must not activate its own effect now.
                    runLater(onComplete);
                } else {
                    player.setChanceActivatedThisTurn(true);
                    List<Player> allPlayers = new ArrayList<>(getPlayers());
                    new ChanceTileView().showChancePopup(player, chance, () -> {
                        chance.executeEffect(player, allPlayers);
                        runLater(onComplete);
                    });
                }
            } else {
                runLater(onComplete);
            }
        });
        pause.play();
        player.finishMove();
        LOGGER.info("Tile Done");

    }

    /**
     * Helper to execute callbacks safely on the JavaFX thread.
     */
    private void runLater(Runnable runnable) {
        if (runnable != null) Platform.runLater(runnable);
    }

    /**
     * @return true if any player animation is currently in progress.
     */
    public boolean hasActiveAnimations() {
        return playerAnimating.get();
    }

    public Set<Player> getPlayers() {
        return previousPositions.keySet();
    }

    @Override
    public void update(Observable<Player> observable, String eventType) {
        updatePlayerPositions();
    }

    public PlayerView getPlayerView() {
        return playerView;
    }
}
