package edu.ntnu.iir.bidata.view.elements;

import edu.ntnu.iir.bidata.Stigespillet;
import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.file.SaveFileTracker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class InGameMenu extends VBox {
  private BoardGame boardGame;
  private Stage menuStage;
  private CSS css;

  public InGameMenu(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.css = new CSS();
    setupMenu();
  }

  private void setupMenu() {
    initializeStage();
    configureLayout();

    Text title = createAnimatedTitle();
    Button resumeButton = createResumeButton();
    Button saveButton = createSaveButton();
    Button mainMenuButton = createMainMenuButton();
    Button exitButton = createExitButton();

    getChildren().addAll(title, resumeButton, mainMenuButton, saveButton, exitButton);

    setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));
    setupScene();
  }

  private void initializeStage() {
    menuStage = new Stage();
    menuStage.initModality(Modality.APPLICATION_MODAL);
    menuStage.initStyle(StageStyle.UNDECORATED);
  }

  private void configureLayout() {
    setSpacing(30);
    setAlignment(Pos.CENTER);
    setPadding(new javafx.geometry.Insets(40));
  }

  private Text createAnimatedTitle() {
    Text title = new Text("MISSION CONTROL");
    title.setFont(css.getOrbitronFont(24, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    Glow glow = new Glow(0.8);
    title.setEffect(glow);

    Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulse.setCycleCount(Animation.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    return title;
  }

  private Button createResumeButton() {
    Button button = css.createSpaceButton("Resume Mission");
    button.setOnAction(e -> menuStage.close());
    return button;
  }

  private Button createSaveButton() {
    Button button = css.createSpaceButton("Save Mission");
    button.setOnAction(e -> handleSaveAction());
    return button;
  }

  private void handleSaveAction() {
    try {
      GameSaveWriterCSV saveWriter = new GameSaveWriterCSV();
      String savedFilePath = saveGame(saveWriter);
      showSaveSuccessMessage(savedFilePath);
      menuStage.close();
    } catch (IOException ex) {
      showSaveErrorMessage(ex.getMessage());
    }
  }

  private String saveGame(GameSaveWriterCSV saveWriter) throws IOException {
    if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
      TextInputDialog dialog = createSaveDialog();
      Optional<String> result = dialog.showAndWait();

      if (result.isPresent() && !result.get().trim().isEmpty()) {
        return saveWriter.saveGame(boardGame, null, result.get());
      } else {
        return saveWriter.saveGame(boardGame, null);
      }
    } else {
      String fileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
      return saveWriter.saveGame(boardGame, null, fileName);
    }
  }

  private TextInputDialog createSaveDialog() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Save Game");
    dialog.setHeaderText("Enter a name for your save file:");
    dialog.setContentText("Filename:");
    return dialog;
  }

  private void showSaveSuccessMessage(String savedFilePath) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Mission Saved");
    alert.setHeaderText(null);
    alert.setContentText("Game saved successfully to: " + savedFilePath);
    alert.showAndWait();
  }

  private void showSaveErrorMessage(String errorMessage) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Save Failed");
    alert.setHeaderText(null);
    alert.setContentText("Failed to save game: " + errorMessage);
    alert.showAndWait();
  }

  public Button createMainMenuButton() {
    Button button = css.createSpaceButton("Return to Base");
    button.setOnAction(e -> handleMainMenuAction());
    return button;
  }

  private void handleMainMenuAction() {
    MusicControlPanel.stopMusic();
    menuStage.close();
    closeAllStagesExcept(menuStage);
    restartApplication();
  }

  private void closeAllStagesExcept(Stage exceptStage) {
    List<Stage> stagesToClose = new ArrayList<>();
    for (Window window : Stage.getWindows()) {
      if (window instanceof Stage && window != exceptStage) {
        stagesToClose.add((Stage) window);
      }
    }

    for (Stage stage : stagesToClose) {
      stage.close();
    }
  }

  private void restartApplication() {
    Platform.runLater(() -> {
      Stigespillet stigespillet = new Stigespillet();
      try {
        stigespillet.start(new Stage());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  private Button createExitButton() {
    Button button = css.createSpaceButton("Abort Mission");
    button.setOnAction(e -> System.exit(0));
    return button;
  }

  private void setupScene() {
    Scene scene = new Scene(this, 400, 500);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    menuStage.setScene(scene);
  }

  public void show() {
    menuStage.show();
  }
}