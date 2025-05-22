package edu.ntnu.iir.bidata.laddergame.controller.menu;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.file.BoardRegistry;
import edu.ntnu.iir.bidata.laddergame.file.GameSaveReaderCSV;
import edu.ntnu.iir.bidata.laddergame.file.SaveFileTracker;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.BoardGameView;
import edu.ntnu.iir.bidata.laddergame.view.menu.MainMenu;
import edu.ntnu.iir.bidata.laddergame.view.util.PlayerData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        if (boardName == null || boardName.isBlank() || playerDetails == null || playerDetails.size() < numPlayers) {
            showError("Invalid game setup", "Please select a board and enter valid player details.");
            return;
        }

        SaveFileTracker.getInstance().reset();

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            PlayerData data = playerDetails.get(i);
            players.add(new Player(data.getName(), data.getColor(), data.getShipType()));
        }

        Board board;
        try {
            board = BoardRegistry.getInstance().getBoardByName(boardName);
            if (board == null) throw new IllegalArgumentException("Board not found: " + boardName);
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