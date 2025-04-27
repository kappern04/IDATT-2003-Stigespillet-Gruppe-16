package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerView {

  public static final int SPRITE_SIZE = 32;
  public static final int TILE_SIZE = 70;
  public static final int TILE_CENTER_OFFSET = 35;

  private final Board board;
  private final Map<Player, ImageView> playerSprites;

  public PlayerView(Board board, Player[] players) {
    this.board = Objects.requireNonNull(board, "Board cannot be null");
    this.playerSprites = new HashMap<>();
    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) throw new IllegalArgumentException("Player at index " + i + " is null");
      ImageView playerImage = createPlayerImage("/image/player/Player_" + (i + 1) + ".png");
      playerSprites.put(players[i], playerImage);
    }
  }

  protected ImageView createPlayerImage(String imagePath) {
    try {
      Image image = new Image(Objects.requireNonNull(
              getClass().getResourceAsStream(imagePath),
              "Could not load image: " + imagePath));
      ImageView imageView = new ImageView(image);
      imageView.setFitWidth(SPRITE_SIZE);
      imageView.setFitHeight(SPRITE_SIZE);
      return imageView;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load player image: " + imagePath, e);
    }
  }

  public void addPlayersToBoard(StackPane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    playerSprites.values().forEach(boardPane.getChildren()::add);
  }

  public void positionPlayerAtTile(Player player, Tile tile) {
    ImageView sprite = playerSprites.get(player);
    if (sprite == null) return;
    double targetX = getBoardOffsetX(tile);
    double targetY = getBoardOffsetY(tile);
    double targetRotation = getRotationForTile(tile);
    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
    sprite.setRotate(targetRotation);
  }

  public ImageView getPlayerSprite(Player player) {
    return playerSprites.get(player);
  }

  private double getBoardOffsetX(Tile tile) {
    int xDimension = board.getX_dimension();
    return tile.getX() * TILE_SIZE + TILE_SIZE - (xDimension + 1) * TILE_CENTER_OFFSET;
  }

  private double getBoardOffsetY(Tile tile) {
    int yDimension = board.getY_dimension();
    return tile.getY() * TILE_SIZE + TILE_SIZE - (yDimension + 1) * TILE_CENTER_OFFSET;
  }

  private double getRotationForTile(Tile tile) {
    int currentIndex = tile.getIndex();
    if (currentIndex >= board.getTiles().size() - 1) return 0;
    Tile nextTile = board.getTiles().get(currentIndex + 1);
    int dx = nextTile.getX() - tile.getX();
    int dy = nextTile.getY() - tile.getY();
    if (dx > 0) return 90;
    if (dx < 0) return -90;
    if (dy > 0) return 180;
    return 0;
  }

  public Image getPlayerImage(Player player) {
    if (player != null && playerSprites.containsKey(player)) {
      return playerSprites.get(player).getImage();
    }
    return playerSprites.values().iterator().next().getImage();
  }
}