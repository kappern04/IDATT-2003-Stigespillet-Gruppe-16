package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.BoardUtils;
import edu.ntnu.iir.bidata.laddergame.view.util.ShipUtils;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Handles the visual representation of players on the board.
 */
public class PlayerView {
  private final Board board;
  private final Map<Player, ImageView> playerSprites = new HashMap<>();
  private final Map<Player, Image> baseSprites = new HashMap<>();
  public static final int SPRITE_WIDTH = 32;
  public static final int SPRITE_HEIGHT = 32;

  /**
   * Constructs a PlayerView for the given board and players.
   * @param board the game board
   * @param players the list of players
   */
  public PlayerView(Board board, List<Player> players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    Objects.requireNonNull(players, "Players list cannot be null");

    for (int i = 0; i < players.size(); i++) {
      final int index = i;
      Player player = Objects.requireNonNull(players.get(i), "Player at index " + i + " is null");
      Color playerColor = player.getColor();
      if (playerColor == null) {
        Color[] defaultColors = ShipUtils.getDefaultColors();
        Color c = defaultColors[index % defaultColors.length];
        player.setColor(c);
        playerColor = c;
      }

      int shipType = (player.getShipType() > 0 && player.getShipType() <= 5) ? player.getShipType() : 1;
      Image baseSprite = ShipUtils.loadShipSprite(shipType);
      baseSprites.put(player, baseSprite);

      ImageView playerImage = ShipUtils.createColoredShipImage(playerColor, baseSprite);
      playerImage.setFitWidth(SPRITE_WIDTH);
      playerImage.setFitHeight(SPRITE_HEIGHT);
      playerImage.setPickOnBounds(false);
      playerSprites.put(player, playerImage);
    }
  }

  /**
   * Adds all player sprites to the given board pane.
   * @param boardPane the board pane
   */
  public void addPlayersToBoard(Pane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    playerSprites.values().forEach(boardPane.getChildren()::add);
  }

  /**
   * Positions the given player's sprite at the specified tile.
   * Using node-based positioning via BoardUtils.
   * @param player the player
   * @param tile the tile
   */
  public void positionPlayerAtTile(Player player, Tile tile) {
    ImageView sprite = playerSprites.get(player);
    if (sprite == null || tile == null) return;

    Platform.runLater(() -> {
      // Only set position directly when no animation is running
      if (!sprite.getProperties().containsKey("animating") ||
              !((Boolean)sprite.getProperties().getOrDefault("animating", false))) {
        double targetX = BoardUtils.getBoardOffsetX(board, tile) - SPRITE_WIDTH/2;
        double targetY = BoardUtils.getBoardOffsetY(board, tile) - SPRITE_HEIGHT/2;
        double rotation = BoardUtils.getRotationForTile(board, tile);

        sprite.setTranslateX(targetX);
        sprite.setTranslateY(targetY);
        sprite.setRotate(rotation);

        sprite.setVisible(true);
        sprite.toFront();
        if (sprite.getParent() != null) {
          sprite.getParent().requestLayout();
        }
      }
    });
  }

  /**
   * Prepares a sprite for animation by unbinding properties and marking it as animating.
   * @param sprite the sprite to prepare
   * @param animating whether the sprite is currently being animated
   */
  public void prepareForAnimation(ImageView sprite, boolean animating) {
    if (sprite == null) return;

    Platform.runLater(() -> {
      // Mark sprite as being animated
      sprite.getProperties().put("animating", animating);

      if (animating) {
        // Unbind all properties that might be bound
        if (sprite.layoutXProperty().isBound()) sprite.layoutXProperty().unbind();
        if (sprite.layoutYProperty().isBound()) sprite.layoutYProperty().unbind();
        if (sprite.translateXProperty().isBound()) sprite.translateXProperty().unbind();
        if (sprite.translateYProperty().isBound()) sprite.translateYProperty().unbind();
      }
    });
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
   * Gets the base image for the given player.
   * @param player the player
   * @return the player's base image, or null if not found
   */
  public Image getPlayerImage(Player player) {
    return playerSprites.get(player).getImage();
  }

  /**
   * Gets an unmodifiable view of the player sprites map.
   * @return unmodifiable map of player to sprite
   */
  public Map<Player, ImageView> getPlayerSprites() {
    return Collections.unmodifiableMap(playerSprites);
  }
}