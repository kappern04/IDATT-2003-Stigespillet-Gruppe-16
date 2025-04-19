package edu.ntnu.iir.bidata.view.elements;

import edu.ntnu.iir.bidata.Stigespillet;
import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.file.SaveFileTracker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.stage.Window;
import javafx.util.Duration;

public class InGameMenu extends VBox {
  private BoardGame boardGame;
  private Stage menuStage;
  private final Color SPACE_BLUE = Color.rgb(64, 224, 208);
  private final Color SPACE_PURPLE = Color.rgb(138, 43, 226);
  private Font orbitronFont;

  public InGameMenu(BoardGame boardGame) {
    this.boardGame = boardGame;
    loadCustomFont();
    setupMenu();
  }

  private void loadCustomFont() {
    try {
      orbitronFont = Font.loadFont(getClass().getResourceAsStream("/font/Orbitron-VariableFont_wght.ttf"), 12);
      if (orbitronFont == null) {
        orbitronFont = Font.font("Arial");
      }
    } catch (Exception e) {
      orbitronFont = Font.font("Arial");
    }
  }

  private void setupMenu() {
    menuStage = new Stage();
    menuStage.initModality(Modality.APPLICATION_MODAL);
    menuStage.initStyle(StageStyle.UNDECORATED);

    setSpacing(30);
    setAlignment(Pos.CENTER);
    setPadding(new javafx.geometry.Insets(40));

    Text title = new Text("MISSION CONTROL");
    title.setFont(Font.font(orbitronFont.getFamily(), FontWeight.BOLD, 24));
    title.setFill(SPACE_BLUE);

    Glow glow = new Glow(0.8);
    title.setEffect(glow);

    Timeline pulse = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
        new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulse.setCycleCount(Animation.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    Button resumeButton = createSpaceButton("Resume Mission");
    Button saveButton = createSpaceButton("Save Mission");
    Button mainMenuButton = createSpaceButton("Return to Base");
    Button exitButton = createSpaceButton("Abort Mission");

    resumeButton.setOnAction(e -> menuStage.close());

    saveButton.setOnAction(e -> {
      try {
        GameSaveWriterCSV saveWriter = new GameSaveWriterCSV();
        String savedFilePath;

        // Only show the filename dialog if this is not a loaded save game
        if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
          // Show dialog to get custom filename
          TextInputDialog dialog = new TextInputDialog();
          dialog.setTitle("Save Game");
          dialog.setHeaderText("Enter a name for your save file:");
          dialog.setContentText("Filename:");

          Optional<String> result = dialog.showAndWait();

          if (result.isPresent() && !result.get().trim().isEmpty()) {
            // Use custom filename
            savedFilePath = saveWriter.saveGame(boardGame, null, result.get());
          } else {
            // Use timestamp (default behavior) if no name was provided
            savedFilePath = saveWriter.saveGame(boardGame, null);
          }
        } else {
          // Overwrite the original save file if this was loaded from a save
          String fileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
          savedFilePath = saveWriter.saveGame(boardGame, null, fileName);
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mission Saved");
        alert.setHeaderText(null);
        alert.setContentText("Game saved successfully to: " + savedFilePath);
        alert.showAndWait();

        menuStage.close();
      } catch (IOException ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Save Failed");
        alert.setHeaderText(null);
        alert.setContentText("Failed to save game: " + ex.getMessage());
        alert.showAndWait();
      }
    });

    mainMenuButton.setOnAction(e -> {
      MusicControlPanel.stopMusic();
      menuStage.close();

      // Close all stages except this one
      List<Stage> stagesToClose = new ArrayList<>();
      for (Window window : Stage.getWindows()) {
        if (window instanceof Stage && window != menuStage) {
          stagesToClose.add((Stage) window);
        }
      }

      // Close collected stages
      for (Stage stage : stagesToClose) {
        stage.close();
      }

      // Start a fresh instance of the main application
      Platform.runLater(() -> {
        Stigespillet stigespillet = new Stigespillet();
        try {
          stigespillet.start(new Stage());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      });
    });

    exitButton.setOnAction(e -> System.exit(0));

    getChildren().addAll(title, resumeButton, mainMenuButton, saveButton, exitButton);

    setupSpaceBackground();
    Scene scene = new Scene(this, 400, 500);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    menuStage.setScene(scene);
  }

  private Button createSpaceButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(250);
    button.setPrefHeight(50);
    button.setFont(Font.font(orbitronFont.getFamily(), FontWeight.BOLD, 16));
    button.setTextFill(Color.WHITE);
    button.getStyleClass().add("space-button");

    DropShadow shadow = new DropShadow();
    shadow.setColor(SPACE_BLUE);
    button.setEffect(shadow);

    button.setOnMouseEntered(e -> {
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
      shake.setFromX(-3);
      shake.setToX(3);
      shake.setCycleCount(6);
      shake.setAutoReverse(true);
      shake.play();

      shadow.setRadius(20);
      shadow.setColor(SPACE_PURPLE);
    });

    button.setOnMouseExited(e -> {
      shadow.setRadius(10);
      shadow.setColor(SPACE_BLUE);
    });

    return button;
  }

  private void setupSpaceBackground() {
    BackgroundImage bgImage = new BackgroundImage(
        new Image(getClass().getResourceAsStream("/image/background/mainmenu.png")),
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

    setBackground(new Background(bgImage));
  }

  public void show() {
    menuStage.show();
  }
}