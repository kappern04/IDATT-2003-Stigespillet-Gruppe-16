package edu.ntnu.iir.bidata.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameBoardGui {
  private Board board;

  public GameBoardGui(Board board) {
    this.board = board;
  }

  private StackPane createTiles(int i) {
    StackPane stackPane = new StackPane();

    Rectangle rect = new Rectangle(50, 45);
    rect.setFill(Color.TRANSPARENT); // Make tile transparent
    rect.setStroke(Color.BLACK);

    stackPane.getChildren().add(rect);

    return stackPane;
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    int numRows = (int) Math.ceil(board.getTiles().length / 9.0);

    for (int i = 1; i < board.getTiles().length; i++) {
      StackPane tile = createTiles(i);
      int row = (i - 1) / 9;
      int col = (i - 1) % 9;
      if (row % 2 == 1) {
        col = 8 - col;
      }
      gridPane.add(tile, col, numRows - 1 - row);
    }
    gridPane.alignmentProperty().set(javafx.geometry.Pos.CENTER);

    // Load Background Image
    Image image = new Image(getClass().getResource("/image/stigespill-bilde.jpg").toExternalForm());
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(500); // Set the width to match the gridPane
    imageView.setFitHeight(500); // Set the height to match the gridPane
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
