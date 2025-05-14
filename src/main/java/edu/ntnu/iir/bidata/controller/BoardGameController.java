package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.controller.board.DieController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Main controller responsible for managing the board game state and game flow.
 * This class coordinates player turns, die rolls, and movement on the board.
 */
public class BoardGameController {
  private Board board;
  private Player[] players;
  private int currentPlayerIndex;
  private final Die die;
  private final ArrayList<Player> playerRanks;

  /**
   * Creates a new BoardGameController with default board.
   */
  public BoardGameController() {
    this.board = new Board();
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
  }

  /**
   * Creates a new BoardGameController with the specified board.
   *
   * @param board The game board
   * @throws NullPointerException if board is null
   */
  public BoardGameController(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
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
   * Gets the players in the game.
   *
   * @return Array of players
   */
  public Player[] getPlayers() {
    return players;
  }

  /**
   * Sets the players for the game.
   *
   * @param players Array of players
   * @throws IllegalArgumentException if players is null or empty
   */
  public void setPlayers(Player[] players) {
    if (players == null || players.length == 0) {
      throw new IllegalArgumentException("Players array cannot be null or empty.");
    }
    this.players = players;
  }

  /**
   * Sets the game board.
   *
   * @param board The game board
   * @throws NullPointerException if board is null
   */
  public void setBoard(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
  }

  /**
   * Gets the die used in the game.
   *
   * @return The die
   */
  public Die getDie() {
    return die;
  }

  /**
   * Sets the index of the current player.
   *
   * @param currentPlayerIndex Index of the current player
   * @throws IllegalArgumentException if index is invalid
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    if (currentPlayerIndex < 0 || (players.length > 0 && currentPlayerIndex >= players.length)) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    this.currentPlayerIndex = currentPlayerIndex;
  }

  /**
   * Returns the index of the current player or -1 if all players are finished.
   *
   * @return Index of the current player
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Gets the player whose turn it currently is.
   *
   * @return The current player or null if no current player
   */
  public Player getCurrentPlayer() {
    if (players == null || players.length == 0 || currentPlayerIndex == -1) {
      return null;
    }
    return players[currentPlayerIndex];
  }

  /**
   * Gets the list of players ranked by finish order.
   *
   * @return List of ranked players
   */
  public List<Player> getPlayerRanks() {
    return new ArrayList<>(playerRanks);
  }

  /**
   * Sets the player rankings.
   *
   * @param playerRanks List of players in rank order
   * @throws NullPointerException if playerRanks is null
   */
  public void setPlayerRanks(List<Player> playerRanks) {
    this.playerRanks.clear();
    this.playerRanks.addAll(Objects.requireNonNull(playerRanks, "Player ranks cannot be null"));
  }

  /**
   * Handles a player's turn by rolling the die and updating the game state.
   *
   * @param dieController The controller for the die
   * @param onAnimationComplete Callback for when animations complete
   * @throws NullPointerException if dieController is null
   */
  public void playTurn(DieController dieController, Runnable onAnimationComplete) {
    Objects.requireNonNull(dieController, "DieController cannot be null");

    if (areAllPlayersFinished()) {
      currentPlayerIndex = -1;
      if (onAnimationComplete != null) {
        onAnimationComplete.run();
      }
      return;
    }

    skipFinishedPlayers();

    Player currentPlayer = getCurrentPlayer();
    dieController.setOnAnimationComplete(() -> handlePlayerRoll(currentPlayer, onAnimationComplete));
    die.roll();
  }

  private boolean areAllPlayersFinished() {
    return Arrays.stream(players).allMatch(player -> player.getPositionIndex() >= board.getTiles().size() - 1);
  }

  private void skipFinishedPlayers() {
    while (players[currentPlayerIndex].getPositionIndex() >= board.getTiles().size() - 1) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
  }

  private void handlePlayerRoll(Player currentPlayer, Runnable onAnimationComplete) {
    int roll = die.getLastRoll();
    int boardSize = board.getTiles().size();

    if (currentPlayer.getPositionIndex() + roll >= boardSize - 1) {
      roll = boardSize - 1 - currentPlayer.getPositionIndex();
      if (!playerRanks.contains(currentPlayer)) {
        playerRanks.add(currentPlayer);
      }
    }

    currentPlayer.move(roll);
    Tile currentTile = board.getTiles().get(currentPlayer.getPositionIndex());
    currentTile.landOn(currentPlayer);

    // Advance to the next player whether the current player is finished or not
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;

    // Skip players who have already finished
    if (!areAllPlayersFinished()) {
      skipFinishedPlayers();
    } else {
      currentPlayerIndex = -1;
    }

    System.out.println(currentPlayer.getName() + " rolled " + roll + " and is now at position "
            + currentPlayer.getPositionIndex());

    if (onAnimationComplete != null) {
      onAnimationComplete.run();
    }
  }

  @Override
  public String toString() {
    return "BoardGame{" + "board=" + board + ", players=" + Arrays.toString(players) + ", die="
            + die + '}';
  }
}