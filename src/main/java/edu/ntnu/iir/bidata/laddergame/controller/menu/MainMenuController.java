package edu.ntnu.iir.bidata.laddergame.controller.menu;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.file.BoardRegistry;
import edu.ntnu.iir.bidata.laddergame.file.GameSaveReaderCSV;
import edu.ntnu.iir.bidata.laddergame.file.SaveFileTracker;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.CosmicChanceAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;
import edu.ntnu.iir.bidata.laddergame.view.BoardGameView;
import edu.ntnu.iir.bidata.laddergame.view.menu.MainMenu;
import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Controller for the main menu, handling new game and load game actions.
 */
public class MainMenuController {
    private final Stage primaryStage;
    private final MainMenu view;

    public MainMenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = new MainMenu(this, primaryStage);
        view.showMainMenu();
    }

    /**
     * Starts a new game with the selected board and player details.
     *
     * @param boardName     the name of the board to load
     * @param numPlayers    the number of players
     * @param playerDetails the list of player data
     */
    public void startNewGame(String boardName, int numPlayers, List<PlayerData> playerDetails) {
        // For backward compatibility, call the enhanced method with default chance settings
        startNewGameWithOptions(boardName, numPlayers, playerDetails, false, 0, false);
    }

    /**
     * Starts a new game with the given options.
     *
     * @param boardName         the name of the board
     * @param playerCount       the number of players
     * @param playerDetails     the player details
     * @param enableChanceTiles whether chance tiles are enabled
     * @param chancePercentage  the percentage of chance tiles
     * @param doubleDiceMode    whether double dice mode is enabled
     */
    public void startNewGameWithOptions(
            String boardName,
            int playerCount,
            List<PlayerData> playerDetails,
            boolean enableChanceTiles,
            int chancePercentage,
            boolean doubleDiceMode
    ) {
        if (boardName == null || boardName.isBlank() || playerDetails == null || playerDetails.size() < playerCount) {
            showError("Invalid game setup", "Please select a board and enter valid player details.");
            return;
        }

        SaveFileTracker.getInstance().reset();

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            PlayerData data = playerDetails.get(i);
            players.add(new Player(data.getName(), data.getColor(), data.getShipType()));
        }

        Board board;
        try {
            board = BoardRegistry.getInstance().getBoardByName(boardName);
            if (board == null) throw new IllegalArgumentException("Board not found: " + boardName);

            // Apply chance tiles if enabled
            if (enableChanceTiles && chancePercentage > 0) {
                applyChanceTilesToBoard(board, chancePercentage);
            }
        } catch (Exception e) {
            showError("Board Loading Error", "Could not load board: " + e.getMessage());
            board = new Board();
        }

        BoardGameController game = new BoardGameController();
        game.setPlayers(players);
        game.setBoard(board);

        BoardGameView boardGameView = new BoardGameView(game);
        boardGameView.setUpStage(primaryStage);
    }

    /**
     * Applies chance tiles to the board based on the given percentage.
     *
     * @param board the game board
     * @param chancePercentage percentage of normal tiles to convert to chance tiles
     */
    private void applyChanceTilesToBoard(Board board, int chancePercentage) {
        List<Tile> tiles = board.getTiles();
        List<Tile> eligibleTiles = new ArrayList<>();

        // Find all eligible tiles (not ladder tiles and not the first or last tile)
        for (Tile tile : tiles) {
            int index = tile.getIndex();
            // Skip first tile, last tile, and tiles that already have ladder actions
            if (index != 0 && index != board.getLastTile() && !tile.hasLadderAction() && !tile.isDestinationOfLadder(board)) {
                eligibleTiles.add(tile);
            }
        }

        // Calculate how many tiles to convert
        int tilesToConvert = (int) Math.ceil(eligibleTiles.size() * (chancePercentage / 100.0));

        // Randomly select tiles to convert
        Random random = new Random();
        for (int i = 0; i < tilesToConvert && !eligibleTiles.isEmpty(); i++) {
            int randomIndex = random.nextInt(eligibleTiles.size());
            Tile selectedTile = eligibleTiles.remove(randomIndex);

            // Randomly select a chance effect type
            ChanceEffectType[] effectTypes = ChanceEffectType.values();
            ChanceEffectType randomEffect = effectTypes[random.nextInt(effectTypes.length)];

            selectedTile.setTileAction(new CosmicChanceAction(randomEffect));
            selectedTile.setType("chance");
        }
    }

    /**
     * Loads a saved game from the given file.
     *
     * @param file the save file to load
     */
    public void loadGame(File file) {
        if (file == null || !file.exists()) {
            showError("Load Failed", "Selected save file does not exist.");
            return;
        }
        try {
            GameSaveReaderCSV saveReader = new GameSaveReaderCSV();
            BoardGameController loadedGame = saveReader.loadGame(file.getAbsolutePath());
            BoardGameView boardGameView = new BoardGameView(loadedGame);
            boardGameView.setUpStage(primaryStage);
        } catch (IOException ex) {
            showError("Load Failed", "Failed to load game: " + ex.getMessage());
        }
    }

    /**
     * Shows an error alert with the given title and message.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}