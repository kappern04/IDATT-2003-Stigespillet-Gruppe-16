package edu.ntnu.iir.bidata.controller.other;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.file.BoardRegistry;
import edu.ntnu.iir.bidata.file.GameSaveReaderCSV;
import edu.ntnu.iir.bidata.view.other.MainMenu;
import edu.ntnu.iir.bidata.view.BoardGameView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainMenuController {
    private Stage primaryStage;
    private MainMenu view;

    public MainMenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = new MainMenu(this, primaryStage);
        view.showMainMenu();
    }

    public void startNewGame(String boardType, int numPlayers, List<String> playerNames) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(playerNames.get(i));
        }
        BoardGameController game = new BoardGameController();
        game.setPlayers(players);

        Board board;
        try {
            board = BoardRegistry.getInstance().getBoardByName(boardType);
            if (board == null) throw new RuntimeException("Board not found: " + boardType);
        } catch (RuntimeException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Board Loading Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load board: " + e.getMessage());
            alert.showAndWait();
            board = new Board();
        }
        game.setBoard(board);
        BoardGameView boardGameView = new BoardGameView(game);
        boardGameView.setUpStage(primaryStage);
    }

    public void loadGame(File file) {
        try {
            GameSaveReaderCSV saveReader = new GameSaveReaderCSV();
            BoardGameController loadedGame = saveReader.loadGame(file.getAbsolutePath());
            BoardGameView boardGameView = new BoardGameView(loadedGame);
            boardGameView.setUpStage(primaryStage);
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Load Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load game: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}