package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.BoardUtils;
import edu.ntnu.iir.bidata.util.ShipUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.effect.DropShadow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerView {
  private final Board board;
  private final Map<Player, ImageView> playerSprites;
  private final Map<Player, Image> baseSprites;

  public PlayerView(Board board, Player[] players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.playerSprites = new HashMap<>();
    this.baseSprites = new HashMap<>();

    // Default colors if player doesn't specify one
    Color[] defaultColors = {
            Color.RED, Color.BLUE, Color.PURPLE, Color.ORANGE,
            Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE
    };

    for (int i = 0; i < players.length; i++) {
      Player player = players[i];
      if (player == null) throw new IllegalArgumentException("Player at index " + i + " is null");

      Color playerColor = player.getColor();
      if (playerColor == null) {
        playerColor = defaultColors[i % defaultColors.length];
        player.setColor(playerColor);
      }

      // Load the player's specific ship type (or default to Ship_1)
      int shipType = player.getShipType() > 0 && player.getShipType() <= 5 ?
              player.getShipType() : 1;
      Image playerBaseSprite = ShipUtils.loadShipSprite(shipType);
      baseSprites.put(player, playerBaseSprite);

      ImageView playerImage = ShipUtils.createColoredShipImage(playerColor, playerBaseSprite);
      playerSprites.put(player, playerImage);
    }
  }

  public void addPlayersToBoard(StackPane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    playerSprites.values().forEach(boardPane.getChildren()::add);
  }

  public void positionPlayerAtTile(Player player, Tile tile) {
    ImageView sprite = playerSprites.get(player);
    if (sprite == null) return;
    double targetX = BoardUtils.getBoardOffsetX(board, tile);
    double targetY = BoardUtils.getBoardOffsetY(board, tile);
    double targetRotation = BoardUtils.getRotationForTile(board, tile);
    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
    sprite.setRotate(targetRotation);
  }

  public ImageView getPlayerSprite(Player player) {
    return playerSprites.get(player);
  }

  public Image getPlayerImage(Player player) {
    if (player != null && playerSprites.containsKey(player)) {
      return playerSprites.get(player).getImage();
    }
    return playerSprites.values().iterator().next().getImage();
  }
}