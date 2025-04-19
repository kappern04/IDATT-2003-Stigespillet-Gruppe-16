package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.file.BoardGameFactory;
import edu.ntnu.iir.bidata.file.GameSaveReaderCSV;
import edu.ntnu.iir.bidata.view.elements.CSS;
import java.io.File;
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
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {

  private Stage primaryStage;
  private CSS css;

  public MainMenu(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.css = new CSS();
    showMainMenu();
  }

  private void showMainMenu() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    // Title
    Text title = new Text("COSMIC LADDER");
    title.setFont(css.getOrbitronFont(36, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

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
    Button newGameBtn = css.createSpaceButton("New Mission");
    Button loadGameBtn = css.createSpaceButton("Load Mission");
    Button quitBtn = css.createSpaceButton("Abort");

    // Button actions
    newGameBtn.setOnAction(e -> showGameSetup());
    loadGameBtn.setOnAction(e -> showLoadGameDialog());
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
    boardLabel.setTextFill(css.getSpaceBlue());
    boardLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));

    ComboBox<String> boardSelector = new ComboBox<>();
    boardSelector.getItems().addAll("Spiral Way", "Ladderia Prime");
    boardSelector.setValue("Spiral Way");
    boardSelector.getStyleClass().add("space-combo-box");

    // Player selection
    Label playerLabel = new Label("SPACE TRAVELERS:");
    playerLabel.setTextFill(css.getSpaceBlue());
    playerLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));

    Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
    playerSpinner.getStyleClass().add("space-spinner");

    // Buttons
    Button startBtn = css.createSpaceButton("Launch Mission");
    Button backBtn = css.createSpaceButton("Return to Base");

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
    // Collect player names
    List<String> playerNames = collectPlayerNames(numPlayers);

    // Create Player objects
    Player[] players = new Player[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      players[i] = new Player(playerNames.get(i));
    }

    // Create a new BoardGame instance
    BoardGame game = new BoardGame();

    // Set the players in the game
    game.setPlayers(players);

    // Load the selected board
    Board board;
    try {
      switch (boardType) {
        case "Andromeda":
          board = loadBoardFromResource("/boards/andromeda.json");
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
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Board Loading Error");
      alert.setHeaderText(null);
      alert.setContentText("Could not load board: " + e.getMessage());
      alert.showAndWait();
      board = new Board(); // Fallback to default board
    }

    // Set the board in the game
    game.setBoard(board);

    // Create MainView and set it up - no need to pass board name separately
    MainView mainView = new MainView(game);
    mainView.setUpStage(primaryStage);
  }

  public List<String> collectPlayerNames(int numPlayers) {
    List<String> names = new ArrayList<>();

    // Create a dialog pane for player names
    Dialog<List<String>> dialog = new Dialog<>();
    dialog.setTitle("Space Traveler Identification");
    dialog.setHeaderText("Enter names for your " + numPlayers + " space travelers:");

    // Set the button types
    ButtonType confirmButtonType = new ButtonType("Launch", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

    // Create a grid for the name fields
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // Create text fields for each player's name
    TextField[] nameFields = new TextField[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      Label label = new Label("Traveler " + (i + 1) + ":");
      label.setTextFill(css.getSpaceBlue());
      label.setFont(css.getOrbitronFont(12, FontWeight.BOLD));

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
      return null;
    });
  }

  private void setupSpaceBackground(Pane root) {
    root.setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));
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

  private void showLoadGameDialog() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Saved Mission");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    );
    fileChooser.setInitialDirectory(new File("saves"));

    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile != null) {
      try {
        // Load the game
        GameSaveReaderCSV saveReader = new GameSaveReaderCSV();
        BoardGame loadedGame = saveReader.loadGame(selectedFile.getAbsolutePath());

        // Create MainView with the loaded game
        MainView mainView = new MainView(loadedGame);
        mainView.setUpStage(primaryStage);
      } catch (IOException ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Load Failed");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load game: " + ex.getMessage());
        alert.showAndWait();
      }
    }
  }
}