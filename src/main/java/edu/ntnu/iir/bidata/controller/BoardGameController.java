package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.controller.board.DieController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.BoardUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main controller responsible for managing the board game state and game flow.
 * This class coordinates player turns, die rolls, and movement on the board.
 */
public class BoardGameController {
  private static final Logger LOGGER = Logger.getLogger(BoardGameController.class.getName());

  // Game state constants
  private enum GameState { WAITING_FOR_PLAYERS, READY_TO_START, TURN_IN_PROGRESS, WAITING_FOR_TURN, GAME_OVER }

  // Game components
  private Board board;
  private List<Player> players;
  private int currentPlayerIndex;
  private final Die die;
  private final List<Player> playerRanks;
  private GameState gameState;

  /**
   * Creates a new game controller with a default board.
   */
  public BoardGameController() {
    this.board = new Board();
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
    this.gameState = GameState.WAITING_FOR_PLAYERS;
    LOGGER.info("BoardGameController initialized with default board");
  }

  /**
   * Creates a new game controller with the specified board.
   *
   * @param board the game board
   * @throws NullPointerException if board is null
   */
  public BoardGameController(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
    this.gameState = GameState.WAITING_FOR_PLAYERS;
    LOGGER.info("BoardGameController initialized with custom board");
  }

  /**
   * Gets the game board.
   *
   * @return the board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets the list of players.
   *
   * @return unmodifiable list of players
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  /**
   * Sets the list of players for the game.
   *
   * @param players the list of players
   * @throws IllegalArgumentException if players is null or empty
   */
  public void setPlayers(List<Player> players) {
    if (players == null || players.isEmpty()) {
      throw new IllegalArgumentException("Players list cannot be null or empty.");
    }
    this.players = new ArrayList<>(players);
    this.currentPlayerIndex = 0;
    this.gameState = GameState.READY_TO_START;
    LOGGER.info("Players set: " + players.size() + " players");
  }

