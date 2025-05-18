package edu.ntnu.iir.bidata.view.menu;

import edu.ntnu.iir.bidata.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {
  private final MainMenuController controller;
  private final Stage primaryStage;
  private final CSS css;

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

  private void setupSpaceBackground(Pane root) {
    root.setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));
  }

  private Text createAnimatedTitle(String text, int fontSize) {
    Text title = new Text(text);
    title.setFont(css.getOrbitronFont(fontSize, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
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

  private javafx.scene.control.Button createNewGameButton() {
    javafx.scene.control.Button newGameBtn = css.createSpaceButton("New Mission");
    newGameBtn.setOnAction(e -> new GameSetupMenu(controller, primaryStage).showGameSetup());
    return newGameBtn;
  }

  private javafx.scene.control.Button createLoadGameButton() {
    javafx.scene.control.Button loadGameBtn = css.createSpaceButton("Load Mission");
    loadGameBtn.setOnAction(e -> new GameSetupMenu(controller, primaryStage).showLoadGameDialog());
    return loadGameBtn;
  }

  private javafx.scene.control.Button createQuitButton() {
    javafx.scene.control.Button quitBtn = css.createSpaceButton("Abort");
    quitBtn.setOnAction(e -> Platform.exit());
    return quitBtn;
  }
}