package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Die;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Tile;
import edu.ntnu.iir.bidata.view.BoardView;
import edu.ntnu.iir.bidata.view.DieView;
import edu.ntnu.iir.bidata.view.PlayerView;
import javafx.animation.ParallelTransition;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class BoardGame {
  private Board board;
  private Player[] players;
  private int currentPlayerIndex;
  private Die die;
  private BoardView boardView;

  public BoardGame() {
    this.board = new Board();
    this.players = new Player[0]; // Initialize with an empty array
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.boardView = new BoardView(this);
  }

  public BoardGame(Board board) {
    this.board = board;
    this.players = new Player[0]; // Initialize with an empty array
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

  public void playTurn(DieView dieView, PlayerView playerView) {
    Player currentPlayer = getCurrentPlayer();

    // Set a callback to execute after the dice animation completes
    dieView.setOnAnimationComplete(() -> {
      int roll = die.getLastRoll();
      currentPlayer.move(roll);

      // Ensure the player does not move beyond the last tile
      if (currentPlayer.getPositionIndex() >= board.getTiles().size()) {
        currentPlayer.setPositionIndex(board.getTiles().size() - 1);
      }

      // Perform the action on the tile the player lands on
      Tile currentTile = board.getTiles().get(currentPlayer.getPositionIndex());
      currentTile.landOn(currentPlayer);

      // Switch to the next player
      currentPlayerIndex = (currentPlayerIndex + 1) % players.length;

      System.out.println(currentPlayer.getName() + " rolled " + roll + " and is now at position " + currentPlayer.getPositionIndex());
    });

    // Roll the die (this triggers the dice animation via observer pattern)
    die.roll();
  }

  @Override
  public String toString() {
    return "BoardGame{" + "board=" + board + ", players=" + Arrays.toString(players) + ", die=" + die + '}';
  }

}