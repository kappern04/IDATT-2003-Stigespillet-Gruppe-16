package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.board.LadderController;
import edu.ntnu.iir.bidata.controller.board.PlayerController;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.view.util.CSS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Responsible for rendering the game board, including tiles, ladders, and players.
 * This class manages the visualization of the board and coordinates the different
 * visual components (tiles, ladders, players) into a cohesive UI.
 */
public class BoardView {
    private static final int GRID_GAP = 4;

    private static final Map<String, String> BOARD_BACKGROUNDS = initBackgroundMap();
    private static final String DEFAULT_BACKGROUND = "/image/background/mainmenu.png";

    private final BoardController boardController;
    private final TileView tileView;
    private final LadderView ladderView;
    private final LadderController ladderController;
    private final PlayerController playerController;
    private final CSS css;
    private final Map<Integer, Node> tileNodeMap = new HashMap<>();

    /**
     * Initializes the map of board backgrounds.
     *
     * @return Map of board names to background image paths
     */
    private static Map<String, String> initBackgroundMap() {
        Map<String, String> backgrounds = new HashMap<>();
        backgrounds.put("Spiral Way", "/image/background/background_1.png");
        backgrounds.put("Ladderia Prime", "/image/background/background_2.png");
        backgrounds.put("ZigZag Heights", "/image/background/background_3.png");
        return backgrounds;
    }

    /**
     * Creates a new BoardView with the specified controller.
     *
     * @param boardController controller managing the board model
     * @throws NullPointerException if boardController is null
     */
    public BoardView(BoardController boardController) {
        this.boardController = Objects.requireNonNull(boardController, "BoardController cannot be null");
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
     *
     * @return StackPane containing the complete board UI with all components
     */
    public StackPane createBoardPanel() {
        // Create the different layers of the board
        StackPane scalingContainer = new StackPane();
        GridPane gridPane = createTileGrid();
        Pane ladderPane = new Pane();
        Pane playerPane = new Pane();

        // Setup the board's tile node map in the model
        boardController.getBoard().setTileNodeMap(tileNodeMap);

        // Add ladders and players to their respective panes
        ladderController.addLaddersToBoard(ladderPane, tileNodeMap);
        playerController.addPlayersToBoard(playerPane);

        // Combine all layers into a single stack pane
        StackPane boardPane = new StackPane();
        boardPane.getChildren().addAll(gridPane, ladderPane, playerPane);

        return boardPane;
    }

    /**
     * Creates the grid layout containing all tiles.
     *
     * @return GridPane containing the tile layout
     */
    private GridPane createTileGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(GRID_GAP);
        gridPane.setVgap(GRID_GAP);

        List<Tile> tiles = boardController.getTiles();

        // Calculate normalization offsets to ensure grid starts at (0,0)
        int minX = calculateMinimumCoordinate(tiles, Tile::getX);
        int minY = calculateMinimumCoordinate(tiles, Tile::getY);

        // Add each tile to the grid
        for (Tile tile : tiles) {
            addTileToGrid(gridPane, tile, minX, minY);
        }

        return gridPane;
    }

    /**
     * Calculates the minimum value for a coordinate across all tiles.
     *
     * @param tiles list of tiles
     * @param coordinateExtractor function to extract the coordinate
     * @return minimum coordinate value
     */
    private int calculateMinimumCoordinate(List<Tile> tiles, java.util.function.ToIntFunction<Tile> coordinateExtractor) {
        return tiles.stream()
                .mapToInt(coordinateExtractor)
                .min()
                .orElse(0);
    }

    /**
     * Creates and adds a tile to the grid at the appropriate position.
     * Tile 0 (starting tile) is invisible and mouse transparent.
     *
     * @param gridPane the grid to add the tile to
     * @param tile the tile to add
     * @param minX minimum X coordinate for normalization
     * @param minY minimum Y coordinate for normalization
     */
    private void addTileToGrid(GridPane gridPane, Tile tile, int minX, int minY) {
        int tileIndex = tile.getIndex();

        if (tileIndex == 0) {
            StackPane placeholder = tileView.createTile(tileIndex);
            placeholder.setOpacity(0);
            placeholder.setMouseTransparent(true);
            int normalizedX = tile.getX() - minX;
            int normalizedY = tile.getY() - minY;
            gridPane.add(placeholder, normalizedX, normalizedY);
            tileNodeMap.put(tileIndex, placeholder);
            return;
        }

        StackPane tilePane = tileView.createTile(tileIndex);
        int normalizedX = tile.getX() - minX;
        int normalizedY = tile.getY() - minY;
        gridPane.add(tilePane, normalizedX, normalizedY);
        tileNodeMap.put(tileIndex, tilePane);
    }

    /**
     * Returns the background for the given board name.
     *
     * @param boardName the name of the board
     * @return the background image for the specified board
     */
    public Background getBackgroundForBoard(String boardName) {
        String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(
                Objects.requireNonNull(boardName, "Board name cannot be null"),
                DEFAULT_BACKGROUND
        );
        return css.createSpaceBackground(backgroundPath);
    }

    /**
     * Gets the player controller associated with this board view.
     *
     * @return the player controller
     */
    public PlayerController getPlayerController() {
        return playerController;
    }

    /**
     * Gets the ladder controller associated with this board view.
     *
     * @return the ladder controller
     */
    public LadderController getLadderController() {
        return ladderController;
    }
}