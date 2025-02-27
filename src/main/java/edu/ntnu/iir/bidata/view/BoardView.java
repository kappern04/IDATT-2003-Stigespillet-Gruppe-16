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
  private LadderView ladderview;

  public BoardView(Board board) {
    this.board = board;
    this.tileView = new TileView(board);
    this.ladderview = new LadderView(board);
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

    // Create and position the ladder as a free object
    public StackPane createGameBoardWithLadder() {
        StackPane gameBoard = createGameBoard();
        gameBoard.getChildren().add(ladderview.createLadder(1, 40));
        gameBoard.getChildren().add(ladderview.createLadder(8, 10));
        gameBoard.getChildren().add(ladderview.createLadder(36, 52));
        gameBoard.getChildren().add(ladderview.createLadder(43, 62));
        gameBoard.getChildren().add(ladderview.createLadder(49, 79));
        gameBoard.getChildren().add(ladderview.createLadder(65, 82));
        gameBoard.getChildren().add(ladderview.createLadder(68, 85));
        gameBoard.getChildren().add(ladderview.createLadder(24, 5));
        gameBoard.getChildren().add(ladderview.createLadder(33, 3));
        gameBoard.getChildren().add(ladderview.createLadder(42, 30));
        gameBoard.getChildren().add(ladderview.createLadder(56, 37));
        gameBoard.getChildren().add(ladderview.createLadder(64, 27));
        gameBoard.getChildren().add(ladderview.createLadder(74, 12));
        gameBoard.getChildren().add(ladderview.createLadder(87, 70));



      return gameBoard;
    }

  public StackPane outline() {
    StackPane stackPane = new StackPane();
    Rectangle outline = new Rectangle(640, 704);
    outline.setFill(Color.rgb(0, 174, 240));

    stackPane.getChildren().add(outline);
    return stackPane;
  }
}