package edu.ntnu.iir.bidata.view;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Tile;

public class TileView {
  private Board board;

  public TileView(Board board) {
    this.board = board;
  }

  public StackPane createTile(int tileNumber) {
    StackPane stackPane = new StackPane();
    Rectangle rect = new Rectangle(64, 64);
    rect.setFill(Color.rgb(254, 241, 0));

    Label tileLabel = new Label(Integer.toString(tileNumber));
    stackPane.getChildren().addAll(rect, tileLabel);
    return stackPane;
  }

  public void colorTile(int tileNumber, Rectangle rect) {
      if (tileNumber == 90) {
        rect.setFill(Color.rgb(109, 208, 247));
      }
    }

  public void colorDestinationTile(int tileNumber, Rectangle rect) {
    for (Tile destinationT : board.getTiles()) {
      if (destinationT.getTileAction() instanceof LadderAction action
          && action.getDestinationTile() == tileNumber) {
        if (tileNumber > destinationT.getTileNumber()) {
          rect.setFill(Color.rgb(166, 206, 58));
        } else {
          rect.setFill(Color.rgb(250, 165, 25));
        }
        return;
      }
    }
  }

  public void colorActionTile(int tileNumber, Rectangle rect) {
    for (Tile actionT : board.getTiles()) {
      if (actionT.getTileAction() instanceof LadderAction action
          && actionT.getTileNumber() == tileNumber) {
        if (action.getDestinationTile() > tileNumber) {
          rect.setFill(Color.rgb(15, 177, 77));
        } else {
          rect.setFill(Color.rgb(239, 28, 38));
        }
      }
    }
  }
}