package edu.ntnu.iir.bidata.clickgame.gui;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.laddergame.view.menu.GameSetupMenu;
import edu.ntnu.iir.bidata.laddergame.view.menu.PlayerMenu;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import edu.ntnu.iir.bidata.laddergame.view.util.PlayerData;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GameMenu extends GameSetupMenu {
    private static GameMenu instance;
    private int playerCount = 2;
    private Integer targetClicks = null;
    private Integer timerSeconds = null;
    private List<PlayerData> playerData = null;
    private final CSS css;

    public GameMenu(MainMenuController mainMenuController, Stage primaryStage) {
        super(mainMenuController, primaryStage);
        instance = this;
        this.css = new CSS();
    }

    public static GameMenu getInstance() {
        return instance;
    }

    public void show(Stage primaryStage, Runnable onStartGame) {
        Label playerCountLabel = new Label("Number of Players:");
        Spinner<Integer> playerCountSpinner = new Spinner<>(2, 4, playerCount);
        playerCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> playerCount = newVal);

        Label clicksLabel = new Label("First to X Clicks (leave empty for timer mode):");
        TextField clicksField = new TextField();

        Label timerLabel = new Label("Timer (seconds, leave empty for first-to mode):");
        TextField timerField = new TextField();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            errorLabel.setText("");
            targetClicks = null;
            timerSeconds = null;
            try {
                String clicksText = clicksField.getText().trim();
                String timerText = timerField.getText().trim();
                if (clicksText.isEmpty() && timerText.isEmpty()) {
                    errorLabel.setText("Enter at least one value.");
                    return;
                }
                if (!clicksText.isEmpty()) {
                    targetClicks = Integer.parseInt(clicksText);
                    if (targetClicks <= 0) throw new NumberFormatException();
                }
                if (!timerText.isEmpty()) {
                    timerSeconds = Integer.parseInt(timerText);
                    if (timerSeconds <= 0) throw new NumberFormatException();
                }

                // Launch PlayerMenu dialog here
                PlayerMenu playerMenu = new PlayerMenu(css);
                List<PlayerData> playerDetails = playerMenu.collectPlayerDetails(playerCount);

                if (playerDetails != null) {
                    setPlayerData(playerDetails);
                    onStartGame.run();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid positive numbers.");
            }
        });

        VBox root = new VBox(12, playerCountLabel, playerCountSpinner, clicksLabel, clicksField, timerLabel, timerField, errorLabel, startButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(root, 350, 350);
        scene.getStylesheets().add(getClass().getResource("/css/modern-theme.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Click Game Setup");
        primaryStage.show();
    }

    public void setPlayerData(List<PlayerData> playerData) {
        this.playerData = playerData;
    }

    public List<PlayerData> getPlayerData() {
        return playerData;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Integer getTargetClicks() {
        return targetClicks;
    }

    public Integer getTimerSeconds() {
        return timerSeconds;
    }
}