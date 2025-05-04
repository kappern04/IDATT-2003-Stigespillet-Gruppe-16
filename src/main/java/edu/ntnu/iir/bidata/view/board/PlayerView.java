package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;
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
  public static final int SPRITE_SIZE = 32;
  public static final int TILE_SIZE = 70;
  public static final int TILE_CENTER_OFFSET = 35;
  private static final String SHIP_SPRITE_PATH = "/image/player/Ship_%d.png";

  private final Board board;
  private final Map<Player, ImageView> playerSprites;
  private final Map<Player, Image> baseSprites; // Store base sprites for each player

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
      Image playerBaseSprite = loadShipSprite(shipType);
      baseSprites.put(player, playerBaseSprite);

      ImageView playerImage = createColoredPlayerImage(playerColor, playerBaseSprite);
      playerSprites.put(player, playerImage);
    }
  }

  private Image loadShipSprite(int shipType) {
    try {
      String path = String.format(SHIP_SPRITE_PATH, shipType);
      return new Image(Objects.requireNonNull(
              getClass().getResourceAsStream(path),
              "Could not load ship sprite"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to load ship sprite image", e);
    }
  }

  protected ImageView createColoredPlayerImage(Color targetColor, Image baseSprite) {
    // Create a writable copy of the base sprite
    int width = (int)baseSprite.getWidth();
    int height = (int)baseSprite.getHeight();
    WritableImage coloredImage = new WritableImage(width, height);
    PixelReader reader = baseSprite.getPixelReader();
    PixelWriter writer = coloredImage.getPixelWriter();

    // Get target hue from player color
    double targetHue = targetColor.getHue();

    // Process each pixel
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Color pixel = reader.getColor(x, y);

        // Keep transparent pixels unchanged
        if (pixel.getOpacity() < 0.1) {
          writer.setColor(x, y, pixel);
          continue;
        }

        // Only recolor non-black, non-white pixels (skip grayscale elements)
        if (pixel.getSaturation() > 0.15) {
          // Replace hue while keeping saturation and brightness
          Color newColor = Color.hsb(
                  targetHue,
                  pixel.getSaturation(),
                  pixel.getBrightness(),
                  pixel.getOpacity()
          );
          writer.setColor(x, y, newColor);
        } else {
          // Keep grayscale pixels unchanged
          writer.setColor(x, y, pixel);
        }
      }
    }

    // Create and return the ImageView with the new colored image
    ImageView imageView = new ImageView(coloredImage);
    imageView.setFitWidth(SPRITE_SIZE);
    imageView.setFitHeight(SPRITE_SIZE);

    // Add a subtle glow effect with the player's color
    DropShadow glow = new DropShadow();
    glow.setColor(targetColor);
    glow.setRadius(15);
    glow.setSpread(0.3);
    imageView.setEffect(glow);

    return imageView;
  }

  // Keep original method for backwards compatibility
  protected ImageView createColoredPlayerImage(Color targetColor) {
    // Find the player with this color
    Player player = playerSprites.keySet().stream()
            .filter(p -> p.getColor().equals(targetColor))
            .findFirst()
            .orElse(null);

    Image sprite = player != null ? baseSprites.get(player) : loadShipSprite(1);
    return createColoredPlayerImage(targetColor, sprite);
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