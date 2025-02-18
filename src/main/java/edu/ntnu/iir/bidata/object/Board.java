package edu.ntnu.iir.bidata.object;

/**
 * The board class represents the game board a 10*9 grid with 90 tiles. The board is responsible for
 * creating the tiles and setting up the game.
 */
public class Board {
  private Tile[] tiles;

  public Board() {
    tiles = new Tile[101];
    createTiles();
  }

  private void createTiles() {
    for (int i = 0; i < 101; i++) {
      tiles[i] = new Tile(i, new TileAction());
    }

    // Ladders
    tiles[2].setTileAction(new LadderSnakesRocketAction(22));
    tiles[20].setTileAction(new LadderSnakesRocketAction(41));
    tiles[26].setTileAction(new LadderSnakesRocketAction(55));
    tiles[36].setTileAction(new LadderSnakesRocketAction(57));
    tiles[52].setTileAction(new LadderSnakesRocketAction(72));
    tiles[71].setTileAction(new LadderSnakesRocketAction(91));
    // Snakes
    tiles[43].setTileAction(new LadderSnakesRocketAction(17));
    tiles[49].setTileAction(new LadderSnakesRocketAction(33));
    tiles[56].setTileAction(new LadderSnakesRocketAction(38));
    tiles[73].setTileAction(new LadderSnakesRocketAction(12));
    tiles[84].setTileAction(new LadderSnakesRocketAction(58));
    tiles[98].setTileAction(new LadderSnakesRocketAction(78));
    // Rocket Paths
    tiles[26].setTileAction(new LadderSnakesRocketAction(86));
    tiles[48].setTileAction(new LadderSnakesRocketAction(98));

  }
  public Tile[] getTiles() {
    return tiles;
  }
}
