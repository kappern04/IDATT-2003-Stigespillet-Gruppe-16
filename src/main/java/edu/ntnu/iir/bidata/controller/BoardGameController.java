package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.controller.board.DieController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardGameController {
  private Board board;
  private Player[] players;
  private int currentPlayerIndex;
  private Die die;
  private ArrayList<Player> playerRanks;

  public BoardGameController() {
    this.board = new Board();
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
  }

  public BoardGameController(Board board) {
    this.board = board;
    this.players = new Player[0];
    this.currentPlayerIndex = 0;
    this.die = new Die();
    this.playerRanks = new ArrayList<>();
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

  /**
   * Returns the index of the current player and null if all players are finished.
   *
   * @return Index of the current player
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public Player getCurrentPlayer() {
    if (players == null || players.length == 0 || currentPlayerIndex == -1) {
      return null;
    }
    return players[currentPlayerIndex];
  }

  public List<Player> getPlayerRanks() {
    return playerRanks;
  }

  /**
   * Handles a player's turn by rolling the die and updating the game state.
   * @param dieController The controller for the die
   * @param onAnimationComplete Callback for when animations complete
   */
  public void playTurn(DieController dieController, Runnable onAnimationComplete) {
    // skip finished players before the turn
    boolean allFinished = true;

    for (Player p : players) {
      if (p.getPositionIndex() < board.getTiles().size() - 1) {
        allFinished = false;
        break;
      }
    }

    if (allFinished) {
      currentPlayerIndex = -1;
      if (onAnimationComplete != null) {
        onAnimationComplete.run();
      }
      return;
    }

    if (players[currentPlayerIndex].getPositionIndex() >= board.getTiles().size() - 1) {
      do {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
      } while (players[currentPlayerIndex].getPositionIndex() >= board.getTiles().size() - 1);
    }

    Player currentPlayer = getCurrentPlayer();

    dieController.setOnAnimationComplete(() -> {
      int roll = die.getLastRoll();
      if (currentPlayer.getPositionIndex() + roll >= board.getTiles().size() - 1) {
        roll = board.getTiles().size() - 1 - currentPlayer.getPositionIndex();
        if (!playerRanks.contains(currentPlayer)) {
          playerRanks.add(currentPlayer);
        }
      }
      currentPlayer.move(roll);

      Tile currentTile = board.getTiles().get(currentPlayer.getPositionIndex());
      currentTile.landOn(currentPlayer);

      boolean gameFinished = true;
      for (Player p : players) {
        if (p.getPositionIndex() < board.getTiles().size() - 1) {
          gameFinished = false;
          break;
        }
      }

      if (gameFinished) {
        currentPlayerIndex = -1;
      } else {
        // skip to the next unfinished player
        do {
          currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        } while (players[currentPlayerIndex].getPositionIndex() >= board.getTiles().size() - 1);
      }
      System.out.println(currentPlayer.getName() + " rolled " + roll + " and is now at position "
              + currentPlayer.getPositionIndex());

      if (onAnimationComplete != null) {
        onAnimationComplete.run();
      }
    });

    die.roll();
  }

  @Override
  public String toString() {
    return "BoardGame{" + "board=" + board + ", players=" + Arrays.toString(players) + ", die="
            + die + '}';
  }
}