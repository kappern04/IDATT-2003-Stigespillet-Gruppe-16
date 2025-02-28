package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import edu.ntnu.iir.bidata.object.Board;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardView {
  private BoardGame boardGame;
  private TileView tileView;

  public BoardView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.tileView = new TileView(boardGame.getBoard());
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

    // Create a VBox to hold the die button (from BoardGame)
    VBox dieBox = new VBox(10); // Add spacing
    dieBox.getChildren().add(boardGame.getDieView().createDieButton());
    dieBox.setAlignment(Pos.CENTER);

    // Wrap gridPane and dieBox in another VBox for proper layout
    VBox boardContainer = new VBox(20, gridPane, dieBox);
    boardContainer.setAlignment(Pos.CENTER);

    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(outline(), boardContainer);

    return stackPane;
  }

  public StackPane outline() {
    StackPane stackPane = new StackPane();
    Rectangle outline = new Rectangle(640, 704);
    outline.setFill(Color.rgb(0, 174, 240));

    stackPane.getChildren().add(outline);
    return stackPane;
  }
}
