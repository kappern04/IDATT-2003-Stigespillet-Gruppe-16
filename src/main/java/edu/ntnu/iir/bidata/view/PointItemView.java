package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.PointBoardGame;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.util.Observer;
import java.util.HashSet;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PointItemView implements Observer {
  private static final int SPRITE_SIZE = 32;
  private static final int TILE_SIZE = 70;
  private static final int TILE_CENTER_OFFSET = 35;

  private final PointBoardGame game;
  private final Map<Integer, ImageView> pointSprites;
  private final StackPane boardPane;
  private Image pointSprite;

  public PointItemView(PointBoardGame game) {
    this.game = game;
    this.pointSprites = new HashMap<>();
    this.boardPane = new StackPane();
    loadSprite();
    if (game.getPointItemLocations() != null) {
      initializePointItems();
    }
  }

  private void loadSprite() {
    try {
      this.pointSprite = new Image(
          Objects.requireNonNull(
              getClass().getResourceAsStream("/image/point.png"),
              "Could not load point sprite image"
          )
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to load point sprite", e);
    }
  }

  private void initializePointItems() {
    for (Integer location : game.getPointItemLocations()) {
      addPointSprite(location);
    }
  }

  private void addPointSprite(int tileIndex) {
    ImageView sprite = new ImageView(pointSprite);
    sprite.setFitWidth(SPRITE_SIZE);
    sprite.setFitHeight(SPRITE_SIZE);

    Tile tile = game.getBoard().getTile(tileIndex);
    positionSpriteAtTile(sprite, tile);

    pointSprites.put(tileIndex, sprite);
    boardPane.getChildren().add(sprite);
  }

  private void removePointSprite(int tileIndex) {
    ImageView sprite = pointSprites.remove(tileIndex);
    if (sprite != null) {
      boardPane.getChildren().remove(sprite);
    }
  }

  private void positionSpriteAtTile(ImageView sprite, Tile tile) {
    double targetX = getTileCenterX(tile);
    double targetY = getTileCenterY(tile);

    sprite.setTranslateX(targetX);
    sprite.setTranslateY(targetY);
  }

  private double getTileCenterX(Tile tile) {
    int xDimension = game.getBoard().getX_dimension();
    return tile.getX() * TILE_SIZE + TILE_SIZE - (xDimension + 1) * TILE_CENTER_OFFSET;
  }

  private double getTileCenterY(Tile tile) {
    int yDimension = game.getBoard().getY_dimension();
    return tile.getY() * TILE_SIZE + TILE_SIZE - (yDimension + 1) * TILE_CENTER_OFFSET;
  }

  public void addToBoard(StackPane boardPane) {
    Objects.requireNonNull(boardPane, "Board pane cannot be null");
    this.boardPane.getChildren().clear();
    boardPane.getChildren().add(this.boardPane);
    updatePointItems();
  }

  private void updatePointItems() {
    new HashSet<>(pointSprites.keySet()).forEach(location -> {
      if (!game.getPointItemLocations().contains(location)) {
        removePointSprite(location);
      }
    });

    game.getPointItemLocations().forEach(location -> {
      if (!pointSprites.containsKey(location)) {
        addPointSprite(location);
      }
    });
  }

  @Override
  public void update() {
    System.out.println("Updating point items");
    updatePointItems();
  }
}