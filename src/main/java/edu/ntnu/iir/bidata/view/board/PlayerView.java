package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.BoardUtils;
import edu.ntnu.iir.bidata.view.util.ShipUtils;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Handles the visual representation of players on the board.
 */
public class PlayerView {
  private final Board board;
  private final Map<Player, ImageView> playerSprites = new HashMap<>();
  private final Map<Player, Image> baseSprites = new HashMap<>();

  /**
   * Constructs a PlayerView for the given board and players.
   * @param board the game board
   * @param players the list of players
   */
  public PlayerView(Board board, List<Player> players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    Objects.requireNonNull(players, "Players list cannot be null");

    Color[] defaultColors = {
            Color.RED, Color.BLUE, Color.PURPLE, Color.ORANGE,
            Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE
    };

    for (int i = 0; i < players.size(); i++) {
      final int index = i;
      Player player = Objects.requireNonNull(players.get(i), "Player at index " + i + " is null");
      Color playerColor = player.getColor();
      if (playerColor == null) {
        Color c = defaultColors[index % defaultColors.length];
        player.setColor(c);
        playerColor = c;
      }

      int shipType = (player.getShipType() > 0 && player.getShipType() <= 5) ? player.getShipType() : 1;
      Image baseSprite = ShipUtils.loadShipSprite(shipType);
      baseSprites.put(player, baseSprite);

      ImageView playerImage = ShipUtils.createColoredShipImage(playerColor, baseSprite);
      playerSprites.put(player, playerImage);
    }
  }

  /**
   * Adds all player sprites to the given board pane.
   * @param boardPane the board pane
   */
  public void addPlayersToBoard(StackPane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    playerSprites.values().forEach(boardPane.getChildren()::add);
  }

  /**
   * Positions the given player's sprite at the specified tile.
   * @param player the player
   * @param tile the tile
   */
  public void positionPlayerAtTile(Player player, Tile tile) {
    ImageView sprite = playerSprites.get(player);
    if (sprite == null || tile == null) return;
    double targetX = BoardUtils.getBoardOffsetX(board, tile);
    double targetY = BoardUtils.getBoardOffsetY(board, tile);
    double targetRotation = BoardUtils.getRotationForTile(board, tile);
    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
    sprite.setRotate(targetRotation);
  }

  /**
   * Gets the sprite for the given player.
   * @param player the player
   * @return the player's sprite, or null if not found
   */
  public ImageView getPlayerSprite(Player player) {
    return playerSprites.get(player);
  }

  /**
   * Gets the image for the given player.
   * @param player the player
   * @return the player's image, or null if not found
   */
  public Image getPlayerImage(Player player) {
    ImageView sprite = playerSprites.get(player);
    return sprite != null ? sprite.getImage() : null;
  }

  /**
   * Gets an unmodifiable view of the player sprites map.
   * @return unmodifiable map of player to sprite
   */
  public Map<Player, ImageView> getPlayerSprites() {
    return Collections.unmodifiableMap(playerSprites);
  }
}