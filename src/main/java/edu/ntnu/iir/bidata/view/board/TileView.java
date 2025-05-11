package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileView {
  private Board board;

  public TileView(Board board) {
    this.board = board;
  }

  public StackPane createTile(int tileIndex) {
    StackPane stackPane = new StackPane();
    Rectangle rect = new Rectangle(64, 64);

    // Modified visual enhancements that won't break ladder placement
    rect.setStroke(Color.rgb(254, 241, 0));
    rect.setFill(Color.TRANSPARENT); // Reverting to transparent background
    rect.setStrokeWidth(2); // Keep original stroke width

    // Remove rounded corners completely
    rect.setArcWidth(0);
    rect.setArcHeight(0);

    // Use a very subtle effect that won't interfere with ladders
    rect.setEffect(new javafx.scene.effect.Glow(1));

    Label tileLabel = new Label(Integer.toString(tileIndex));
    tileLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-padding: 3px 5px;");

    stackPane.getChildren().addAll(rect, tileLabel);
    stackPane.setAlignment(Pos.TOP_LEFT); // Keep critical alignment

    return stackPane;
  }

  public void colorTile(int tileIndex, Rectangle rect) {
    if (tileIndex == board.getLastTile()) {
      {rect.setStroke(Color.rgb(109, 208, 247));}
    }
  }

  public void colorDestinationTile(int tileIndex, Rectangle rect) {
    for (Tile destinationT : board.getTiles()) {
      if (destinationT.getTileAction() instanceof LadderAction action
              && action.getDestinationTileIndex() == tileIndex) {
        if (tileIndex > destinationT.getIndex()) {
          rect.setStroke(Color.rgb(166, 206, 58));
        } else {
          rect.setStroke(Color.rgb(250, 165, 25));
        }
        return;
      }
    }
  }

  public void colorActionTile(int tileIndex, Rectangle rect) {
    for (Tile actionT : board.getTiles()) {
      if (actionT.getTileAction() instanceof LadderAction action
              && actionT.getIndex() == tileIndex) {
        if (action.getDestinationTileIndex() > tileIndex) {
          rect.setStroke(Color.rgb(15, 177, 77));
        } else {
          rect.setStroke(Color.rgb(239, 28, 38));
        }
      }
    }
  }
}