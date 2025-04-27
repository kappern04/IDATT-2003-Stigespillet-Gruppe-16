package edu.ntnu.iir.bidata.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The board class represents the game board a 10*9 grid with 90 tiles. The board is responsible for
 * creating the tiles and setting up the game.
 */
public class Board {
  private List<Tile> tiles;
  private String name;
  private String description;
  private int x_dimension;
  private int y_dimension;

  //empty constructor creates default board
  public Board() {
    this.name = "Stigespillet 90";
    this.description = "Standard snakes and ladders with 90 (10x9) tiles";
    tiles = new ArrayList<Tile>(91);
    this.x_dimension = 10;
    this.y_dimension = 9;
    createDefaultTiles();
  }

  public Board(String name, String description, List<Tile> tiles) {
    this.name = name;
    this.description = description;
    this.tiles = tiles;
    this.x_dimension = tiles.stream().mapToInt(Tile::getX).max().orElse(0)+1;
    this.y_dimension = tiles.stream().mapToInt(Tile::getY).max().orElse(0)+1;
  }

  public String getBoardName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  private void createDefaultTiles() {
    tiles.addFirst(new Tile(0, -1, -1, new TileAction())); // Position outside the visible board

    // Create tiles 1-90 for the visible board
    for (int i = 1; i <= 90; i++) {
      int y = (90-i) / 9;
      int x = (90-i) % 9;
      if (y % 2 == 1) {
        x = 8 - x;
      } else {
        x = x;
      }
      tiles.add(i, new Tile(i, x, y, new TileAction()));
    }
    tiles.get(1).setTileAction(new LadderAction(40));
    tiles.get(8).setTileAction(new LadderAction(10));
    tiles.get(36).setTileAction(new LadderAction(52));
    tiles.get(43).setTileAction(new LadderAction(62));
    tiles.get(49).setTileAction(new LadderAction(79));
    tiles.get(65).setTileAction(new LadderAction(82));
    tiles.get(68).setTileAction(new LadderAction(85));
    tiles.get(24).setTileAction(new LadderAction(5));
    tiles.get(33).setTileAction(new LadderAction(3));
    tiles.get(42).setTileAction(new LadderAction(30));
    tiles.get(56).setTileAction(new LadderAction(37));
    tiles.get(64).setTileAction(new LadderAction(27));
    tiles.get(74).setTileAction(new LadderAction(12));
    tiles.get(87).setTileAction(new LadderAction(70));
  }

  public List<Tile> getTiles() {
    return tiles;
  }

  public Tile getTile(int index) {
    return tiles.get(index);
  }

  public int getX_dimension() {
    return x_dimension;
  }

  public int getY_dimension() {
    return y_dimension;
  }
}
