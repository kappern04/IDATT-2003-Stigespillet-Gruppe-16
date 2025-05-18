package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.controller.board.DieController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Main controller responsible for managing the board game state and game flow.
 * This class coordinates player turns, die rolls, and movement on the board.
 */
public class BoardGameController {
  private Board board;
  private List<Player> players;
  private int currentPlayerIndex;
  private final Die die;
  private final List<Player> playerRanks;

  public BoardGameController() {
    this.board = new Board();
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
  }

  public BoardGameController(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
  }

  public Board getBoard() {
    return board;
  }

  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Sets the list of players for the game.
   * @param players the list of players
   */
  public void setPlayers(List<Player> players) {
    if (players == null || players.isEmpty()) {
      throw new IllegalArgumentException("Players list cannot be null or empty.");
    }
    this.players = players;
  }

  /**
   * Sets the game board.
   * @param board the board to set
   */
  public void setBoard(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
  }

  public Die getDie() {
    return die;
  }

  /**
   * Sets the current player index.
   * @param currentPlayerIndex the index to set
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    if (currentPlayerIndex < 0 || (players.size() > 0 && currentPlayerIndex >= players.size())) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    this.currentPlayerIndex = currentPlayerIndex;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Gets the current player.
   * @return the current player, or null if not available
   */
  public Player getCurrentPlayer() {
    if (players == null || players.isEmpty() || currentPlayerIndex == -1) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Returns an unmodifiable list of player ranks.
   * @return the player ranks
   */
  public List<Player> getPlayerRanks() {
    return Collections.unmodifiableList(playerRanks);
  }

  /**
   * Sets the player ranks.
   * @param playerRanks the list of player ranks
   */
  public void setPlayerRanks(List<Player> playerRanks) {
    this.playerRanks.clear();
    this.playerRanks.addAll(Objects.requireNonNull(playerRanks, "Player ranks cannot be null"));
  }

  /**
   * Plays a turn for the current player.
   * @param dieController the die controller
   * @param onAnimationComplete callback after animation
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
    return players.stream().allMatch(player -> player.getPositionIndex() >= board.getTiles().size() - 1);
  }

  private void skipFinishedPlayers() {
    if (players == null || players.isEmpty()) return;
    int attempts = 0;
    while (players.get(currentPlayerIndex).getPositionIndex() >= board.getTiles().size() - 1) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      attempts++;
      if (attempts > players.size()) {
        currentPlayerIndex = -1;
        break;
      }
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

    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

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
    return "BoardGame{" + "board=" + board + ", players=" + players + ", die=" + die + '}';
  }
}