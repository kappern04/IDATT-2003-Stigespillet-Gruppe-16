package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.other.MainMenuController;
import edu.ntnu.iir.bidata.file.BoardRegistry;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainMenu {
  private MainMenuController controller;
  private Stage primaryStage;
  private CSS css;

  public MainMenu(MainMenuController controller, Stage primaryStage) {
    this.controller = controller;
    this.primaryStage = primaryStage;
    this.css = new CSS();
  }

  public void showMainMenu() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    Text title = createAnimatedTitle("COSMIC LADDER", 36);

    VBox menuOptions = new VBox(30,
            title,
            createNewGameButton(),
            createLoadGameButton(),
            createQuitButton()
    );
    menuOptions.setAlignment(Pos.CENTER);
    root.setCenter(menuOptions);

    Scene scene = new Scene(root, 700, 500);
    css.applyDefaultStylesheet(scene);
    primaryStage.setTitle("Cosmic Ladder Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void showGameSetup() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    Label boardLabel = new Label("SELECT GALAXY:");
    boardLabel.getStyleClass().add("space-label");

    ComboBox<String> boardSelector = new ComboBox<>();
    boardSelector.getItems().addAll(BoardRegistry.getInstance().getBoardNames());
    boardSelector.setValue(boardSelector.getItems().isEmpty() ? "" : boardSelector.getItems().get(0));
    boardSelector.getStyleClass().add("space-combo-box");

    Label playerLabel = new Label("SPACE TRAVELERS:");
    playerLabel.getStyleClass().add("space-label");

    Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
    playerSpinner.getStyleClass().add("space-spinner");
    playerSpinner.getStyleClass().add("split-arrows-horizontal");

    Button startBtn = createLaunchMissionButton(boardSelector, playerSpinner);
    Button backBtn = createReturnToBaseButton();

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

  private List<String> collectPlayerNames(int numPlayers) {
    Dialog<List<String>> dialog = new Dialog<>();
    dialog.setTitle("Space Traveler Identification");

    // Apply space theme to the dialog pane
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    // Set up a custom header with space styling
    Label headerLabel = css.createStyledLabel(
            "ENTER NAMES FOR YOUR " + numPlayers + " SPACE TRAVELERS",
            FontWeight.BOLD,
            16,
            css.getSpaceBlue());

    // Add glow effect to the header
    Glow glow = new Glow(0.5);
    headerLabel.setEffect(glow);

    VBox headerBox = new VBox(10, headerLabel);
    headerBox.setAlignment(Pos.CENTER);
    dialogPane.setHeader(headerBox);

    // Content grid
    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setPadding(new Insets(20));
    grid.setAlignment(Pos.CENTER);
    grid.getStyleClass().add("space-dialog-grid");

    TextField[] nameFields = new TextField[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      Label label = css.createStyledLabel("TRAVELER " + (i + 1) + ":", FontWeight.NORMAL, 14, Color.WHITE);

      nameFields[i] = new TextField("Traveler " + (i + 1));
      nameFields[i].getStyleClass().add("space-text-field");
      nameFields[i].setPrefWidth(250);

      grid.add(label, 0, i);
      grid.add(nameFields[i], 1, i);
    }

    dialog.getDialogPane().setContent(grid);

    // Custom buttons
    ButtonType confirmButtonType = new ButtonType("LAUNCH MISSION", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("ABORT", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

    // Style buttons - only add shake animations
    Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
    Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);

    confirmButton.setOnMouseEntered(e -> {
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), confirmButton);
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

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirmButtonType) {
        List<String> result = new ArrayList<>();
        for (TextField field : nameFields) {
          String name = field.getText().trim();
          result.add(name.isEmpty() ? "Anonymous Traveler" : name);
        }
        return result;
      }
      return null;
    });

    Optional<List<String>> result = dialog.showAndWait();
    return result.orElse(null);
  }

  private void showLoadGameDialog() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Saved Mission");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    );
    fileChooser.setInitialDirectory(new File("saves"));

    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile != null) {
      controller.loadGame(selectedFile);
    }
  }

  private void setupSpaceBackground(Pane root) {
    root.setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));
  }

  // --- Button and Title Factory Methods ---

  private Text createAnimatedTitle(String text, int fontSize) {
    Text title = new Text(text);
    title.setFont(css.getOrbitronFont(fontSize, FontWeight.BOLD));
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

  private Button createNewGameButton() {
    Button newGameBtn = css.createSpaceButton("New Mission");
    newGameBtn.setOnAction(e -> showGameSetup());
    return newGameBtn;
  }

  private Button createLoadGameButton() {
    Button loadGameBtn = css.createSpaceButton("Load Mission");
    loadGameBtn.setOnAction(e -> showLoadGameDialog());
    return loadGameBtn;
  }

  private Button createQuitButton() {
    Button quitBtn = css.createSpaceButton("Abort");
    quitBtn.setOnAction(e -> Platform.exit());
    return quitBtn;
  }

  private Button createLaunchMissionButton(ComboBox<String> boardSelector, Spinner<Integer> playerSpinner) {
    Button startBtn = css.createSpaceButton("Launch Mission");
    startBtn.setOnAction(e -> {
      String selectedBoard = boardSelector.getValue();
      int numPlayers = playerSpinner.getValue();
      List<String> playerNames = collectPlayerNames(numPlayers);
      if (playerNames != null) {
        controller.startNewGame(selectedBoard, numPlayers, playerNames);
      }
    });
    return startBtn;
  }

  private Button createReturnToBaseButton() {
    Button backBtn = css.createSpaceButton("Return to Base");
    backBtn.setOnAction(e -> showMainMenu());
    return backBtn;
  }
}