  /**
   * Sets the game board.
   *
   * @param board the board to set
   * @throws NullPointerException if board is null
   */
  public void setBoard(Board board) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    LOGGER.info("Game board updated");
  }

  /**
   * Gets the game die.
   *
   * @return the die
   */
  public Die getDie() {
    return die;
  }

  /**
   * Sets the current player index.
   *
   * @param currentPlayerIndex the index to set
   * @throws IllegalArgumentException if index is invalid
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    if (currentPlayerIndex < 0 || (players.size() > 0 && currentPlayerIndex >= players.size())) {
      throw new IllegalArgumentException("Invalid player index: " + currentPlayerIndex);
    }
    this.currentPlayerIndex = currentPlayerIndex;
    LOGGER.fine("Current player index set to: " + currentPlayerIndex);
  }

  /**
   * Gets the current player index.
   *
   * @return the current player index
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Gets the current player.
   *
   * @return the current player, or null if not available
   */
  public Player getCurrentPlayer() {
    if (players.isEmpty() || currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Returns an unmodifiable list of player ranks.
   *
   * @return the player ranks
   */
  public List<Player> getPlayerRanks() {
    return Collections.unmodifiableList(playerRanks);
  }

  /**
   * Sets the player ranks.
   *
   * @param playerRanks the list of player ranks
   * @throws NullPointerException if playerRanks is null
   */
  public void setPlayerRanks(List<Player> playerRanks) {
    this.playerRanks.clear();
    this.playerRanks.addAll(Objects.requireNonNull(playerRanks, "Player ranks cannot be null"));
    LOGGER.fine("Player ranks updated: " + playerRanks.size() + " players ranked");
  }

  /**
   * Plays a turn for the current player.
   *
   * @param dieController the die controller
   * @param onTurnComplete callback after turn is complete
   * @throws NullPointerException if dieController is null
   */
  public void playTurn(DieController dieController, Runnable onTurnComplete) {
    Objects.requireNonNull(dieController, "DieController cannot be null");

    if (isGameOver()) {
      LOGGER.info("Game is already over, ignoring turn request");
      executeCallback(onTurnComplete);
      return;
    }

    skipFinishedPlayers();

    Player currentPlayer = getCurrentPlayer();
    if (currentPlayer == null) {
      LOGGER.warning("No current player available for turn");
      executeCallback(onTurnComplete);
      return;
    }

    gameState = GameState.TURN_IN_PROGRESS;
    LOGGER.info("Starting turn for player: " + currentPlayer.getName());

    dieController.setOnAnimationComplete(() -> handlePlayerRoll(currentPlayer, onTurnComplete));
    die.roll();
    LOGGER.fine("Die rolled: " + die.getLastRoll());
  }

  private boolean isGameOver() {
    if (gameState == GameState.GAME_OVER || areAllPlayersFinished()) {
      gameState = GameState.GAME_OVER;
      currentPlayerIndex = -1;
      return true;
    }
    return false;
  }

  private void executeCallback(Runnable callback) {
    if (callback != null) {
      callback.run();
    }
  }

  /**
   * Checks if all players have finished the game.
   *
   * @return true if all players have reached the final tile
   */
  private boolean areAllPlayersFinished() {
    if (players.isEmpty() || board.getTiles().isEmpty()) {
      return false;
    }

    int finalPosition = board.getTiles().size() - 1;
    return players.stream().allMatch(player -> player.getPositionIndex() >= finalPosition);
  }

  /**
   * Advances the current player index to skip players who have already finished.
   */
  private void skipFinishedPlayers() {
    if (players.isEmpty()) return;

    int finalPosition = board.getTiles().size() - 1;
    int attempts = 0;

    while (currentPlayerIndex >= 0 &&
            currentPlayerIndex < players.size() &&
            players.get(currentPlayerIndex).getPositionIndex() >= finalPosition) {

      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      attempts++;

      if (attempts >= players.size()) {
        currentPlayerIndex = -1;
        gameState = GameState.GAME_OVER;
        LOGGER.info("All players have finished after checking");
        break;
      }
    }
  }

  /**
   * Handles player movement after a die roll.
   *
   * @param currentPlayer the player whose turn it is
   * @param onTurnComplete callback to run after turn is complete
   */
  private void handlePlayerRoll(Player currentPlayer, Runnable onTurnComplete) {
    int roll = die.getLastRoll();
    int boardSize = board.getTiles().size();
    int currentPosition = currentPlayer.getPositionIndex();
    int targetPosition = currentPosition + roll;

    // Handle reaching or exceeding the final position
    if (targetPosition >= boardSize - 1) {
      movePlayerToFinish(currentPlayer, currentPosition);
    } else {
      currentPlayer.move(roll);
    }

    // Apply tile effects
    applyTileEffects(currentPlayer);

    // Prepare for next turn
    advanceToNextPlayer();

    LOGGER.info(currentPlayer.getName() + " rolled " + roll + " and is now at position " +
            currentPlayer.getPositionIndex());

    executeCallback(onTurnComplete);
  }

  private void movePlayerToFinish(Player player, int currentPosition) {
    int finalPosition = board.getTiles().size() - 1;
    int moveDistance = finalPosition - currentPosition;

    player.move(moveDistance);

    if (!playerRanks.contains(player)) {
      playerRanks.add(player);
      LOGGER.info(player.getName() + " finished in position " + playerRanks.size());
    }
  }

  private void applyTileEffects(Player player) {
    int position = player.getPositionIndex();
    if (position >= 0 && position < board.getTiles().size()) {
      Tile currentTile = board.getTiles().get(position);
      currentTile.landOn(player);
    }
  }

  /**
   * Advances the game to the next player's turn.
   */
  private void advanceToNextPlayer() {
    if (players.isEmpty()) {
      return;
    }

    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

    if (areAllPlayersFinished()) {
      currentPlayerIndex = -1;
      gameState = GameState.GAME_OVER;
    } else {
      skipFinishedPlayers();
      gameState = GameState.WAITING_FOR_TURN;
    }
  }

  /**
   * Resets the game to initial state but keeps the same board and players.
   */
  public void resetGame() {
    players.forEach(player -> player.setPositionIndex(0));
    currentPlayerIndex = 0;
    playerRanks.clear();
    gameState = GameState.READY_TO_START;
    LOGGER.info("Game reset to initial state");
  }

  /**
   * Checks if the game has any active players.
   *
   * @return true if there are players in the game
   */
  public boolean hasPlayers() {
    return !players.isEmpty();
  }

  @Override
  public String toString() {
    return "BoardGameController{" +
            "board=" + board +
            ", players=" + players +
            ", currentPlayerIndex=" + currentPlayerIndex +
            ", die=" + die +
            ", gameState=" + gameState +
            '}';
  }
}