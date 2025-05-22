package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.scene.image.Image;

/**
 * Controller for the side panel that exposes player/game state and notifies listeners of changes.
 */
public class SidePanelController implements Observer<Player> {
    private final BoardGameController boardGameController;
    private final PlayerController playerController;
    private Consumer<String> onStateChanged;

    public SidePanelController(BoardGameController boardGameController, PlayerController playerController) {
        this.boardGameController = Objects.requireNonNull(boardGameController);
        this.playerController = Objects.requireNonNull(playerController);
        // Observe player changes
        boardGameController.getPlayers().forEach(p -> p.addObserver(this));
    }

    public List<Player> getPlayers() {
        return boardGameController.getPlayers();
    }

    public Player getCurrentPlayer() {
        return boardGameController.getCurrentPlayer();
    }

    public Image getPlayerImage(Player player) {
        Objects.requireNonNull(player);
        return playerController.getPlayerView().getPlayerImage(player);
    }

    public List<Player> getPlayerRanks() {
        return boardGameController.getPlayerRanks();
    }

    public void setOnStateChanged(Consumer<String> callback) {
        this.onStateChanged = callback;
    }

    public void playTurn(DieController dieController, Runnable onAnimationComplete) {
        Objects.requireNonNull(dieController);
        boardGameController.playTurn(dieController, onAnimationComplete);
    }

    @Override
    public void update(Observable<Player> observable, String eventType) {
        if (onStateChanged != null) {
            onStateChanged.accept(eventType);
        }
    }
}