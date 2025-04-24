package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.DieView;
import edu.ntnu.iir.bidata.view.PlayerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PointBoardGame extends BoardGame{

  private HashMap<Player, Integer> playerPoints;
  private List<Integer> pointItemLocations;
  private Random random;

  public PointBoardGame(Board board) {
    super(board);
    this.playerPoints = new HashMap<>();
    this.random = new Random();
    this.pointItemLocations = new ArrayList<>();
    for (Player player : super.getPlayers()) {
      this.playerPoints.put(player, 0);
    }
    for(int i = 0; i < 5; i++) {
      spawnPointItem();
    }
  }

  private void spawnPointItem() {
    int maxTiles = getBoard().getTiles().size();
    int randomLocation;
    do {
      randomLocation = random.nextInt(maxTiles);
    } while (pointItemLocations.contains(randomLocation));
    pointItemLocations.add(randomLocation);
    System.out.println("Spawned point item at location: " + randomLocation);

    super.notifyObservers();
  }

  private void collectPointItem(int pos, Player p) {
    if (pointItemLocations.contains(pos)) {
      pointItemLocations.remove(pos);
      playerPoints.compute(p, (k, currentPoints) -> currentPoints + 1);
      spawnPointItem();
    }
  }

  @Override
  public void playTurn(DieView dieView, PlayerView playerView) {
    Player currentPlayer = getCurrentPlayer();

    dieView.setOnAnimationComplete(() -> {
      int roll = getDie().getLastRoll();
      currentPlayer.move(roll);

      // Player
      if (currentPlayer.getPositionIndex() >= getBoard().getTiles().size()-1) {
        currentPlayer.setPositionIndex(currentPlayer.getPositionIndex()-getBoard().getTiles().size()-1);
        System.out.println(currentPlayer.getName() + " reached the end and teleported back to start!");
      }

      collectPointItem(currentPlayer.getPositionIndex(), currentPlayer);

      // Execute tile action
      getBoard().getTiles().get(currentPlayer.getPositionIndex()).landOn(currentPlayer);

      // Switch to next player
      setCurrentPlayerIndex((getCurrentPlayerIndex() + 1) % getPlayers().length);

      System.out.println(currentPlayer.getName() + " rolled " + roll +
          " and is now at position " + currentPlayer.getPositionIndex());
    });

    getDie().roll();
  }

  public HashMap<Player, Integer> getPlayerPoints() {
    return playerPoints;
  }

  public List<Integer> getPointItemLocations() {
    return pointItemLocations;
  }
}