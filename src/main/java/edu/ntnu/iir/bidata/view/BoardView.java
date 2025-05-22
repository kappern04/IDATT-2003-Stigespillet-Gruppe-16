package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.controller.PointBoardGame;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Tile;

import java.util.Arrays;
import java.util.List;

import edu.ntnu.iir.bidata.view.elements.CSS;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class BoardView   {

  private BoardGame boardGame;
  private TileView tileView;
  private LadderView ladderView;
  private DieView dieView;
  private PlayerView playerView;
  private PointItemView pointItemView;

  public BoardView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.tileView = new TileView(boardGame.getBoard());
    this.ladderView = new LadderView(boardGame.getBoard());
    this.playerView = new PlayerView(boardGame.getBoard(), boardGame.getPlayers());
    this.dieView = new DieView(boardGame.getDie());
    boardGame.getDie().addObserver(this.dieView);
    Arrays.stream(boardGame.getPlayers()).forEach(p -> p.addObserver(this.playerView));
    if (boardGame instanceof PointBoardGame) {
      this.pointItemView = new PointItemView((PointBoardGame) boardGame);
      this.boardGame.addObserver(this.pointItemView);
    }
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

    // Add player sprites to the board
    playerView.addPlayersToBoard(boardPane);

    if (pointItemView != null) {
      pointItemView.addToBoard(boardPane);
    }

    // Main horizontal layout
    HBox mainLayout = new HBox(20);
    mainLayout.setAlignment(Pos.CENTER);

    // Add board on left and controls on right
    mainLayout.getChildren().addAll(boardPane, createControlPanel());

    return new StackPane(mainLayout);
  }

  public VBox createControlPanel() {
    // Right side VBox for player sprites and die
    VBox controlPanel = new VBox(20);
    controlPanel.setAlignment(Pos.CENTER);
    controlPanel.setMinWidth(200);

    Player[] players = boardGame.getPlayers();

    // Add first half of players
    for (int i = 0; i < players.length / 2; i++) {
      controlPanel.getChildren().add(createPlayerBox(players[i]));
    }

    // Add die in the middle
    HBox dieBox = new HBox(10);
    Button dieButton = dieView.createDieButton(() -> boardGame.playTurn(dieView, playerView));
    dieBox.getChildren().add(dieButton);
    dieBox.setAlignment(Pos.CENTER);
    controlPanel.getChildren().add(dieBox);

    // Add second half of players
    for (int i = players.length / 2; i < players.length; i++) {
      controlPanel.getChildren().add(createPlayerBox(players[i]));
    }

    return controlPanel;
  }

  private VBox createPlayerBox(Player player) {
    VBox playerBox = new VBox(5);
    playerBox.setAlignment(Pos.CENTER);

    Label nameLabel = new Label(player.getName());
    nameLabel.getStyleClass().add("player-name");
    nameLabel.setTextFill(getPlayerColor(player));
    nameLabel.setFont(new CSS().getOrbitronFont(20, FontWeight.BOLD));
    nameLabel.setStyle("-fx-background-color: BLACK; -fx-background-radius:50px;");

    ImageView playerSprite = new ImageView(playerView.getPlayerImage(player));
    playerSprite.setFitWidth(80);
    playerSprite.setFitHeight(80);

    playerBox.getChildren().addAll(nameLabel, playerSprite);
    return playerBox;
  }

  public Color getPlayerColor(Player player) {
    CSS css = new CSS();
    Player[] players = boardGame.getPlayers();

    // Find player index to determine which color to use
    for (int i = 0; i < players.length; i++) {
      if (players[i] == player) {
        switch (i) {
          case 0: return css.PLAYER_1_COLOR;
          case 1: return css.PLAYER_2_COLOR;
          case 2: return css.PLAYER_3_COLOR;
          case 3: return css.PLAYER_4_COLOR;
          default: return Color.WHITE;
        }
      }
    }
    return Color.WHITE;
  }

}