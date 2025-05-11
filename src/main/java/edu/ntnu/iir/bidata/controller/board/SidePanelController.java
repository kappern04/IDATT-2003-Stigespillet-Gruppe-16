package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Player;
import java.util.List;
import javafx.scene.image.Image;

/**
 * Controller for the side panel that displays player information and dice controls.
 */
public class SidePanelController {
    private BoardGameController boardGameController;
    private PlayerController playerController;

  public SidePanelController(
      BoardGameController boardGameController, PlayerController playerController) {
    this.boardGameController = boardGameController;
    this.playerController = playerController;
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

    public List<Player> getPlayerRanks() {
        return boardGameController.getPlayerRanks();
    }

    /**
     * Delegates the turn logic to BoardGameController.
     * @param dieController The controller for the die
     * @param onAnimationComplete Callback for when animations complete
     */
    public void playTurn(DieController dieController, Runnable onAnimationComplete) {
        boardGameController.playTurn(dieController, onAnimationComplete);
    }
}