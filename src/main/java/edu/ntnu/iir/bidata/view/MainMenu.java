package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Player;
import edu.ntnu.iir.bidata.object.file.BoardGameFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {

  private Stage primaryStage;
  private Font orbitronFont;
  private final Color SPACE_BLUE = Color.rgb(64, 224, 208);
  private final Color SPACE_PURPLE = Color.rgb(138, 43, 226);

  public MainMenu(Stage primaryStage) {
    this.primaryStage = primaryStage;
    loadCustomFont();
    showMainMenu();
  }

  private void loadCustomFont() {
    try {
      InputStream is = getClass().getResourceAsStream("/font/Orbitron-VariableFont_wght.ttf");
      orbitronFont = Font.loadFont(is, 12); // Load with a default size
      if (orbitronFont == null) {
        System.err.println("Failed to load Orbitron font. Using system font.");
        orbitronFont = Font.font("Arial"); // Fallback
      } else {
        System.out.println("Orbitron font loaded successfully!");
      }
    } catch (Exception e) {
      System.err.println("Error loading font: " + e.getMessage());
      orbitronFont = Font.font("Arial"); // Fallback
    }
  }

  // Then modify all Font.font(SPACE_FONT, ...) usage with custom sized versions:
  private Font getOrbitronFont(double size, FontWeight weight) {
    return Font.font(orbitronFont.getFamily(), weight, size);
  }

  private void showMainMenu() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    // Title
    Text title = new Text("COSMIC LADDER");
    title.setFont(getOrbitronFont(36, FontWeight.BOLD));
    title.setFill(SPACE_BLUE);

    // Add glow effect to title
    Glow glow = new Glow(0.8);
    title.setEffect(glow);

    // Create pulse animation for title
    Timeline pulse = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
        new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulse.setCycleCount(Animation.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    // Menu buttons
    Button newGameBtn = createSpaceButton("New Mission");
    Button loadGameBtn = createSpaceButton("Load Mission");
    Button quitBtn = createSpaceButton("Abort");

    // Button actions
    newGameBtn.setOnAction(e -> showGameSetup());

    loadGameBtn.setOnAction(e -> {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Transmission Interrupted");
      alert.setHeaderText(null);
      alert.setContentText("This feature will be available in a future update!");
      alert.showAndWait();
    });

    quitBtn.setOnAction(e -> Platform.exit());

    // Layout
    VBox menuOptions = new VBox(30, title, newGameBtn, loadGameBtn, quitBtn);
    menuOptions.setAlignment(Pos.CENTER);
    root.setCenter(menuOptions);

    Scene scene = new Scene(root, 700, 500);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    primaryStage.setTitle("Cosmic Ladder Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void showGameSetup() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    // Board selection
    Label boardLabel = new Label("SELECT GALAXY:");
    boardLabel.setTextFill(SPACE_BLUE);
    boardLabel.setFont(getOrbitronFont(16, FontWeight.BOLD));

    ComboBox<String> boardSelector = new ComboBox<>();
    boardSelector.getItems().addAll("Spiral Way", "Ladderia Prime");
    boardSelector.setValue("Spiral Way");
    boardSelector.getStyleClass().add("space-combo-box");

    // Player selection
    Label playerLabel = new Label("SPACE TRAVELERS:");
    playerLabel.setTextFill(SPACE_BLUE);
    playerLabel.setFont(getOrbitronFont(16, FontWeight.BOLD));

    Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
    playerSpinner.getStyleClass().add("space-spinner");

    // Buttons
    Button startBtn = createSpaceButton("Launch Mission");
    Button backBtn = createSpaceButton("Return to Base");

    startBtn.setOnAction(e -> {
      String selectedBoard = boardSelector.getValue();
      int numPlayers = playerSpinner.getValue();
      startNewGame(selectedBoard, numPlayers);
    });

    backBtn.setOnAction(e -> showMainMenu());

    // Layout
    VBox settingsBox = new VBox(20,
        boardLabel, boardSelector,
        playerLabel, playerSpinner,
        startBtn, backBtn);
    settingsBox.setAlignment(Pos.CENTER);
    settingsBox.setSpacing(25);
    root.setCenter(settingsBox);

    Scene scene = new Scene(root, 700, 500);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  private void startNewGame(String boardType, int numPlayers) {
    // Create dialog to collect player names
    List<String> playerNames = collectPlayerNames(numPlayers);

    Player[] players = new Player[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      players[i] = new Player(playerNames.get(i));
    }
    BoardGame game = new BoardGame();

    // Set the players in the game
    game.setPlayers(players);

    // Create board based on selection
    // Create board based on selection
    Board board;
    try {
      switch (boardType) {
        case "Andromeda":
          board = loadBoardFromResource("/boards/andromeda.json"); // Assuming this exists
          break;
        case "Ladderia Prime":
          board = loadBoardFromResource("/boards/normal.json");
          break;
        case "Spiral Way":
          board = loadBoardFromResource("/boards/spiral.json");
          break;
        default:
          board = new Board(); // Fallback to default board
          break;
      }
    } catch (RuntimeException e) {
      // Handle any loading errors
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Board Loading Error");
      alert.setHeaderText(null);
      alert.setContentText("Could not load board: " + e.getMessage());
      alert.showAndWait();
      board = new Board(); // Fallback to default board
    }

    game.setBoard(board);

    // Create MainView and set it up
    MainView mainView = new MainView(game);
    mainView.setUpStage(primaryStage);
    primaryStage.setTitle("Cosmic Ladder - " + boardType);
  }

  private List<String> collectPlayerNames(int numPlayers) {
    List<String> names = new ArrayList<>();

    // Create a dialog pane for player names
    Dialog<List<String>> dialog = new Dialog<>();
    dialog.setTitle("Space Traveler Identification");
    dialog.setHeaderText("Enter names for your " + numPlayers + " space travelers:");

    // Set the button types
    ButtonType confirmButtonType = new ButtonType("Launch", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

    // Create a grid for the name fields
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // Create text fields for each player's name
    TextField[] nameFields = new TextField[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      Label label = new Label("Traveler " + (i + 1) + ":");
      label.setTextFill(SPACE_BLUE);
      label.setFont(getOrbitronFont(12, FontWeight.BOLD));

      nameFields[i] = new TextField("Traveler " + (i + 1));
      nameFields[i].setPrefWidth(200);

      grid.add(label, 0, i);
      grid.add(nameFields[i], 1, i);
    }

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getStylesheets()
        .add(getClass().getResource("/css/space-theme.css").toExternalForm());

    // Convert the result to list of names when the confirm button is clicked
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

    // Show the dialog and process the result
    Optional<List<String>> result = dialog.showAndWait();

    return result.orElseGet(() -> {
      // If dialog was cancelled, generate default names
      List<String> defaultNames = new ArrayList<>();
      for (int i = 0; i < numPlayers; i++) {
        defaultNames.add("Traveler " + (i + 1));
      }
      return defaultNames;
    });

    // TODO: Implement actual game start with views
  }

  private Button createSpaceButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(250);
    button.setPrefHeight(50);
    button.setFont(getOrbitronFont(16, FontWeight.BOLD));
    button.setTextFill(Color.WHITE);

    // Apply CSS styling
    button.getStyleClass().add("space-button");

    // Add glow effect
    DropShadow shadow = new DropShadow();
    shadow.setColor(SPACE_BLUE);
    button.setEffect(shadow);

    // Add hover shake animation
    button.setOnMouseEntered(e -> {
      // Create shaking animation
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
      shake.setFromX(-3);
      shake.setToX(3);
      shake.setCycleCount(6);
      shake.setAutoReverse(true);
      shake.play();

      // Increase glow effect
      shadow.setRadius(20);
      shadow.setColor(SPACE_PURPLE);
    });

    button.setOnMouseExited(e -> {
      // Reset effects
      shadow.setRadius(10);
      shadow.setColor(SPACE_BLUE);
    });

    return button;
  }

  private void setupSpaceBackground(Pane root) {
    // Set background with stars
    BackgroundImage bgImage = new BackgroundImage(
        new Image(getClass().getResourceAsStream("/image/mainmenu.png")),
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

    root.setBackground(new Background(bgImage));
  }

  private Board loadBoardFromResource(String resourcePath) {
    try {
      InputStream inputStream = getClass().getResourceAsStream(resourcePath);
      if (inputStream == null) {
        throw new IOException("Resource not found: " + resourcePath);
      }
      return new BoardGameFactory().createBoardGameFromStream(inputStream).getBoard();
    } catch (Exception e) {
      throw new RuntimeException("Failed to load board from resource: " + resourcePath, e);
    }
  }
}