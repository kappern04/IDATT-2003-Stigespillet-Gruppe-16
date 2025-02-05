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

  private StackPane createTile(int tileNummer) {
    StackPane rute = new StackPane();
    Rectangle rect = new Rectangle(30, 30);
    Tile tile = board.getTiles()[tileNummer];
    if (tile.getTileAction() instanceof LadderAction) {
      LadderAction action = (LadderAction) tile.getTileAction();
      if (action.getDestinationTile() > tileNummer) {
        rect.setFill(Color.GREEN);
      } else {
        rect.setFill(Color.RED);
      }
    } else {
      rect.setFill(Color.ORANGE);
    }
    Label ruteLabel = new Label(Integer.toString(tileNummer));
    rute.getChildren().addAll(rect, ruteLabel);
    return rute;
  }

  public GridPane createGameBoard() {
    GridPane gridPane = new GridPane();
    for (int i = 1; i < board.getTiles().length; i++) { // Start from 1 to exclude tile 0
      StackPane tile = createTile(i);
      gridPane.add(tile, (i - 1) % 10, (i - 1) / 10); // Adjust position to account for skipping tile 0
    }
    return gridPane;
  }
}