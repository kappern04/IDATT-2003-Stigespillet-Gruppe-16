package edu.ntnu.iir.bidata.clickgame.gui;

import edu.ntnu.iir.bidata.laddergame.view.menu.GameSetupMenu;
import edu.ntnu.iir.bidata.laddergame.view.menu.PlayerMenu;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import edu.ntnu.iir.bidata.clickgame.controller.DummyMainMenuController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class GameMenu extends GameSetupMenu {
    private static GameMenu instance;
    private int playerCount = 2;
    private Integer targetClicks = null;
    private Integer timerSeconds = null;
    private List<PlayerData> playerData = null;
    private final CSS css;
    private final DummyMainMenuController dummyMainMenuController;

    // Fixed dimensions for consistent layout
    private static final double MENU_WIDTH = 450;
    private static final double MENU_HEIGHT = 850;

    public GameMenu(DummyMainMenuController dummyMainMenuController, Stage primaryStage) {
        super(dummyMainMenuController, primaryStage);
        this.dummyMainMenuController = dummyMainMenuController;
        instance = this;
        this.css = new CSS();
    }

    public static GameMenu getInstance() {
        return instance;
    }

    public void show(Stage primaryStage, Runnable onStartGame) {
        // Using BorderPane for better structure
        BorderPane mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("game-menu");

        // Content container with fixed width
        VBox contentRoot = new VBox(20);
        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.setPadding(new Insets(30));
        contentRoot.setMinWidth(MENU_WIDTH - 60); // Accounting for padding
        contentRoot.setMaxWidth(MENU_WIDTH - 60);

        // Title
        Label titleLabel = new Label("Click Game Setup");
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        // Player count section
        VBox playerSection = createSection("Number of Players", createPlayerCountSpinner());

        // Key info section
        Label infoLabel = new Label("Player Keys to Press:\nP1: 1   P2: 4   P3: 7   P4: 0");
        infoLabel.getStyleClass().add("info-label");
        infoLabel.setMaxWidth(Double.MAX_VALUE);
        infoLabel.setAlignment(Pos.CENTER);

        // Game mode section
        VBox gameModeSection = createGameModeSection();

        // Error label
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMinHeight(24);

        // Start button
        Button startButton = new Button("Start Game");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> handleStartGame(errorLabel, onStartGame));
        startButton.setMaxWidth(Double.MAX_VALUE);

        contentRoot.getChildren().addAll(
                titleLabel,
                playerSection,
                infoLabel,
                gameModeSection,
                errorLabel,
                startButton
        );

        mainContainer.setCenter(contentRoot);

        Scene scene = new Scene(mainContainer, MENU_WIDTH, MENU_HEIGHT);
        css.applyStyleSheet(scene, "/css/modern-theme.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Click Game Setup");
        primaryStage.setMinWidth(MENU_WIDTH);
        primaryStage.setMinHeight(MENU_HEIGHT);
        primaryStage.show();
    }

    private Spinner<Integer> createPlayerCountSpinner() {
        Spinner<Integer> spinner = new Spinner<>(2, 4, playerCount);
        spinner.getStyleClass().addAll("player-spinner", "split-arrows-horizontal");
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> playerCount = newVal);
        return spinner;
    }

    private VBox createSection(String title, Control... controls) {
        VBox section = new VBox(10);
        section.getStyleClass().add("menu-section");
        section.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        section.getChildren().add(titleLabel);

        for (Control control : controls) {
            // Ensure all controls take full width
            VBox.setVgrow(control, Priority.NEVER);
            HBox.setHgrow(control, Priority.ALWAYS);
            control.setMaxWidth(Double.MAX_VALUE);
            section.getChildren().add(control);
        }

        return section;
    }

    private VBox createGameModeSection() {
        TextField clicksField = new TextField();
        clicksField.getStyleClass().add("input-field");
        clicksField.setPromptText("Enter number of clicks");
        clicksField.setId("clicksField");

        TextField timerField = new TextField();
        timerField.getStyleClass().add("input-field");
        timerField.setPromptText("Enter seconds");
        timerField.setId("timerField");

        VBox clicksSection = createSection(
                "X Clicks Win (empty = timer mode)",
                clicksField
        );

        VBox timerSection = createSection(
                "Countdown (s) (empty = click mode)",
                timerField
        );

        VBox container = new VBox(20);
        container.getChildren().addAll(clicksSection, timerSection);
        container.setMaxWidth(Double.MAX_VALUE);
        return container;
    }

    private void handleStartGame(Label errorLabel, Runnable onStartGame) {
        errorLabel.setText("");
        targetClicks = null;
        timerSeconds = null;

        try {
            if (!validateAndSetGameMode(errorLabel)) {
                return;
            }

            PlayerMenu playerMenu = new PlayerMenu(css);
            List<PlayerData> playerDetails = playerMenu.collectPlayerDetails(playerCount);

            if (playerDetails != null) {
                setPlayerData(playerDetails);
                onStartGame.run();
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter valid positive numbers.");
        }
    }

    private boolean validateAndSetGameMode(Label errorLabel) {
        // Find the input fields by traversing the scene graph
        TextField clicksField = (TextField) lookup("#clicksField");
        TextField timerField = (TextField) lookup("#timerField");

        String clicksText = clicksField.getText().trim();
        String timerText = timerField.getText().trim();

        // Check if at least one value is provided
        if (clicksText.isEmpty() && timerText.isEmpty()) {
            errorLabel.setText("Please set either clicks target or timer");
            return false;
        }

        // Parse clicks if provided
        if (!clicksText.isEmpty()) {
            try {
                int clicks = Integer.parseInt(clicksText);
                if (clicks <= 0) {
                    errorLabel.setText("Clicks must be a positive number");
                    return false;
                }
                targetClicks = clicks;
            } catch (NumberFormatException e) {
                errorLabel.setText("Invalid number format for clicks");
                return false;
            }
        }

        // Parse timer if provided
        if (!timerText.isEmpty()) {
            try {
                int timer = Integer.parseInt(timerText);
                if (timer <= 0) {
                    errorLabel.setText("Timer must be a positive number");
                    return false;
                }
                timerSeconds = timer;
            } catch (NumberFormatException e) {
                errorLabel.setText("Invalid number format for timer");
                return false;
            }
        }

        return true;
    }

    private Node lookup(String id) {
        Scene scene = ((Stage) dummyMainMenuController.getPrimaryStage()).getScene();
        return scene.lookup(id);
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