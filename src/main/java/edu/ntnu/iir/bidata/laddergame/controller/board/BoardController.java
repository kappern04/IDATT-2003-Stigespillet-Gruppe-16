package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import java.util.List;
import java.util.Objects;

/**
 * Controller for the game board.
 * Responsible for managing the board state and player interaction with the board.
 */
public class BoardController {
    private final Board board;
    private final List<Player> players;

    public BoardController(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.players = Objects.requireNonNull(players, "Players list cannot be null");
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tile> getTiles() {
        return board.getTiles();
    }

    public boolean hasLadderAction(Tile tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        return tile.hasLadderAction();
    }

    public Tile getDestinationTile(Tile tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        return tile.getLadderDestination(board);
    }

    public void registerPlayerObserver(Observer<Player> observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        for (Player player : players) {
            player.addObserver(observer);
        }
    }
}