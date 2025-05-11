package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.other.MainMenuController;
import edu.ntnu.iir.bidata.file.BoardRegistry;
import edu.ntnu.iir.bidata.view.util.PlayerData;
import edu.ntnu.iir.bidata.file.SaveFileTracker;
import edu.ntnu.iir.bidata.view.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.view.util.ShipUtils;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

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

  private List<PlayerData> collectPlayerDetails(int numPlayers) {
    Dialog<List<PlayerData>> dialog = new Dialog<>();
    dialog.setTitle("Space Traveler Identification");

    // Apply space theme to the dialog pane
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    dialogPane.getStyleClass().add("space-dialog-pane");

    Label headerLabel = css.createStyledLabel(
            "ENTER DETAILS FOR YOUR " + numPlayers + " SPACE TRAVELERS",
            FontWeight.BOLD,
            16,
            css.getSpaceBlue());

    Glow glow = new Glow(0.5);
    headerLabel.setEffect(glow);

    VBox headerBox = new VBox(10, headerLabel);
    headerBox.setAlignment(Pos.CENTER);
    dialogPane.setHeader(headerBox);

    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setPadding(new Insets(20));
    grid.setAlignment(Pos.CENTER);
    grid.getStyleClass().add("space-dialog-grid");

    // Add column headers
    Label nameHeader = css.createStyledLabel("NAME:", FontWeight.NORMAL, 14, Color.WHITE);
    Label colorHeader = css.createStyledLabel("COLOR:", FontWeight.NORMAL, 14, Color.WHITE);
    Label shipHeader = css.createStyledLabel("SHIP:", FontWeight.NORMAL, 14, Color.WHITE);
    grid.add(nameHeader, 1, 0);
    grid.add(colorHeader, 2, 0);
    grid.add(shipHeader, 3, 0);

    TextField[] nameFields = new TextField[numPlayers];
    ColorPicker[] colorPickers = new ColorPicker[numPlayers];
    Button[] shipButtons = new Button[numPlayers];
    int[] selectedShips = new int[numPlayers]; // Track selected ship (1-5)
    Color[] defaultColors = {Color.RED, Color.BLUE, Color.PURPLE, Color.ORANGE};

    for (int i = 0; i < numPlayers; i++) {
      Label label = css.createStyledLabel("TRAVELER " + (i + 1) + ":", FontWeight.NORMAL, 14, Color.WHITE);

      nameFields[i] = new TextField("Traveler " + (i + 1));
      nameFields[i].getStyleClass().add("space-text-field");
      nameFields[i].setPrefWidth(200);

      colorPickers[i] = new ColorPicker(defaultColors[i % defaultColors.length]);
      colorPickers[i].getStyleClass().add("space-color-picker");
      colorPickers[i].setStyle("-fx-color-label-visible: false;");

      // Ship selector button with initial ship (Ship_1)
      selectedShips[i] = 1; // Start with Ship_1
      int playerIndex = i; // Capture for lambda

      shipButtons[i] = createShipSelectorButton(selectedShips, playerIndex, colorPickers);

      // Update button when color changes
      colorPickers[i].valueProperty().addListener((obs, oldVal, newVal) ->
              updateShipButtonImage(shipButtons[playerIndex], selectedShips[playerIndex], newVal));

      grid.add(label, 0, i + 1);
      grid.add(nameFields[i], 1, i + 1);
      grid.add(colorPickers[i], 2, i + 1);
      grid.add(shipButtons[i], 3, i + 1);
    }

    dialog.getDialogPane().setContent(grid);

    ButtonType cancelButtonType = new ButtonType("ABORT", ButtonBar.ButtonData.CANCEL_CLOSE);
    ButtonType confirmButtonType = new ButtonType("LAUNCH MISSION", ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, confirmButtonType);



    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirmButtonType) {
        List<PlayerData> result = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
          String name = nameFields[i].getText().trim();
          if (name.isEmpty()) name = "Anonymous Traveler";
          Color color = colorPickers[i].getValue();
          result.add(new PlayerData(name, color, selectedShips[i]));
        }
        return result;
      }
      return null;
    });

    Optional<List<PlayerData>> result = dialog.showAndWait();
    return result.orElse(null);
  }

  private Button createShipSelectorButton(int[] selectedShips, int playerIndex, ColorPicker[] colorPickers) {
    Button button = new Button();
    button.setPrefSize(48, 48);
    button.getStyleClass().add("ship-selector-button");

    // Set initial ship preview image using ShipUtils
    updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());

    // Add click handler to cycle through ships
    button.setOnAction(e -> {
      selectedShips[playerIndex] = (selectedShips[playerIndex] % 5) + 1;
      updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());
    });

    return button;
  }

  private void updateShipButtonImage(Button button, int shipType, Color color) {
    try {
      // Use ShipUtils to create the colored ship image
      Image baseImage = ShipUtils.loadShipSprite(shipType);
      ImageView shipView = ShipUtils.createColoredShipImage(color, baseImage);

      // Use our enhanced PixelArtUpscaler to resize the image
      int targetSize = 48;
      ImageView upscaledView = PixelArtUpscaler.resizeImage(
              shipView.getImage(),
              targetSize,
              targetSize
      );

      // Apply the same effect as the original ship view
      upscaledView.setEffect(shipView.getEffect());

      button.setGraphic(upscaledView);
      button.setText("");
    } catch (Exception ex) {
      button.setText("Ship " + shipType);
    }
  }

  private void showLoadGameDialog() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Saved Mission");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    );

    // Use the standardized save directory path
    String savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
    File savesDir = new File(savesDirectory);

    // If directory exists, use it as initial directory
    if (savesDir.exists() && savesDir.isDirectory()) {
      fileChooser.setInitialDirectory(savesDir);

      // If there's a current save file, preselect it
      if (SaveFileTracker.getInstance().wasLoadedFromSave()) {
        String currentFileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
        if (currentFileName != null) {
          File initialFile = new File(savesDir, currentFileName);
          if (initialFile.exists()) {
            fileChooser.setInitialFileName(currentFileName);
          }
        }
      }
    }

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
      List<PlayerData> playerDetails = collectPlayerDetails(numPlayers);
      if (playerDetails != null) {
        controller.startNewGame(selectedBoard, numPlayers, playerDetails);
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