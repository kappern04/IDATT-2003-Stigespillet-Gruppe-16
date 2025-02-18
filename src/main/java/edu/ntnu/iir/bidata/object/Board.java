package edu.ntnu.iir.bidata.object;

/**
 * The board class represents the game board a 10*9 grid with 90 tiles. The board is responsible for
 * creating the tiles and setting up the game.
 */
public class Board {
  private Tile[] tiles;

  public Board() {
    tiles = new Tile[91];
    createTiles();
  }

  private void createTiles() {
    for (int i = 0; i < 91; i++) {
      tiles[i] = new Tile(i, new TileAction());
    }

    // Define ladder actions
    tiles[1].setTileAction(new LadderAction(40));
    tiles[8].setTileAction(new LadderAction(10));
    tiles[36].setTileAction(new LadderAction(52));
    tiles[43].setTileAction(new LadderAction(62));
    tiles[49].setTileAction(new LadderAction(79));
    tiles[65].setTileAction(new LadderAction(82));
    tiles[68].setTileAction(new LadderAction(85));
    tiles[24].setTileAction(new LadderAction(5));
    tiles[33].setTileAction(new LadderAction(3));
    tiles[42].setTileAction(new LadderAction(30));
    tiles[56].setTileAction(new LadderAction(37));
    tiles[64].setTileAction(new LadderAction(27));
    tiles[74].setTileAction(new LadderAction(12));
    tiles[87].setTileAction(new LadderAction(70));
  }

  public Tile[] getTiles() {
    return tiles;
  }
}
