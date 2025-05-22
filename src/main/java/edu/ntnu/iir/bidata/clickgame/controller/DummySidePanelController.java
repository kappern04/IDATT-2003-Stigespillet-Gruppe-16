package edu.ntnu.iir.bidata.clickgame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.util.ShipUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummySidePanelController {
    private final BoardGameController boardGameController;
    private final PlayerController playerController;
    private Player currentPlayer;
    private int totalClicks = 0;
    private final Map<Player, Integer> playerClicks = new HashMap<>();

    public DummySidePanelController(BoardGameController boardGameController, PlayerController playerController) {
        this.boardGameController = boardGameController;
        this.playerController = playerController;

        // Initialize click count for each player
        for (Player player : playerController.getPlayers()) {
            playerClicks.put(player, 0);
        }
    }

    public Image getPlayerImage(Player player) {
        int shipType = player.getShipType();
        Color color = player.getColor();
        Image baseImage = ShipUtils.loadShipSprite(shipType);
        ImageView coloredView = ShipUtils.createColoredShipImage(color, baseImage);
        return coloredView.getImage();
    }

    public List<Player> getPlayers() {
        return playerController.getPlayers().stream()
                .map(Player::getPlayer)
                .toList();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public int getTotalClicks() {
        return totalClicks;
    }

    public void incrementTotalClicks() {
        totalClicks++;
    }

    public int getPlayerClicks(Player player) {
        return playerClicks.getOrDefault(player, 0);
    }

    public void incrementPlayerClicks(Player player) {
        int clicks = playerClicks.getOrDefault(player, 0);
        playerClicks.put(player, clicks + 1);
    }

    public Player determineWinner() {
        Player winner = null;
        int maxClicks = -1;

        for (Map.Entry<Player, Integer> entry : playerClicks.entrySet()) {
            if (entry.getValue() > maxClicks) {
                maxClicks = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }

    public List<Player> determineWinners() {
        List<Player> winners = new java.util.ArrayList<>();
        int maxClicks = -1;

        // Find maximum clicks
        for (Map.Entry<Player, Integer> entry : playerClicks.entrySet()) {
            if (entry.getValue() > maxClicks) {
                maxClicks = entry.getValue();
            }
        }

        // Find all players with max clicks
        for (Map.Entry<Player, Integer> entry : playerClicks.entrySet()) {
            if (entry.getValue() == maxClicks) {
                winners.add(entry.getKey());
            }
        }

        return winners;
    }

    public void resetGame() {
        totalClicks = 0;
        playerClicks.replaceAll((player, v) -> 0);
        currentPlayer = null;
    }
}