package edu.ntnu.iir.bidata.view.menu;

import edu.ntnu.iir.bidata.controller.menu.InGameMenuController;
import edu.ntnu.iir.bidata.file.SaveFileTracker;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.io.IOException;
import java.util.Optional;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class InGameMenu extends VBox {
  private InGameMenuController controller;
  private Stage menuStage;
  private CSS css;

  public InGameMenu(InGameMenuController controller) {
    this.controller = controller;
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
    String fileName = null;
    if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
      TextInputDialog dialog = createSaveDialog();
      Optional<String> result = dialog.showAndWait();

      if (!result.isPresent()) {
        return;
      }

      fileName = result.get().trim().isEmpty() ? null : result.get();
    }

    try {
      String savedFilePath = controller.saveGame(fileName);
      showSaveSuccessMessage(savedFilePath);
      menuStage.close();
    } catch (IOException ex) {
      showSaveErrorMessage(ex.getMessage());
    }
  }

  private TextInputDialog createSaveDialog() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Save Mission");

    // Apply space theme to dialog
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    // Style the text field
    dialog.getEditor().getStyleClass().add("space-text-field");

    dialog.setHeaderText("ENTER MISSION NAME:");
    dialog.setContentText("Designation:");

    // Add shake animations to buttons
    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

    okButton.setText("SAVE MISSION");
    cancelButton.setText("ABORT");

    // Add shake animation to buttons
    okButton.setOnMouseEntered(e -> {
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), okButton);
      shake.setFromX(-2);
      shake.setToX(2);
      shake.setCycleCount(4);
      shake.setAutoReverse(true);
      shake.play();
    });

    cancelButton.setOnMouseEntered(e -> {
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), cancelButton);
      shake.setFromX(-2);
      shake.setToX(2);
      shake.setCycleCount(4);
      shake.setAutoReverse(true);
      shake.play();
    });

    return dialog;
  }

  private void showSaveSuccessMessage(String savedFilePath) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Mission Saved");

    // Apply space theme to alert
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    alert.setHeaderText(null);
    alert.setContentText("Game saved successfully to: " + savedFilePath);
    alert.showAndWait();
  }

  private void showSaveErrorMessage(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Save Failed");

    // Apply space theme to alert
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    alert.setHeaderText(null);
    alert.setContentText("Failed to save game: " + errorMessage);
    alert.showAndWait();
  }

  private Button createMainMenuButton() {
    Button button = css.createSpaceButton("Return to Base");
    button.setOnAction(e -> {
      menuStage.close();
      controller.returnToMainMenu();
    });
    return button;
  }

  private Button createExitButton() {
    Button button = css.createSpaceButton("Abort Mission");
    button.setOnAction(e -> controller.exitGame());
    return button;
  }

  private void setupScene() {
    Scene scene = new Scene(this, 400, 500);
    css.applyDefaultStylesheet(scene);
    menuStage.setScene(scene);
  }

  public void show() {
    menuStage.show();
  }
}