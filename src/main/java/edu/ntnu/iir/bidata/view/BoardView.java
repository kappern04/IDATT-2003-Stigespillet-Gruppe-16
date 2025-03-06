package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardView {
  private BoardGame boardGame;
  private TileView tileView;
  private LadderView ladderView;

  public BoardView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.tileView = new TileView(boardGame.getBoard());
    this.ladderView = new LadderView(boardGame.getBoard());
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    int numRows = (int) Math.ceil(boardGame.getBoard().getTiles().length / 9.0);

    for (int i = 1; i < boardGame.getBoard().getTiles().length; i++) { // Start from 1 to exclude tile 0
      int tileNumber = boardGame.getBoard().getTiles()[i].getTileNumber();
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
    gridPane.setHgap(4);
    gridPane.setVgap(4);

    gridPane.setAlignment(Pos.CENTER);

    // Create the StackPane for the board and outline
    StackPane boardPane = new StackPane();
    boardPane.getChildren().addAll(gridPane,
            //BLUE
            ladderView.createLadder("Blue", 1, 40),
            ladderView.createLadder("Blue", 8, 10),
            ladderView.createLadder("Blue", 36, 52),
            ladderView.createLadder("Blue", 43, 62),
            ladderView.createLadder("Blue", 49, 79),
            ladderView.createLadder("Blue", 65, 82),
            ladderView.createLadder("Blue", 68, 85),
            //RED
            ladderView.createLadder("Red", 24, 5),
            ladderView.createLadder("Red", 33, 3),
            ladderView.createLadder("Red", 42, 30),
            ladderView.createLadder("Red", 56, 37),
            ladderView.createLadder("Red", 64, 27),
            ladderView.createLadder("Red", 74, 12),
            ladderView.createLadder("Red", 87, 70));

// Create a VBox to hold the game board and die button
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.getChildren().addAll(boardPane);

// Create the VBox for the die button
    VBox dieBox = new VBox(10);
    dieBox.getChildren().add(boardGame.getDieView().createDieButton());
    dieBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(dieBox);

// Return the final layout
    return new StackPane(layout);

  }

//  public StackPane outline() {
//    StackPane stackPane = new StackPane();
//    Rectangle outline = new Rectangle(640, 704);
//    outline.setFill(Color.rgb(0, 0, 0));
//
//    stackPane.getChildren().add(outline);
//    return stackPane;
//  }
}