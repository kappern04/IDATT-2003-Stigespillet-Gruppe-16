package edu.ntnu.iir.bidata.view.menu;

import edu.ntnu.iir.bidata.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.file.BoardRegistry;
import edu.ntnu.iir.bidata.view.util.CSS;
import edu.ntnu.iir.bidata.view.util.PlayerData;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class GameSetupMenu {
    private final MainMenuController controller;
    private final Stage primaryStage;
    private final CSS css;

    public GameSetupMenu(MainMenuController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        this.css = new CSS();
    }

    public void showGameSetup() {
        BorderPane root = new BorderPane();
        root.setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));

        Label boardLabel = new Label("SELECT GALAXY:");
        boardLabel.getStyleClass().add("space-label");

        ComboBox<String> boardSelector = new ComboBox<>();
        boardSelector.getItems().addAll(BoardRegistry.getInstance().getBoardNames());
        boardSelector.setValue(boardSelector.getItems().isEmpty() ? "" : boardSelector.getItems().get(0));
        boardSelector.getStyleClass().add("space-combo-box");

        Label playerLabel = new Label("SPACE TRAVELERS:");
        playerLabel.getStyleClass().add("space-label");

        Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
        playerSpinner.getStyleClass().addAll("space-spinner", "split-arrows-horizontal");

        Button startBtn = css.createSpaceButton("Launch Mission");
        startBtn.setOnAction(e -> {
            List<PlayerData> playerDetails = new PlayerMenu(css).collectPlayerDetails(playerSpinner.getValue());
            if (playerDetails != null) {
                controller.startNewGame(boardSelector.getValue(), playerSpinner.getValue(), playerDetails);
            }
        });

        Button backBtn = css.createSpaceButton("Return to Base");
        backBtn.setOnAction(e -> new MainMenu(controller, primaryStage).showMainMenu());

        VBox settingsBox = new VBox(20,
                boardLabel, boardSelector,
                playerLabel, playerSpinner,
                startBtn, backBtn);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setSpacing(25);
        settingsBox.getStyleClass().add("space-settings-box");
        root.setCenter(settingsBox);

        Scene scene = new Scene(root, 700, 500);
        css.applyDefaultStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showLoadGameDialog() {
        new LoadGameDialog(controller, primaryStage).show();
    }
}