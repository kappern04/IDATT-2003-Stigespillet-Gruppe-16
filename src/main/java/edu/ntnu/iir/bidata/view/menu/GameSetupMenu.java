package edu.ntnu.iir.bidata.view.menu;

import edu.ntnu.iir.bidata.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.file.BoardRegistry;
import edu.ntnu.iir.bidata.view.util.CSS;
import edu.ntnu.iir.bidata.view.util.PlayerData;
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

        // Action buttons
        Button startBtn = css.createSpaceButton("Launch Mission");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> {
            List<PlayerData> playerDetails = new PlayerMenu(css).collectPlayerDetails(playerSpinner.getValue());
            if (playerDetails != null) {
                controller.startNewGame(boardSelector.getValue(), playerSpinner.getValue(), playerDetails);
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

        Scene scene = new Scene(root, 700, 600);
        css.applyDefaultStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showLoadGameDialog() {
        new LoadGameDialog(controller, primaryStage).show();
    }
}