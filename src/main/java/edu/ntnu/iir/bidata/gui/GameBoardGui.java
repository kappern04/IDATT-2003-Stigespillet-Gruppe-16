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

  private StackPane createTiles(int tileNummer) {
    StackPane rute = new StackPane();
    Rectangle rect = new Rectangle(30, 30);

    handleDestinationTile(tileNummer, rect);
    handleActionTile(tileNummer, rect);
    handleTile(tileNummer, rect);

    Label ruteLabel = new Label(Integer.toString(tileNummer));
    rute.getChildren().addAll(rect, ruteLabel);
    return rute;
  }

  private void handleTile(int tileNummer, Rectangle rect) {
    if (rect.getFill() == Color.BLACK) {
      rect.setFill(Color.ORANGE);
    }
  }

  private void handleDestinationTile(int tileNummer, Rectangle rect) {
    for (Tile destinationT : board.getTiles()) {
      if (destinationT.getTileAction() instanceof LadderAction) {
        LadderAction action = (LadderAction) destinationT.getTileAction();
        if (action.getDestinationTile() == tileNummer) {
          if (tileNummer > destinationT.getTileNumber()) {
            rect.setFill(Color.LIGHTGREEN);
          } else {
            rect.setFill(Color.LIGHTCORAL);
          }
          return;
        }
      }
    }
  }

  private void handleActionTile(int tileNummer, Rectangle rect) {
    Tile actionT = board.getTiles()[tileNummer];
    if (actionT.getTileAction() instanceof LadderAction) {
      LadderAction action = (LadderAction) actionT.getTileAction();
      if (action.getDestinationTile() > tileNummer) {
        rect.setFill(Color.GREEN);
      } else {
        rect.setFill(Color.RED);
      }
    }
  }

  public GridPane createGameBoard() {
    GridPane gridPane = new GridPane();
    for (int i = 1; i < board.getTiles().length; i++) { // Start from 1 to exclude tile 0
      StackPane tile = createTiles(i);
      gridPane.add(tile, (i - 1) % 10, (i - 1) / 10); // Adjust position to account for skipping tile 0
    }
    return gridPane;
  }
}