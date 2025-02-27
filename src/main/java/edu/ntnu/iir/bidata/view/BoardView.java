package edu.ntnu.iir.bidata.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import edu.ntnu.iir.bidata.object.Board;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardView {
  private Board board;
  private TileView tileView;

  public BoardView(Board board) {
    this.board = board;
    this.tileView = new TileView(board);
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    int numRows = (int) Math.ceil(board.getTiles().length / 9.0);
    for (int i = 1; i < board.getTiles().length; i++) { // Start from 1 to exclude tile 0
      int tileNumber = board.getTiles()[i].getTileNumber();
      StackPane tilePane = tileView.createTile(tileNumber);
      Rectangle rect = (Rectangle) tilePane.getChildren().get(0);

      tileView.colorTile(tileNumber, rect);
      tileView.colorDestinationTile(tileNumber, rect);
      tileView.colorActionTile(tileNumber, rect);

      int row = (i - 1) / 9;
      int col = (i - 1) % 9;
      if (row % 2 == 1) {
        col = 8 - col; // Reverse column order for odd rows
      }
      gridPane.add(tilePane, col, numRows - 1 - row); // Adjust position to account for skipping tile 0
    }

    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(outline(), gridPane);

    gridPane.setAlignment(Pos.CENTER);
    stackPane.setAlignment(Pos.CENTER);

    return stackPane;
  }


  public StackPane outline() {
    StackPane stackPane = new StackPane();
    Rectangle outline = new Rectangle(500, 500);
    outline.setFill(Color.rgb(0, 174, 240));

    stackPane.getChildren().add(outline);
    return stackPane;
  }
}