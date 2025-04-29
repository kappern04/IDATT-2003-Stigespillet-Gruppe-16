package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.board.PlayerController;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class BoardView {
    private BoardController boardController;
    private TileView tileView;
    private LadderView ladderView;
    private PlayerController playerController;
    private static final Map<String, String> BOARD_BACKGROUNDS = new HashMap<>();
    private static final String DEFAULT_BACKGROUND = "/image/default_background.png";
    private CSS css;
    static {
        BOARD_BACKGROUNDS.put("Spiral Way", "/image/background/background_1.png");
        BOARD_BACKGROUNDS.put("Ladderia Prime", "/image/background/background_2.png");
        BOARD_BACKGROUNDS.put("ZigZag Heights", "/image/background/background_3.png");
    }

    public BoardView(BoardController boardController) {
        this.boardController = boardController;
        this.tileView = new TileView(boardController.getBoard());
        this.ladderView = new LadderView(boardController.getBoard());
        this.css = new CSS();

        // Create PlayerController which implements Observer
        this.playerController = new PlayerController(
                boardController.getBoard(),
                boardController.getPlayersArray()
        );

        // Register the controller as an observer
        boardController.registerPlayerObserver(this.playerController);
    }

    public StackPane createBoardPanel() {
        GridPane gridPane = new GridPane();
        StackPane boardPane = new StackPane();

        List<Tile> tiles = boardController.getTiles();
        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            int tileIndex = tile.getIndex();
            StackPane tilePane = tileView.createTile(tileIndex);
            Rectangle rect = (Rectangle) tilePane.getChildren().getFirst();

            // Apply coloring to the rectangle
            tileView.colorTile(tileIndex, rect);
            tileView.colorDestinationTile(tileIndex, rect);
            tileView.colorActionTile(tileIndex, rect);

            gridPane.add(tilePane, tile.getX(), tile.getY());
            if (boardController.hasLadderAction(tile)) {
                Node ladder = ladderView.createLadder(tile);
                boardPane.getChildren().add(ladder);
            }
        }
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.setAlignment(Pos.CENTER);
        boardPane.getChildren().addAll(gridPane);

        // Use PlayerController to add players to board
        playerController.addPlayersToBoard(boardPane);
        return boardPane;
    }
    public Background getBackgroundForBoard(String boardName) {
        String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(boardName, DEFAULT_BACKGROUND);
        return css.createSpaceBackground(backgroundPath);
    }
}