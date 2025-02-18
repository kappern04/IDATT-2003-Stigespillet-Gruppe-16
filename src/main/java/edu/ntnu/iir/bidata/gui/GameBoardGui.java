package edu.ntnu.iir.bidata.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Tile;

public class GameBoardGui {
  private Board board;

  public GameBoardGui(Board board) {
    this.board = board;
  }

  private StackPane createTiles(int tileNumber) {
    StackPane stackPane = new StackPane();
    Rectangle rect = new Rectangle(50, 45);
    rect.setStroke(Color.BLACK);

    colorDestinationTile(tileNumber, rect);
    colorActionTile(tileNumber, rect);
    colorTile(tileNumber, rect);

    Label ruteLabel = new Label(Integer.toString(tileNumber));
    stackPane.getChildren().addAll(rect, ruteLabel);
    return stackPane;
  }

  private void colorTile(int tileNumber, Rectangle rect) {
    if (rect.getFill() == Color.BLACK) {
      rect.setFill(Color.ORANGE);
      if (tileNumber == 90) {
        rect.setFill(Color.LIGHTBLUE);
      }
    }
  }

  private void colorDestinationTile(int tileNumber, Rectangle rect) {
    for (Tile destinationT : board.getTiles()) {
      if (destinationT.getTileAction() instanceof LadderAction action
          && action.getDestinationTile() == tileNumber) {
        if (tileNumber > destinationT.getTileNumber()) {
          rect.setFill(Color.LIGHTGREEN);
        } else {
          rect.setFill(Color.LIGHTCORAL);
        }
        return;
      }
    }
  }

  private void colorActionTile(int tileNumber, Rectangle rect) {
    for (Tile actionT : board.getTiles()) {
      if (actionT.getTileAction() instanceof LadderAction action
          && actionT.getTileNumber() == tileNumber) {
        if (action.getDestinationTile() > tileNumber) {
          rect.setFill(Color.GREEN);
        } else {
          rect.setFill(Color.RED);
        }
      }
    }
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    int numRows = (int) Math.ceil(board.getTiles().length / 9.0);
    for (int i = 1; i < board.getTiles().length; i++) { // Start from 1 to exclude tile 0
      StackPane tile = createTiles(i);
      int row = (i - 1) / 9;
      int col = (i - 1) % 9;
      if (row % 2 == 1) {
        col = 8 - col; // Reverse column order for odd rows
      }
      gridPane.add(tile, col, numRows - 1 - row); // Adjust position to account for skipping tile 0
    }
    gridPane.alignmentProperty().set(javafx.geometry.Pos.CENTER);

    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(outline(), gridPane);

    return stackPane;
  }


  public StackPane outline() {
    StackPane stackPane = new StackPane();
    Rectangle outline = new Rectangle(500, 500);
    outline.setFill(Color.LIGHTSKYBLUE);

    stackPane.getChildren().add(outline);
    return stackPane;
  }
}
