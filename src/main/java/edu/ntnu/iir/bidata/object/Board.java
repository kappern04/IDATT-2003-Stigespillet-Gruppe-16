package edu.ntnu.iir.bidata.object;

import edu.ntnu.iir.bidata.object.file.BoardFileReader;
import edu.ntnu.iir.bidata.object.file.BoardFileReaderGson;
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

  //empty constructor creates default board
  public Board() {
    this.name = "Stigespillet 90";
    this.description = "Standard snakes and ladders with 90 (10x9) tiles";
    tiles = new ArrayList<Tile>(91);
    createDefaultTiles();
  }

  public Board(String name, String description, List<Tile> tiles) {
    this.name = name;
    this.description = description;
    this.tiles = tiles;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  private void createDefaultTiles() {
    for (int i = 0; i < 91; i++) {
      int y = i / 10;
      int x;
      if (y % 2 == 0) {
        x = i % 10;
      } else {
        x = 9 - (i % 10);
      }
      tiles.set(i, new Tile(i, x, y, new TileAction()));
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
  }

  public List<Tile> getTiles() {
    return tiles;
  }

  public Tile getTile(int index) {
    return tiles.get(index);
  }

}
