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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
    private Timeline gameTimer;

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

        Integer targetClicks = gameMenu.getTargetClicks();
        Integer timerSeconds = gameMenu.getTimerSeconds();

        // Use StackPane for overlays
        StackPane root = new StackPane();
        BorderPane gameRoot = new BorderPane();
        gameRoot.setCenter(gui.createSidePanels());
        root.getChildren().add(gameRoot);

        // Timer label
        Label timerLabel = new Label();
        timerLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #eebbc3; -fx-font-weight: bold;");
        timerLabel.setVisible(false);
        root.getChildren().add(timerLabel);

        // Countdown overlay
        Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-font-size: 64px; -fx-text-fill: #eebbc3; -fx-font-weight: bold;");
        root.getChildren().add(countdownLabel);

        Scene gameScene = new Scene(root, GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        css.applyStyleSheet(gameScene, "/css/modern-theme.css");

        List<Player> playersList = new ArrayList<>(playerController.getPlayers());
        if (!playersList.isEmpty()) {
            gui.setCurrentPlayer(playersList.get(0));
        }

        // Key event handler (disabled until countdown is done)
        gameScene.setOnKeyPressed(event -> {
            if (!countdownLabel.isVisible()) {
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

                    if (targetClicks != null && gui.getClicks() >= targetClicks) {
                        endGame(gui);
                    }
                }
            }
        });
        gameScene.getRoot().requestFocus();

        // Countdown sequence
        Timeline countdown = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> countdownLabel.setText("3")),
                new KeyFrame(Duration.seconds(1), e -> countdownLabel.setText("2")),
                new KeyFrame(Duration.seconds(2), e -> countdownLabel.setText("1")),
                new KeyFrame(Duration.seconds(3), e -> countdownLabel.setText("Go!")),
                new KeyFrame(Duration.seconds(4), e -> {
                    countdownLabel.setVisible(false);
                    timerLabel.setVisible(timerSeconds != null);
                    if (timerSeconds != null) {
                        startGameTimer(timerLabel, timerSeconds, gui);
                    }
                })
        );
        countdown.setCycleCount(1);
        countdown.play();

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Click Game");
        primaryStage.show();
    }

    // Timer logic with visible countdown
    private void startGameTimer(Label timerLabel, int seconds, Gui gui) {
        timerLabel.setText("Time: " + seconds);
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            int timeLeft = seconds;
            @Override
            public void handle(ActionEvent event) {
                timeLeft--;
                timerLabel.setText("Time: " + timeLeft);
                if (timeLeft <= 0) {
                    endGame(gui);
                    if (gameTimer != null) gameTimer.stop();
                }
            }
        }));
        gameTimer.setCycleCount(seconds);
        gameTimer.play();
    }

    private void endGame(Gui gui) {
        if (gameTimer != null) gameTimer.stop();
        Player winner = gui.getCurrentPlayer();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished!");
        alert.setContentText(winner.getName() + " wins with " + gui.getClicks() + " clicks!");
        alert.showAndWait();
        showGameMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}