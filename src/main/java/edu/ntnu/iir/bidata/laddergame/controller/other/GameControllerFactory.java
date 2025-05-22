package edu.ntnu.iir.bidata.laddergame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Die;
import edu.ntnu.iir.bidata.laddergame.view.board.DieView;

import java.util.List;

public class GameControllerFactory {
    public static BoardGameController createGameController(
            Board board,
            List<Player> players,
            boolean enableChanceTiles,
            int chancePercentage
    ) {
        PlayerController playerController = new PlayerController(board, players);
        Die die = new Die();
        DieView dieView = new DieView();
        DieController dieController = new DieController(die, dieView);

        if (enableChanceTiles) {
            return new ChanceBoardGame(board, playerController, dieController, chancePercentage);
        } else {
            BoardGameController game = new BoardGameController();
            game.setPlayers(players);
            game.setBoard(board);
            // Add setters if needed
            return game;
        }
    }
}