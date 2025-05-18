package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;

import java.util.List;
import java.util.Objects;

/**
 * Controller for the game board.
 * Responsible for managing the board state and player interaction with the board.
 */
public class BoardController {
    private final Board board;
    private final List<Player> players;

    /**
     * Creates a new board controller.
     *
     * @param board The game board
     * @param players List of players in the game
     * @throws NullPointerException if board or players is null
     */
    public BoardController(Board board, List<Player> players) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.players = Objects.requireNonNull(players, "Players list cannot be null");
    }

    /**
     * Gets the game board.
     *
     * @return The game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the list of players.
     *
     * @return List of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets all tiles on the board.
     *
     * @return List of tiles
     */
    public List<Tile> getTiles() {
        return board.getTiles();
    }

    /**
     * Checks if a tile has a ladder action.
     *
     * @param tile The tile to check
     * @return true if the tile has a ladder action, false otherwise
     * @throws NullPointerException if tile is null
     */
    public boolean hasLadderAction(Tile tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        return tile.hasLadderAction();
    }

    /**
     * Gets the destination tile for a ladder action.
     *
     * @param tile The tile with the ladder
     * @return The destination tile
     * @throws NullPointerException if tile is null
     */
    public Tile getDestinationTile(Tile tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        return tile.getLadderDestination(board);
    }

    /**
     * Registers an observer with all players in the game.
     * This allows the observer to receive updates when player positions change.
     *
     * @param observer The observer to register with all players
     * @throws NullPointerException if observer is null
     */
    public void registerPlayerObserver(Observer observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        for (Player player : players) {
            if (player instanceof Observable) {
                ((Observable<Observer>) player).addObserver(observer);
            }
        }
    }
}