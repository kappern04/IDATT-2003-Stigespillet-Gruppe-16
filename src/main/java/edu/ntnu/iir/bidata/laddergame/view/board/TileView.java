package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TileView {
  private final Board board;
  private static final int TILE_SIZE = 64;

  public TileView(Board board) {
    this.board = board;
  }

  public StackPane createTile(int tileIndex) {
    StackPane stackPane = new StackPane();
    stackPane.setPrefSize(TILE_SIZE, TILE_SIZE);
    stackPane.setMinSize(TILE_SIZE, TILE_SIZE);
    stackPane.setMaxSize(TILE_SIZE, TILE_SIZE);
    stackPane.getStyleClass().add("tile-stack");

    // Tile number
    Label tileLabel = new Label(Integer.toString(tileIndex));
    tileLabel.getStyleClass().add("tile-label");

    // Position label in top-left corner
    StackPane.setAlignment(tileLabel, javafx.geometry.Pos.TOP_LEFT);
    tileLabel.setPadding(new javafx.geometry.Insets(3, 0, 0, 5));

    stackPane.getChildren().add(tileLabel);

    // Apply appropriate styling based on tile type
    updateTileStyle(tileIndex, stackPane);

    return stackPane;
  }

  /**
   * Applies the appropriate style classes to a tile based on its function
   */
  public void updateTileStyle(int tileIndex, StackPane stackPane) {
    // Remove all special styling first
    stackPane.getStyleClass().removeAll(
            "tile-default", "tile-goal",
            "tile-ladder-destination-up", "tile-ladder-destination-down",
            "tile-ladder-action-up", "tile-ladder-action-down"
    );

    // Apply default style as base
    stackPane.getStyleClass().add("tile-default");

    // Check if this is the goal tile
    if (tileIndex == board.getLastTile()) {
      stackPane.getStyleClass().add("tile-goal");
      return; // Goal styling takes precedence
    }

    // Simplified ladder checks
    for (Tile tile : board.getTiles()) {
      if (tile.getTileAction() instanceof LadderAction action) {
        // Check if this is a ladder destination
        if (action.getDestinationTileIndex() == tileIndex) {
          String styleClass = tileIndex > tile.getIndex() ?
                  "tile-ladder-destination-up" : "tile-ladder-destination-down";
          stackPane.getStyleClass().add(styleClass);
          break;
        }

        // Check if this is a ladder action tile
        if (tile.getIndex() == tileIndex) {
          String styleClass = action.getDestinationTileIndex() > tileIndex ?
                  "tile-ladder-action-up" : "tile-ladder-action-down";
          stackPane.getStyleClass().add(styleClass);
          break;
        }
      }
    }
  }
}