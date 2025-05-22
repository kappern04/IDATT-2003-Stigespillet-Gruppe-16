package edu.ntnu.iir.bidata.clickgame;

import edu.ntnu.iir.bidata.clickgame.controller.DummyMainMenuController;
import edu.ntnu.iir.bidata.clickgame.gui.GameMenu;
import edu.ntnu.iir.bidata.clickgame.gui.Gui;
import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import edu.ntnu.iir.bidata.laddergame.view.util.PlayerData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ClickGameApp extends Application {
    private static final int GAME_WINDOW_WIDTH = 800;
    private static final int GAME_WINDOW_HEIGHT = 600;

    private final CSS css = new CSS();
    private Stage primaryStage;
    private GameMenu gameMenu;
    private PlayerController playerController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeGameMenu();
        showGameMenu();
    }

    private void initializeGameMenu() {
        DummyMainMenuController mainMenuController = new DummyMainMenuController(primaryStage);
        gameMenu = new GameMenu(mainMenuController, primaryStage);
    }

    private void showGameMenu() {
        gameMenu.show(primaryStage, this::startGame);
    }

    private void startGame() {
        BoardGameController boardGameController = new BoardGameController();
        playerController = createPlayerController();
        setupGameControllers(boardGameController, playerController);
        showGameWindow(boardGameController, playerController);
    }

    private PlayerController createPlayerController() {
        Board board = new Board();
        List<Player> players = createPlayersFromData();
        return new PlayerController(board, players);
    }

    private List<Player> createPlayersFromData() {
        List<Player> players = new ArrayList<>();
        List<PlayerData> playerDataList = gameMenu.getPlayerData();

        if (playerDataList != null) {
            for (PlayerData data : playerDataList) {
                players.add(new Player(data.getName(), data.getColor(), data.getShipType()));
            }
        }
        return players;
    }

    private void setupGameControllers(BoardGameController boardGameController, PlayerController playerController) {
        Board board = new Board();
        boardGameController.setBoard(board);
        boardGameController.setPlayers(new ArrayList<>(playerController.getPlayers()));
    }

    private void showGameWindow(BoardGameController boardGameController, PlayerController playerController) {
        Gui gui = new Gui(boardGameController, playerController);

        // Get game settings
        Integer targetClicks = gameMenu.getTargetClicks();
        Integer timerSeconds = gameMenu.getTimerSeconds();

        // Set up timer if needed
        if (timerSeconds != null) {
            setupGameTimer(gui, timerSeconds);
        }

        Scene gameScene = createGameScene(gui, playerController, targetClicks);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Click Game");
        primaryStage.show();
    }

    private void setupGameTimer(Gui gui, int seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds), event -> endGame(gui))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    private Scene createGameScene(Gui gui, PlayerController playerController, Integer targetClicks) {
        BorderPane gameRoot = new BorderPane();
        gameRoot.setCenter(gui.createSidePanels());
        Scene scene = new Scene(gameRoot, GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        css.applyStyleSheet(scene, "/css/modern-theme.css");

        // Convert Set to List for index-based access
        List<Player> playersList = new ArrayList<>(playerController.getPlayers());

        // Set initial current player
        if (!playersList.isEmpty()) {
            gui.setCurrentPlayer(playersList.get(0));
        }

        scene.setOnKeyPressed(event -> {
            int playerIndex = -1;
            if (event.getCode() == KeyCode.DIGIT1) playerIndex = 0;
            else if (event.getCode() == KeyCode.DIGIT4) playerIndex = 1;
            else if (event.getCode() == KeyCode.DIGIT7) playerIndex = 2;
            else if (event.getCode() == KeyCode.DIGIT0) playerIndex = 3;

            if (playerIndex >= 0 && playerIndex < playersList.size()) {
                Player player = playersList.get(playerIndex);
                gui.setCurrentPlayer(player);
                gui.incrementClicks();
                gui.updatePlayerClicks(player);

                // Check for winner if targetClicks is set
                if (targetClicks != null && gui.getClicks() >= targetClicks) {
                    endGame(gui);
                }
            }
        });

        // Ensure the scene is focused to receive key events
        scene.getRoot().requestFocus();

        return scene;
    }

    private void endGame(Gui gui) {
        Player winner = gui.getCurrentPlayer();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished!");
        alert.setContentText(winner.getName() + " wins with " + gui.getClicks() + " clicks!");
        alert.showAndWait();

        // Return to menu
        showGameMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}