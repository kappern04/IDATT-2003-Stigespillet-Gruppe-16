package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.controller.menu.InGameMenuController;
import edu.ntnu.iir.bidata.laddergame.file.SaveFileTracker;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InGameMenu extends VBox {
  private static final Logger LOGGER = Logger.getLogger(InGameMenu.class.getName());

  private static final int MENU_WIDTH = 400;
  private static final int MENU_HEIGHT = 500;
  private static final int SPACING = 30;
  private static final int PADDING = 40;
  private static final String BACKGROUND_PATH = "/image/background/mainmenu.png";
  private static final String STYLESHEET_PATH = "/css/space-theme.css";
  private static final String TITLE_TEXT = "MISSION CONTROL";

  private static final String RESUME_TEXT = "Resume Mission";
  private static final String SAVE_TEXT = "Save Mission";
  private static final String MAIN_MENU_TEXT = "Return to Base";
  private static final String EXIT_TEXT = "Abort Mission";
  private static final String SAVE_DIALOG_TITLE = "Save Mission";
  private static final String SAVE_DIALOG_HEADER = "ENTER MISSION NAME:";
  private static final String SAVE_DIALOG_CONTENT = "Designation:";
  private static final String SAVE_BUTTON_TEXT = "SAVE MISSION";
  private static final String CANCEL_BUTTON_TEXT = "ABORT";

  private final InGameMenuController controller;
  private final Stage menuStage;
  private final CSS css;

  public InGameMenu(InGameMenuController controller) {
    this.controller = Objects.requireNonNull(controller, "Controller cannot be null");
    this.css = new CSS();
    this.menuStage = new Stage();
    initializeMenu();
  }

  public void show() {
    centerOnScreen();
    menuStage.show();
    Platform.runLater(() -> {
      if (getChildren().size() > 1 && getChildren().get(1) instanceof Button button) {
        button.requestFocus();
      }
    });
  }

  private void initializeMenu() {
    configureStage();
    configureLayout();
    populateMenu();
    setupBackground();
    setupScene();
    setupKeyboardShortcuts();
  }

  private void configureStage() {
    menuStage.initModality(Modality.APPLICATION_MODAL);
    menuStage.initStyle(StageStyle.UNDECORATED);
    menuStage.setOnShown(e -> {
      Scene scene = menuStage.getScene();
      scene.setFill(Color.TRANSPARENT);
      scene.getRoot().setEffect(new DropShadow(20, Color.BLACK));
    });
  }

  private void configureLayout() {
    setSpacing(SPACING);
    setAlignment(Pos.CENTER);
    setPadding(new Insets(PADDING));
    setId("in-game-menu");
  }

  private Button createMenuButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
    Button button = css.createSpaceButton(text);
    button.setOnAction(action);
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
  }

  private void populateMenu() {
    Text title = createAnimatedTitle();
    Button resumeButton = createMenuButton(RESUME_TEXT, e -> menuStage.close());
    Button mainMenuButton = createMenuButton(MAIN_MENU_TEXT, e -> handleMainMenuAction());
    Button saveButton = createMenuButton(SAVE_TEXT, e -> handleSaveAction());
    Button exitButton = createMenuButton(EXIT_TEXT, e -> handleExitAction());

    getChildren().addAll(title, resumeButton, mainMenuButton, saveButton, exitButton);
  }

  private void setupBackground() {
    setBackground(css.createSpaceBackground(BACKGROUND_PATH));
  }

  private void setupScene() {
    Scene scene = new Scene(this, MENU_WIDTH, MENU_HEIGHT);
    scene.setFill(Color.TRANSPARENT);
    css.applyDefaultStylesheet(scene);
    menuStage.setScene(scene);
  }

  private void setupKeyboardShortcuts() {
    Scene scene = menuStage.getScene();
    scene.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        menuStage.close();
      }
    });
  }

  private void centerOnScreen() {
    double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    menuStage.setX((screenWidth - MENU_WIDTH) / 2);
    menuStage.setY((screenHeight - MENU_HEIGHT) / 2);
  }

  private Text createAnimatedTitle() {
    Text title = new Text(TITLE_TEXT);
    title.setFont(css.getOrbitronFont(24, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    return title;
  }


  private void handleMainMenuAction() {
    Alert confirmDialog = createSpaceThemedAlert(
            Alert.AlertType.CONFIRMATION,
            "Confirm Return",
            null,
            "Return to main menu? Mission progress since last save will be lost."
    );
    confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    Button yesButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.YES);
    Button noButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.NO);
    yesButton.setText("CONFIRM");
    noButton.setText("CANCEL");

    Optional<ButtonType> result = confirmDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.YES) {
      menuStage.close();
      controller.returnToMainMenu();
    }
  }

  private void handleSaveAction() {
    String fileName = null;
    if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
      TextInputDialog dialog = createSaveDialog();
      Optional<String> result = dialog.showAndWait();

      if (result.isEmpty()) {
        return;
      }

      String inputName = result.get().trim();
      if (inputName.isEmpty()) {
        Alert alert = createSpaceThemedAlert(
                Alert.AlertType.WARNING,
                "Invalid Name",
                null,
                "Mission name cannot be empty. Save canceled."
        );
        alert.showAndWait();
        return;
      }

      fileName = inputName;
    }

    try {
      String savedFilePath = controller.saveGame(fileName);
      showSaveSuccessMessage(savedFilePath);
      menuStage.close();
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Failed to save game", ex);
      showSaveErrorMessage(ex.getMessage());
    }
  }

  private void handleExitAction() {
    Alert confirmDialog = createSpaceThemedAlert(
            Alert.AlertType.CONFIRMATION,
            "Confirm Exit",
            null,
            "Are you sure you want to exit the game? Unsaved progress will be lost."
    );
    confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    Button yesButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.YES);
    Button noButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.NO);
    yesButton.setText("CONFIRM");
    noButton.setText("CANCEL");

    Optional<ButtonType> result = confirmDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.YES) {
      controller.exitGame();
    }
  }

  private TextInputDialog createSaveDialog() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle(SAVE_DIALOG_TITLE);

    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    TextField editor = dialog.getEditor();
    editor.getStyleClass().add("space-text-field");

    dialog.setHeaderText(SAVE_DIALOG_HEADER);
    dialog.setContentText(SAVE_DIALOG_CONTENT);

    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

    okButton.setStyle("small-space-button");
    cancelButton.setStyle("small-space-button");

    okButton.setText(SAVE_BUTTON_TEXT);
    cancelButton.setText(CANCEL_BUTTON_TEXT);

    return dialog;
  }

  private Alert createSpaceThemedAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    return alert;
  }

  private void showSaveSuccessMessage(String savedFilePath) {
    Alert alert = createSpaceThemedAlert(
            Alert.AlertType.INFORMATION,
            "Mission Saved",
            null,
            "Game saved successfully to: " + savedFilePath
    );
    alert.showAndWait();
  }

  private void showSaveErrorMessage(String errorMessage) {
    Alert alert = createSpaceThemedAlert(
            Alert.AlertType.ERROR,
            "Save Failed",
            null,
            "Failed to save game: " + errorMessage
    );
    alert.showAndWait();
  }
}