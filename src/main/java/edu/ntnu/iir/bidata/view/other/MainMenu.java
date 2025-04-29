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

    Text title = new Text("COSMIC LADDER");
    title.setFont(css.getOrbitronFont(36, FontWeight.BOLD));
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

    Button newGameBtn = css.createSpaceButton("New Mission");
    Button loadGameBtn = css.createSpaceButton("Load Mission");
    Button quitBtn = css.createSpaceButton("Abort");

    newGameBtn.setOnAction(e -> showGameSetup());
    loadGameBtn.setOnAction(e -> showLoadGameDialog());
    quitBtn.setOnAction(e -> Platform.exit());

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

    Label boardLabel = new Label("SELECT GALAXY:");
    boardLabel.setTextFill(css.getSpaceBlue());
    boardLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));

    ComboBox<String> boardSelector = new ComboBox<>();
    boardSelector.getItems().addAll(BoardRegistry.getInstance().getBoardNames());
    boardSelector.setValue(boardSelector.getItems().isEmpty() ? "" : boardSelector.getItems().get(0));
    boardSelector.getStyleClass().add("space-combo-box");

    Label playerLabel = new Label("SPACE TRAVELERS:");
    playerLabel.setTextFill(css.getSpaceBlue());
    playerLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));

    Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
    playerSpinner.getStyleClass().add("space-spinner");

    Button startBtn = css.createSpaceButton("Launch Mission");
    Button backBtn = css.createSpaceButton("Return to Base");

    startBtn.setOnAction(e -> {
      String selectedBoard = boardSelector.getValue();
      int numPlayers = playerSpinner.getValue();
      List<String> playerNames = collectPlayerNames(numPlayers);
      if (playerNames != null) {
        controller.startNewGame(selectedBoard, numPlayers, playerNames);
      }
    });

    backBtn.setOnAction(e -> showMainMenu());

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

  private List<String> collectPlayerNames(int numPlayers) {
    List<String> names = new ArrayList<>();
    Dialog<List<String>> dialog = new Dialog<>();
    dialog.setTitle("Space Traveler Identification");
    dialog.setHeaderText("Enter names for your " + numPlayers + " space travelers:");

    ButtonType confirmButtonType = new ButtonType("Launch", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

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
}