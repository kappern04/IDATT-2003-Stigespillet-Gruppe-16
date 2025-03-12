package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class PlayerView {

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

  public void updatePlayerPositions() {
    for (Map.Entry<Player, ImageView> entry : playerSprites.entrySet()) {
      Player player = entry.getKey();
      ImageView sprite = entry.getValue();
      Tile tile = board.getTiles()[player.getPosition()];

      sprite.setTranslateX(getTileCenterX(tile) - 350);
      sprite.setTranslateY(getTileCenterY(tile) - 385);

      if (((tile.getTileNumber() - 1) / 9) % 2 == 1) {
        sprite.setRotate(-90);
      } else {
        sprite.setRotate(90);
      }
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
}
