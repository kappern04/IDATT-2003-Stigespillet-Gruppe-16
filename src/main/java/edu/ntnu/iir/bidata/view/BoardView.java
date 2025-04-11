package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.LadderAction;
import edu.ntnu.iir.bidata.object.Tile;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public class BoardView {

  private BoardGame boardGame;
  private TileView tileView;
  private LadderView ladderView;
  private DieView dieView;
  private PlayerView playerView;

  public BoardView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.tileView = new TileView(boardGame.getBoard());
    this.ladderView = new LadderView(boardGame.getBoard());
    this.playerView = new PlayerView(boardGame.getBoard(), boardGame.getPlayers());
    this.dieView = new DieView(boardGame.getDie());
    boardGame.getDie().addObserver(this.dieView);
    Arrays.stream(boardGame.getPlayers()).forEach(p -> p.addObserver(this.playerView));
  }

  public StackPane createGameBoard() {
    GridPane gridPane = new GridPane();
    StackPane boardPane = new StackPane();

    List<Tile> tiles = boardGame.getBoard().getTiles();

    for (Tile tile : tiles) {
      // Skip tile 0
      if (tile.getIndex() == 0) {
        continue;
      }
      // creates tiles
      int tileIndex = tile.getIndex();
      StackPane tilePane = tileView.createTile(tileIndex);
      Rectangle rect = (Rectangle) tilePane.getChildren().getFirst();

      tileView.colorTile(tileIndex, rect);
      tileView.colorDestinationTile(tileIndex, rect);
      tileView.colorActionTile(tileIndex, rect);

      gridPane.add(tilePane, tile.getX(), tile.getY());
      if (tile.getTileAction() instanceof LadderAction) {
        Node ladder = ladderView.createLadder(tile);
        boardPane.getChildren().add(ladder);
      }
    }

    gridPane.setHgap(4);
    gridPane.setVgap(4);
    gridPane.setAlignment(Pos.CENTER);
    boardPane.getChildren().addAll(gridPane);

    HBox layout = new HBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.getChildren().addAll(boardPane);

    VBox playerInfo = new VBox(10);

    HBox dieBox = new HBox(10);
    // Pass both dieView and playerView to playTurn method to handle sequencing
    Button dieButton = dieView.createDieButton(() -> boardGame.playTurn(dieView, playerView));

    playerView.addPlayersToBoard(boardPane);

    dieBox.getChildren().add(dieButton);
    dieBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(dieBox);

    return new StackPane(layout);
  }
}