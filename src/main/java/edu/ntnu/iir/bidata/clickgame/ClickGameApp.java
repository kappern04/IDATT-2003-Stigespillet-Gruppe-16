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
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ClickGameApp extends Application {
    private static final int GAME_WINDOW_WIDTH = 800;
    private static final int GAME_WINDOW_HEIGHT = 800;

    // Map player positions to key codes for better readability
    private static final Map<KeyCode, Integer> PLAYER_KEYS = Map.of(
            KeyCode.DIGIT1, 0,
            KeyCode.DIGIT4, 1,
            KeyCode.DIGIT7, 2,
            KeyCode.DIGIT0, 3
    );

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
        List<Player> playersList = new ArrayList<>(playerController.getPlayers());

        // Initialize UI components
        StackPane root = createGameLayout(gui);
        Label timerLabel = createTimerLabel();
        Label countdownLabel = createCountdownLabel();

        root.getChildren().addAll(timerLabel, countdownLabel);

        Scene gameScene = new Scene(root, gui.prefWidth(-1), gui.prefHeight(-1));
        css.applyStyleSheet(gameScene, "/css/modern-theme.css");

        // Set initial player
        if (!playersList.isEmpty()) {
            gui.setCurrentPlayer(playersList.get(0));
        }

        // Setup key press handling
        setupKeyHandling(gameScene, gui, playersList, countdownLabel, targetClicks);

        // Start countdown
        startCountdown(countdownLabel, timerLabel, timerSeconds, gui);

        // Show the game window
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Click Game");
        primaryStage.show();
        gameScene.getRoot().requestFocus();
    }

    private StackPane createGameLayout(Gui gui) {
        StackPane root = new StackPane();
        BorderPane gameRoot = new BorderPane();
        gameRoot.setCenter(gui.createSidePanels());
        root.getChildren().add(gameRoot);
        return root;
    }

    private Label createTimerLabel() {
        Label timerLabel = new Label();
        timerLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #eebbc3; -fx-font-weight: bold;");
        timerLabel.setVisible(false);
        return timerLabel;
    }

    private Label createCountdownLabel() {
        Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-font-size: 64px; -fx-text-fill: #eebbc3; -fx-font-weight: bold;");
        return countdownLabel;
    }

    private void setupKeyHandling(Scene gameScene, Gui gui, List<Player> playersList,
                                  Label countdownLabel, Integer targetClicks) {
        gameScene.setOnKeyPressed(event -> {
            if (countdownLabel.isVisible()) {
                return; // Don't process keys during countdown
            }

            Integer playerIndex = PLAYER_KEYS.get(event.getCode());
            if (playerIndex != null && playerIndex < playersList.size()) {
                Player player = playersList.get(playerIndex);
                gui.setCurrentPlayer(player);
                gui.incrementClicks();
                gui.updatePlayerClicks(player);

                if (targetClicks != null && gui.getClicks() >= targetClicks) {
                    endGame(gui);
                }
            }
        });
    }

    private void startCountdown(Label countdownLabel, Label timerLabel, Integer timerSeconds, Gui gui) {
        Timeline countdown = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> countdownLabel.setText("3")),
                new KeyFrame(Duration.seconds(1), e -> countdownLabel.setText("2")),
                new KeyFrame(Duration.seconds(2), e -> countdownLabel.setText("1")),
                new KeyFrame(Duration.seconds(3), e -> countdownLabel.setText("Go!")),
                new KeyFrame(Duration.seconds(4), e -> {
                    countdownLabel.setVisible(false);
                    if (timerSeconds != null) {
                        timerLabel.setVisible(true);
                        startGameTimer(timerLabel, timerSeconds, gui);
                    }
                })
        );
        countdown.setCycleCount(1);
        countdown.play();
    }

    private void startGameTimer(Label timerLabel, int seconds, Gui gui) {
        final int[] timeLeft = {seconds};
        timerLabel.setText("Time: " + timeLeft[0]);

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft[0]--;
            timerLabel.setText("Time: " + timeLeft[0]);
            if (timeLeft[0] <= 0) {
                endGame(gui);
                if (gameTimer != null) gameTimer.stop();
            }
        }));

        gameTimer.setCycleCount(seconds);
        gameTimer.play();
    }

    private void endGame(Gui gui) {
        if (gameTimer != null) gameTimer.stop();

        List<Player> winners = determineWinners(gui);

        Platform.runLater(() -> {
            Alert alert = createGameOverAlert(winners, gui.getClicks());
            alert.showAndWait();
            showGameMenu();
        });
    }

    private List<Player> determineWinners(Gui gui) {
        List<Player> allPlayers = new ArrayList<>(playerController.getPlayers());
        List<Player> winners = new ArrayList<>();
        int maxClicks = 0;

        // Find the highest click count
        for (Player player : allPlayers) {
            int playerClicks = gui.getPlayerClicks(player);
            if (playerClicks > maxClicks) {
                maxClicks = playerClicks;
            }
        }

        // Find all players with the highest click count
        for (Player player : allPlayers) {
            if (gui.getPlayerClicks(player) == maxClicks) {
                winners.add(player);
            }
        }

        return winners;
    }

    private Alert createGameOverAlert(List<Player> winners, int clicks) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished!");

        String contentText;
        if (winners.size() == 1) {
            // Single winner
            contentText = winners.get(0).getName() + " wins with " + clicks + " clicks!";
        } else {
            // Multiple winners - it's a tie
            StringBuilder tieMessage = new StringBuilder("It's a tie between: ");
            for (int i = 0; i < winners.size(); i++) {
                tieMessage.append(winners.get(i).getName());
                if (i < winners.size() - 2) {
                    tieMessage.append(", ");
                } else if (i == winners.size() - 2) {
                    tieMessage.append(" and ");
                }
            }
            tieMessage.append(" with ").append(clicks).append(" clicks each!");
            contentText = tieMessage.toString();
        }

        alert.setContentText(contentText);

        // Apply custom CSS
        var cssUrl = getClass().getResource("/css/modern-theme.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            alert.getDialogPane().getStyleClass().add("custom-alert");
        }

        return alert;
    }

    @Override
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}