package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.board.DieView;
import java.util.List;
import javafx.scene.image.Image;

/**
 * Controller for the side panel that displays player information and dice controls.
 */
public class SidePanelController {
    private BoardGameController boardGameController;
    private PlayerController playerController;

    public SidePanelController(BoardGameController boardGameController) {
        this.boardGameController = boardGameController;

        // Create a player controller to access player visuals
        this.playerController = new PlayerController(
                boardGameController.getBoard(),
                boardGameController.getPlayers()
        );

        // Register the player controller as observer
        for (Player player : boardGameController.getPlayers()) {
            player.addObserver(playerController);
        }
    }

    public Player[] getPlayers() {
        return boardGameController.getPlayers();
    }

    public Player getCurrentPlayer() {
        return boardGameController.getCurrentPlayer();
    }

    public Image getPlayerImage(Player player) {
        return playerController.getPlayerView().getPlayerImage(player);
    }

    public Die getDie() {
        return boardGameController.getDie();
    }

    /**
     * Delegates the turn logic to BoardGameController.
     * @param dieView The view for the die
     * @param onAnimationComplete Callback for when animations complete
     */
    public void playTurn(DieView dieView, Runnable onAnimationComplete) {
        boardGameController.playTurn(dieView, onAnimationComplete);
    }

    public List<Player> getPlayerRanks() {
        return boardGameController.getPlayerRanks();
    }
}