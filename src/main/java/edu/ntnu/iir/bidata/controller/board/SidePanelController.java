package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;

import java.util.List;
import java.util.Objects;
import javafx.scene.image.Image;

/**
 * Controller for the side panel that displays player information and dice controls.
 * Responsible for coordinating between the game state and the side panel view.
 */
public class SidePanelController implements Observer {
    private final BoardGameController boardGameController;
    private final PlayerController playerController;
    private Runnable onPlayerChangeCallback;

    /**
     * Creates a new side panel controller.
     *
     * @param boardGameController The main game controller
     * @param playerController The controller for player-related operations
     * @throws NullPointerException if any parameter is null
     */
    public SidePanelController(
            BoardGameController boardGameController, PlayerController playerController) {
        this.boardGameController = Objects.requireNonNull(boardGameController, "BoardGameController cannot be null");
        this.playerController = Objects.requireNonNull(playerController, "PlayerController cannot be null");
    }

    /**
     * Gets the list of players in the game.
     *
     * @return Array of players
     */
    public List<Player> getPlayers() {
        return boardGameController.getPlayers();
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return boardGameController.getCurrentPlayer();
    }

    /**
     * Gets the image representation for a player.
     *
     * @param player The player to get the image for
     * @return The player's image
     * @throws NullPointerException if player is null
     */
    public Image getPlayerImage(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        return playerController.getPlayerView().getPlayerImage(player);
    }

    /**
     * Gets the ranked list of players based on their positions.
     *
     * @return List of players sorted by rank
     */
    public List<Player> getPlayerRanks() {
        return boardGameController.getPlayerRanks();
    }

    /**
     * Sets a callback to be invoked when the current player changes.
     *
     * @param callback The callback to execute
     */
    public void setOnPlayerChangeCallback(Runnable callback) {
        this.onPlayerChangeCallback = callback;
    }

    /**
     * Delegates the turn logic to BoardGameController.
     *
     * @param dieController The controller for the die
     * @param onAnimationComplete Callback for when animations complete
     * @throws NullPointerException if dieController is null
     */
    public void playTurn(DieController dieController, Runnable onAnimationComplete) {
        Objects.requireNonNull(dieController, "DieController cannot be null");
        boardGameController.playTurn(dieController, onAnimationComplete);
    }

    /**
     * Updates the side panel when notified of changes from observable objects.
     *
     * @param observable The object that triggered the update
     * @param prompt Additional information about the update
     */
    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        if (prompt != null && prompt.equals("PLAYER_CHANGED") && onPlayerChangeCallback != null) {
            onPlayerChangeCallback.run();
        }
    }
}