package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.board.LadderController;
import edu.ntnu.iir.bidata.controller.board.PlayerController;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Responsible for rendering the game board, including tiles, ladders, and players.
 */
public class BoardView {
    private final BoardController boardController;
    private final TileView tileView;
    private final LadderView ladderView;
    private final LadderController ladderController;
    private final PlayerController playerController;
    private static final Map<String, String> BOARD_BACKGROUNDS = new HashMap<>();
    private static final String DEFAULT_BACKGROUND = "/image/background/background_1.png";
    private final CSS css;

    static {
        BOARD_BACKGROUNDS.put("Spiral Way", "/image/background/background_1.png");
        BOARD_BACKGROUNDS.put("Ladderia Prime", "/image/background/background_2.png");
        BOARD_BACKGROUNDS.put("ZigZag Heights", "/image/background/background_3.png");
    }

    public BoardView(BoardController boardController) {
        this.boardController = boardController;
        this.tileView = new TileView(boardController.getBoard());
        this.ladderView = new LadderView();
        this.ladderController = new LadderController(boardController.getBoard());
        this.css = new CSS();

        this.playerController = new PlayerController(
                boardController.getBoard(),
                boardController.getPlayers()
        );
        boardController.registerPlayerObserver(this.playerController);
    }

    /**
     * Creates and returns the main board panel with tiles, ladders, and players.
     * @return StackPane containing the board UI
     */
    public StackPane createBoardPanel() {
        GridPane gridPane = new GridPane();
        Pane ladderPane = new Pane();
        StackPane boardPane = new StackPane();

        List<Tile> tiles = boardController.getTiles();
        Map<Integer, Node> tileNodeMap = new HashMap<>();

        int minX = tiles.stream().mapToInt(Tile::getX).min().orElse(0);
        int minY = tiles.stream().mapToInt(Tile::getY).min().orElse(0);

        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            int tileIndex = tile.getIndex();
            StackPane tilePane = tileView.createTile(tileIndex);

            if (!tilePane.getChildren().isEmpty() && tilePane.getChildren().getFirst() instanceof Rectangle rect) {
                tileView.colorTile(tileIndex, rect);
                tileView.colorDestinationTile(tileIndex, rect);
                tileView.colorActionTile(tileIndex, rect);
            }

            int normalizedX = tile.getX() - minX;
            int normalizedY = tile.getY() - minY;
            gridPane.add(tilePane, normalizedX, normalizedY);
            tileNodeMap.put(tileIndex, tilePane);
        }

        boardController.getBoard().setTileNodeMap(tileNodeMap);

        ladderController.addLaddersToBoard(ladderPane, tileNodeMap);

        gridPane.setHgap(4);
        gridPane.setVgap(4);

        boardPane.getChildren().addAll(gridPane, ladderPane);
        playerController.addPlayersToBoard(boardPane);
        return boardPane;
    }

    /**
     * Returns the background for the given board name.
     * @param boardName the name of the board
     * @return the background image
     */
    public Background getBackgroundForBoard(String boardName) {
        String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(boardName, DEFAULT_BACKGROUND);
        return css.createSpaceBackground(backgroundPath);
    }
}