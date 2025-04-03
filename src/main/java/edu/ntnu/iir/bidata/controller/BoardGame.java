package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Die;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Tile;
import edu.ntnu.iir.bidata.view.Observer;
import java.util.Arrays;

public class BoardGame {
  private Board board;
  private Player[] players;
  private int currentPlayerIndex;
  private Die die;

  public BoardGame() {
    this.board = new Board();
    this.players = new Player[2];
    this.players[0] = new Player("Player 1");
    this.players[1] = new Player("Player 2");
    this.currentPlayerIndex = 0;
    this.die = new Die<Observer>();
  }

  public BoardGame(Board board) {
    this.board = board;
    this.players = new Player[2];
    this.players[0] = new Player("Player 1");
    this.players[1] = new Player("Player 2");
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
    this.players = players;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Die getDie() {
    return die;
  }

  public void playTurn() {
    Player currentPlayer = players[currentPlayerIndex];
    die.roll();
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
    System.out.println(currentPlayer.getName() + ": " + currentPlayer.getPositionIndex());
  }

  @Override
  public String toString() {
    return "BoardGame{" + "board=" + board + ", players=" + Arrays.toString(players) + ", die=" + die + '}';
  }
}
