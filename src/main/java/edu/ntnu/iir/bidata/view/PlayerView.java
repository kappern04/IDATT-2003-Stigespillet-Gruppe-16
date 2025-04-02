package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class PlayerView {

  private static final int TILE_SIZE = 70;
  private static final int BOARD_OFFSET_X = 350;
  private static final int BOARD_OFFSET_Y = 385;
  private static final double SPRITE_SIZE = 32.0;

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
    imageView.setFitWidth(SPRITE_SIZE);
    imageView.setFitHeight(SPRITE_SIZE);
    return imageView;
  }

  public void addPlayersToBoard(StackPane boardPane) {
    playerSprites.values().forEach(boardPane.getChildren()::add);
    updatePlayerPositions();
  }

  public void updatePlayerPositions() {
    playerSprites.forEach((player, sprite) -> animatePlayerMovement(player, sprite));
  }

  private void animatePlayerMovement(Player player, ImageView sprite) {
    Tile tile = board.getTiles()[player.getPosition()];

    double targetX = getTileCenterX(tile) - BOARD_OFFSET_X;
    double targetY = getTileCenterY(tile) - BOARD_OFFSET_Y;
    double targetRotation = getRotationForTile(tile);

    TranslateTransition translate = createTranslateTransition(sprite, targetX, targetY);
    RotateTransition rotate = createRotateTransition(sprite, targetRotation);

    ParallelTransition animation = new ParallelTransition(translate, rotate);
    animation.play();
  }

  private TranslateTransition createTranslateTransition(ImageView sprite, double targetX, double targetY) {
    TranslateTransition translate = new TranslateTransition(Duration.millis(500), sprite);
    translate.setToX(targetX);
    translate.setToY(targetY);
    translate.setInterpolator(Interpolator.EASE_BOTH);
    return translate;
  }

  private RotateTransition createRotateTransition(ImageView sprite, double targetRotation) {
    RotateTransition rotate = new RotateTransition(Duration.millis(300), sprite);
    rotate.setToAngle(targetRotation);
    rotate.setInterpolator(Interpolator.EASE_BOTH);
    return rotate;
  }

  private double getTileCenterX(Tile tile) {
    int col = (tile.getTileNumber() - 1) % 9;
    if (isOddRow(tile)) {
      col = 8 - col;
    }
    return col * TILE_SIZE + TILE_SIZE;
  }

  private double getTileCenterY(Tile tile) {
    int row = (tile.getTileNumber() - 1) / 9;
    return (9 - row) * TILE_SIZE + TILE_SIZE;
  }

  private double getRotationForTile(Tile tile) {
    return isOddRow(tile) ? -90 : 90;
  }

  private boolean isOddRow(Tile tile) {
    return ((tile.getTileNumber() - 1) / 9) % 2 == 1;
  }
}
