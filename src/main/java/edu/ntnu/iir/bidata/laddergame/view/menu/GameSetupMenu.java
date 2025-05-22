package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.laddergame.file.BoardRegistry;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.util.List;

public class GameSetupMenu {
    private final MainMenuController controller;
    private final Stage primaryStage;
    private final CSS css;

    // Constants for better maintenance
    private static final String BACKGROUND_PATH = "/image/background/mainmenu.png";
    private static final int SPACING = 25;
    private static final int PADDING = 30;

    public GameSetupMenu(MainMenuController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        this.css = new CSS();
    }

    public void showGameSetup() {
        BorderPane root = new BorderPane();
        root.setBackground(css.createSpaceBackground(BACKGROUND_PATH));

        // Create a title
        Label titleLabel = css.createStyledLabel("MISSION SETUP", FontWeight.BOLD, 28, css.getSpaceBlue());

        // Board selection
        Label boardLabel = css.createStyledLabel("SELECT GALAXY:", FontWeight.BOLD, 16, Color.WHITE);

        ComboBox<String> boardSelector = new ComboBox<>();
        boardSelector.getItems().addAll(BoardRegistry.getInstance().getBoardNames());
        boardSelector.setValue(boardSelector.getItems().isEmpty() ? "" : boardSelector.getItems().get(0));
        boardSelector.getStyleClass().add("space-combo-box");
        boardSelector.setMaxWidth(Double.MAX_VALUE);

        // Player count
        Label playerLabel = css.createStyledLabel("SPACE TRAVELERS:", FontWeight.BOLD, 16, Color.WHITE);

        Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
        playerSpinner.getStyleClass().addAll("space-spinner", "split-arrows-horizontal");
        playerSpinner.setMaxWidth(Double.MAX_VALUE);

        // Game options section
        Label optionsLabel = css.createStyledLabel("MISSION OPTIONS:", FontWeight.BOLD, 16, Color.WHITE);

        CheckBox chanceCheckBox = new CheckBox("Random Chance Tiles");
        chanceCheckBox.setSelected(true);
        chanceCheckBox.setTextFill(Color.WHITE);

        // Set percentage of chance tiles (5-25%)
        Label chancePercentLabel = css.createStyledLabel("Chance Tile Frequency:", FontWeight.NORMAL, 12, Color.LIGHTGRAY);
        Slider chancePercentSlider = new Slider(5, 25, 10);
        chancePercentSlider.setShowTickLabels(true);
        chancePercentSlider.setShowTickMarks(true);
        chancePercentSlider.setMajorTickUnit(5);
        chancePercentSlider.setMinorTickCount(0);
        chancePercentSlider.setSnapToTicks(true);

        VBox chanceOptions = new VBox(5, chancePercentLabel, chancePercentSlider);
        chanceOptions.setPadding(new Insets(0, 0, 0, 20));

        // Single listener to handle enabling/disabling the options
        chanceCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            chanceOptions.setDisable(!newVal);
        });

        // Set initial state based on checkbox
        chanceOptions.setDisable(!chanceCheckBox.isSelected());

        // Action buttons
        Button startBtn = css.createSpaceButton("Launch Mission");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> {
            List<PlayerData> playerDetails = new PlayerMenu(css).collectPlayerDetails(playerSpinner.getValue());
            if (playerDetails != null) {
                boolean enableChanceTiles = chanceCheckBox.isSelected();
                int chancePercentage = (int) chancePercentSlider.getValue();
                controller.startNewGameWithOptions(boardSelector.getValue(),
                        playerSpinner.getValue(),
                        playerDetails,
                        enableChanceTiles,
                        chancePercentage);
            }
        });

        // Add CSS-styled tooltip to start button
        Tooltip startTooltip = new Tooltip("Begin your space adventure");
        startTooltip.setShowDelay(Duration.millis(500));
        startTooltip.getStyleClass().add("space-tooltip");
        Tooltip.install(startBtn, startTooltip);

        Button backBtn = css.createSpaceButton("Return to Base");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> new MainMenu(controller, primaryStage).showMainMenu());

        // Add CSS-styled tooltip to back button
        Tooltip backTooltip = new Tooltip("Return to main menu");
        backTooltip.setShowDelay(Duration.millis(500));
        backTooltip.getStyleClass().add("space-tooltip");
        Tooltip.install(backBtn, backTooltip);

        // Layout containers
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(PADDING, 0, PADDING, 0));

        // Settings container
        VBox settingsBox = new VBox(
                boardLabel, boardSelector,
                new Separator(),
                playerLabel, playerSpinner,
                new Separator(),
                optionsLabel, chanceCheckBox, chanceOptions,
                new Separator(),
                startBtn, backBtn);

        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setSpacing(SPACING);
        settingsBox.setPadding(new Insets(0, PADDING, PADDING, PADDING));
        settingsBox.getStyleClass().add("space-settings-box");
        settingsBox.setMaxWidth(400);  // Limit width for better appearance

        // Main container
        VBox mainContent = new VBox(titleBox, settingsBox);
        mainContent.setAlignment(Pos.CENTER);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        css.applyDefaultStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showLoadGameDialog() {
        new LoadGameDialog(controller, primaryStage).show();
    }
}