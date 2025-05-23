package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Controller for the side panel. Provides access to player/game state
 * and notifies registered listeners of state changes.
 */
public class SidePanelController implements Observer<Player> {

    private final BoardGameController boardGameController;
    private final PlayerController playerController;
    private Consumer<String> onStateChanged;

    public SidePanelController(BoardGameController boardGameController, PlayerController playerController) {
        this.boardGameController = Objects.requireNonNull(boardGameController, "BoardGameController cannot be null");
        this.playerController = Objects.requireNonNull(playerController, "PlayerController cannot be null");

        // Register this controller to observe player updates
        for (Player player : getPlayers()) {
            if (player instanceof Observable<?>) {
                ((Observable<Player>) player).addObserver(this);
            }
        }
    }

    /**
     * Returns the list of players in the game.
     */
    public List<Player> getPlayers() {
        return boardGameController.getPlayers();
    }

    /**
     * Returns the current player.
     */
    public Player getCurrentPlayer() {
        return boardGameController.getCurrentPlayer();
    }

    /**
     * Returns the visual image associated with the given player.
     */
    public Image getPlayerImage(Player player) {
        return playerController.getPlayerView().getPlayerImage(Objects.requireNonNull(player));
    }

    /**
     * Returns the players ranked by position.
     */
    public List<Player> getPlayerRanks() {
        return boardGameController.getPlayerRanks();
    }

    /**
     * Sets a listener to be notified when any player state changes.
     */
    public void setOnStateChanged(Consumer<String> callback) {
        this.onStateChanged = callback;
    }

    /**
     * Plays a turn using the provided die controller and triggers a callback
     * once animations are complete.
     */
    public void playTurn(DieController dieController, Runnable onAnimationComplete) {
        Objects.requireNonNull(dieController, "DieController cannot be null");
        boardGameController.playTurn(dieController, onAnimationComplete);
    }

    /**
     * Notified when a player state changes. Delegates event to the registered UI callback.
     */
    @Override
    public void update(Observable<Player> observable, String eventType) {
        if (onStateChanged != null) {
            onStateChanged.accept(eventType);
        }
    }
}
