package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.Observer;
import java.util.List;

public class BoardController {
    private Board board;
    private List<Player> players;

    public BoardController(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public Player[] getPlayersArray() {
        return players.toArray(new Player[0]);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tile> getTiles() {
        return board.getTiles();
    }

    public boolean hasLadderAction(Tile tile) {
        return tile.getTileAction() instanceof LadderAction;
    }

    public void registerPlayerObserver(Observer observer) {
        for (Player player : players) {
            player.addObserver(observer);
        }
    }
}