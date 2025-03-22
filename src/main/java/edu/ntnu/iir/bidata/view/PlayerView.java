package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Observable;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import javafx.util.Duration;

public class PlayerView implements Observer{

  private Board board;
  private Map<Player, ImageView> playerSprites;

  public PlayerView(Board board, Player[] players) {
    this.board = board;
    this.playerSprites = new HashMap<>();

    for (int i = 0; i < players.length; i++) {
      ImageView playerImage = createPlayerImage("/image/Player_" + (i + 1) + ".png");
      playerSprites.put(players[i], playerImage);
    }
  }

  private ImageView createPlayerImage(String imagePath) {
    Image image = new Image(getClass().getResourceAsStream(imagePath));
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(32);
    imageView.setFitHeight(32);
    return imageView;
  }

  public void addPlayersToBoard(StackPane boardPane) {
    for (ImageView sprite : playerSprites.values()) {
      boardPane.getChildren().add(sprite);
    }
    updatePlayerPositions();
  }

  private void updatePlayerPositions() {
    for (Map.Entry<Player, ImageView> entry : playerSprites.entrySet()) {
      Player player = entry.getKey();
      ImageView sprite = entry.getValue();
      Tile tile = board.getTiles()[player.getPosition()];

      // needs to be reworked so that the ship goes through a wormhole
      // may need to refactor how the turn is played
      double targetX = getTileCenterX(tile) - 350;
      double targetY = getTileCenterY(tile) - 385;
      double targetRotation = (((tile.getTileNumber() - 1) / 9) % 2 == 1) ? -90 : 90;

      TranslateTransition translate = new TranslateTransition(Duration.millis(500), sprite);
      translate.setToX(targetX);
      translate.setToY(targetY);

      RotateTransition rotate = new RotateTransition(Duration.millis(100), sprite);
      rotate.setToAngle(targetRotation);

      translate.play();
      rotate.play();
    }
  }

  private double getTileCenterX(Tile tile) {
    int col = (tile.getTileNumber() - 1) % 9;

    if (((tile.getTileNumber() - 1) / 9) % 2 == 1) {
      col = 8 - col;
    }

    return col * 70 + 70;
  }

  private double getTileCenterY(Tile tile) {
    int row = (tile.getTileNumber() - 1) / 9;
    return (9 - row) * 70 + 70;
  }

  @Override
  public <T extends Observer> void update(Observable<T> observable, String prompt) {
    updatePlayerPositions();
    System.out.println("Movement animating.");
  }
}
