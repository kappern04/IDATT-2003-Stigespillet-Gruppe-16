package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.controller.board.DieController;

import java.util.Arrays;

public class BoardGameController {
  private Board board;
  private Player[] players;
  private int currentPlayerIndex;
  private Die die;

  public BoardGameController() {
    this.board = new Board();
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
  }

  public BoardGameController(Board board) {
    this.board = board;
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
  }

  public Board getBoard() {
    return board;
  }

  public Player[] getPlayers() {
    return players;
  }

  public void setPlayers(Player[] players) {
    if (players == null || players.length == 0) {
      throw new IllegalArgumentException("Players array cannot be null or empty.");
    }
    this.players = players;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Die getDie() {
    return die;
  }

  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    if (currentPlayerIndex < 0 || currentPlayerIndex >= players.length) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    this.currentPlayerIndex = currentPlayerIndex;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public Player getCurrentPlayer() {
    if (players == null || players.length == 0) {
      throw new IllegalStateException("No players available to get the current player.");
    }
    return players[currentPlayerIndex];
  }

  /**
   * Handles a player's turn by rolling the die and updating the game state.
   * @param dieController The controller for the die
   * @param onAnimationComplete Callback for when animations complete
   */
  public void playTurn(DieController dieController, Runnable onAnimationComplete) {
    Player currentPlayer = getCurrentPlayer();

    dieController.setOnAnimationComplete(() -> {
      int roll = die.getLastRoll();
      currentPlayer.move(roll);

      if (currentPlayer.getPositionIndex() >= board.getTiles().size()) {
        currentPlayer.setPositionIndex(board.getTiles().size() - 1);
      }

      Tile currentTile = board.getTiles().get(currentPlayer.getPositionIndex());
      currentTile.landOn(currentPlayer);

      currentPlayerIndex = (currentPlayerIndex + 1) % players.length;

      System.out.println(currentPlayer.getName() + " rolled " + roll + " and is now at position " + currentPlayer.getPositionIndex());

      if (onAnimationComplete != null) {
        onAnimationComplete.run();
      }
    });

    die.roll();
  }

  @Override
  public String toString() {
    return "BoardGame{" + "board=" + board + ", players=" + Arrays.toString(players) + ", die=" + die + '}';
  }
}