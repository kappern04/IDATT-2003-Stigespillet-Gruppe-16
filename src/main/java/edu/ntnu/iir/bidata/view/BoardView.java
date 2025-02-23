package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.LadderAction;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private Label coordinatesLabel;

  public BoardView(Board board) {
    this.board = board;
    this.tileView = new TileView(board);
    this.ladderview = new LadderView();
    this.coordinatesLabel = new Label();
    this.coordinatesLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
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

    gridPane.alignmentProperty().set(javafx.geometry.Pos.CENTER);

    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(outline(), gridPane, coordinatesLabel);

    // Add mouse moved event handler to update coordinates
    stackPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
      double mouseX = event.getX();
      double mouseY = event.getY();
      coordinatesLabel.setText("Col: " + mouseX + ", Row: " + mouseY);
    });

    // Set the alignment of the coordinatesLabel to the top-right corner
    StackPane.setAlignment(coordinatesLabel, javafx.geometry.Pos.TOP_RIGHT);

    return stackPane;
  }

    // Create and position the ladder as a free object
    public StackPane createGameBoardWithLadder() {
      StackPane gameBoard = createGameBoard();

      // Create and position the ladder as a free object
      GridPane ladder = ladderview.createLadder("/image/ladder.png", 10, 10, 4, 4);
      gameBoard.getChildren().add(ladder);

      return gameBoard;
    }

  public StackPane outline() {
    StackPane stackPane = new StackPane();
    Rectangle outline = new Rectangle(500, 500);
    outline.setFill(Color.rgb(0, 174, 240));

    stackPane.getChildren().add(outline);
    return stackPane;
  }
}