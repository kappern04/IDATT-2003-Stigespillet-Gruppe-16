package edu.ntnu.iir.bidata.gui;

import edu.ntnu.iir.bidata.object.LadderSnakesRocketAction;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import edu.ntnu.iir.bidata.object.Board;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameBoardGui {
  private Board board;

  public GameBoardGui(Board board) {
    this.board = board;
  }

  private StackPane createTiles(int tileNumber) {
    StackPane stackPane = new StackPane();
    Rectangle rect = new Rectangle(80, 80);


    colorDestinationTile(tileNumber, rect);
    colorActionTile(tileNumber, rect);
    colorTile(tileNumber, rect);


    stackPane.getChildren().addAll(rect);
    return stackPane;
  }

  private void colorTile(int tileNumber, Rectangle rect) {
    if (rect.getFill() == Color.BLACK) {
      rect.setFill(Color.rgb(255, 255, 255, 0.3));
      if (tileNumber == 90) {
        rect.setFill(Color.rgb(137, 207, 240, 0.3));
      }
    }
  }

  private void colorDestinationTile(int tileNumber, Rectangle rect) {
    for (Tile destinationT : board.getTiles()) {
      if (destinationT.getTileAction() instanceof LadderSnakesRocketAction action
              && action.getDestinationTile() == tileNumber) {
        if (tileNumber > destinationT.getTileNumber()) {
          rect.setFill(Color.rgb(0, 255, 0, 0.3));
        } else {
          rect.setFill(Color.rgb(255, 0, 0, 0.3));
        }
        return;
      }
    }
  }

  private void colorActionTile(int tileNumber, Rectangle rect) {
    for (Tile actionT : board.getTiles()) {
      if (actionT.getTileAction() instanceof LadderSnakesRocketAction action
              && actionT.getTileNumber() == tileNumber) {
        if (action.getDestinationTile() > tileNumber) {
          rect.setFill(Color.rgb(0, 255, 0, 0.5));
        } else {
          rect.setFill(Color.rgb(255, 0, 0, 0.5));
        }
      }
    }
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    int numRows = 10;
    int numCols = 10;
    int tileSize = 80; // Size of each tile

    for (int i = 1; i <= numRows * numCols; i++) {
      StackPane tile = createTiles(i);
      int row = (i - 1) / numCols;
      int col = (i - 1) % numCols;
      if (row % 2 == 1) {
        col = numCols - 1 - col;
      }
      gridPane.add(tile, col, numRows - 1 - row);
    }
    gridPane.alignmentProperty().set(javafx.geometry.Pos.CENTER);

    // Load Background Image
    Image image = new Image(getClass().getResource("/image/board2.jpg").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(numCols * tileSize);
    imageView.setFitHeight(numRows * tileSize);
    imageView.setPreserveRatio(false); // Disable preserving the aspect ratio

    // Ensure tiles are transparent
    gridPane.setStyle("-fx-background-color: transparent;");

    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(imageView, gridPane);
    StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);
    StackPane.setAlignment(gridPane, javafx.geometry.Pos.CENTER);

    return stackPane;
  }
}
