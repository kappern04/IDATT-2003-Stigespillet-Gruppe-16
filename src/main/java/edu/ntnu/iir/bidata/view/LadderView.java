package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class LadderView {

  private Board board;

  public LadderView(Board board) {
    this.board = board;
  }

  public Node createLadder(int actionTile, int destinationTile ) {
    Tile startTile = board.getTiles()[actionTile];
    Tile endTile = board.getTiles()[destinationTile];

    double startX = getTileCenterX(startTile);
    double startY = getTileCenterY(startTile);
    double endX = getTileCenterX(endTile);
    double endY = getTileCenterY(endTile);

    Line ladder = new Line(startX, startY, endX, endY);
    ladder.setStrokeWidth(5);
    ladder.setStroke(Color.BROWN);

    return ladder;
  }

  private double getTileCenterX(Tile tile) {
    int col = (tile.getTileNumber() - 1) % 9;
    if (((tile.getTileNumber() - 1) / 9) % 2 == 1) {
      col = 8 - col;
    }
    return col * 64 + 32;
  }

  private double getTileCenterY(Tile tile) {
    int row = (tile.getTileNumber() - 1) / 9;
    return (7 - row) * 64 + 32;
  }
}