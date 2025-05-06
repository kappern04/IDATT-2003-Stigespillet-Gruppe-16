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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class BoardView {
    private BoardController boardController;
    private TileView tileView;
    private LadderView ladderView;
    private PlayerController playerController;
    private static final Map<String, String> BOARD_BACKGROUNDS = new HashMap<>();
    private static final String DEFAULT_BACKGROUND = "/image/background/background_1.png";
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
        Pane ladderPane = new Pane();
        StackPane boardPane = new StackPane();

        List<Tile> tiles = boardController.getTiles();
        Map<Integer, Node> tileNodeMap = new HashMap<>();

        // First pass: create tiles and build the map
        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            int tileIndex = tile.getIndex();
            StackPane tilePane = tileView.createTile(tileIndex);
            Rectangle rect = (Rectangle) tilePane.getChildren().getFirst();

            tileView.colorTile(tileIndex, rect);
            tileView.colorDestinationTile(tileIndex, rect);
            tileView.colorActionTile(tileIndex, rect);

            gridPane.add(tilePane, tile.getX(), tile.getY());
            tileNodeMap.put(tileIndex, tilePane);
        }

        // Store the tile nodes in the board for access by other components
        boardController.getBoard().setTileNodeMap(tileNodeMap);

        // Second pass: add ladders
        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            if (boardController.hasLadderAction(tile)) {
                Tile destinationTile = boardController.getDestinationTile(tile);
                Node fromTileNode = tileNodeMap.get(tile.getIndex());
                Node toTileNode = tileNodeMap.get(destinationTile.getIndex());

                Node ladder = ladderView.createLadder(tile, destinationTile, fromTileNode, toTileNode);
                ladderPane.getChildren().add(ladder);
            }
        }

        gridPane.setHgap(4);
        gridPane.setVgap(4);


        boardPane.getChildren().addAll(gridPane, ladderPane);
        playerController.addPlayersToBoard(boardPane);
        return boardPane;
    }

    public Background getBackgroundForBoard(String boardName) {
        String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(boardName, DEFAULT_BACKGROUND);
        return css.createSpaceBackground(backgroundPath);
    }
}