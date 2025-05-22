package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {
  // Constants for better maintainability
  private static final String BACKGROUND_PATH = "/image/background/mainmenu.png";
  private static final String GAME_TITLE = "COSMIC LADDER";
  private static final String GAME_SUBTITLE = "SPACE ADVENTURE";
  private static final int TITLE_FONT_SIZE = 36;
  private static final int SUBTITLE_FONT_SIZE = 16;
  private static final int BUTTON_SPACING = 20;
  private static final int CONTENT_SPACING = 30;

  private final MainMenuController controller;
  private final Stage primaryStage;
  private final CSS css;
  private Timeline titleAnimation;

  public MainMenu(MainMenuController controller, Stage primaryStage) {
    this.controller = controller;
    this.primaryStage = primaryStage;
    this.css = new CSS();
  }

  public void showMainMenu() {
    BorderPane root = new BorderPane();
    setupSpaceBackground(root);

    // Title section with main title and subtitle
    Text title = createAnimatedTitle(GAME_TITLE, TITLE_FONT_SIZE);
    Text subtitle = createSubtitle(GAME_SUBTITLE, SUBTITLE_FONT_SIZE);

    VBox titleSection = new VBox(10, title, subtitle);
    titleSection.setAlignment(Pos.CENTER);
    titleSection.setPadding(new Insets(30, 0, 0, 0));

    // Button section with menu options
    VBox buttonSection = new VBox(BUTTON_SPACING,
            createNewGameButton(),
            createLoadGameButton(),
            createQuitButton()
    );
    buttonSection.setAlignment(Pos.CENTER);
    buttonSection.setPadding(new Insets(30, 0, 30, 0));

    // Combined content
    VBox menuContent = new VBox(CONTENT_SPACING, titleSection, buttonSection);
    menuContent.setAlignment(Pos.CENTER);

    root.setCenter(menuContent);

    Scene scene = new Scene(root, 700, 600);
    css.applyDefaultStylesheet(scene);
    setupKeyboardShortcuts(scene);

    primaryStage.setTitle("Cosmic Ladder Game");
    primaryStage.setScene(scene);
    primaryStage.setOnHidden(e -> stopAnimations());
    primaryStage.show();
  }

  private void setupSpaceBackground(Pane root) {
    root.setBackground(css.createSpaceBackground(BACKGROUND_PATH));
  }

  private Text createAnimatedTitle(String text, int fontSize) {
    Text title = new Text(text);
    title.setFont(css.getOrbitronFont(fontSize, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
    title.setEffect(glow);

    titleAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    titleAnimation.setCycleCount(Animation.INDEFINITE);
    titleAnimation.setAutoReverse(true);
    titleAnimation.play();

    return title;
  }

  private Text createSubtitle(String text, int fontSize) {
    Text subtitle = new Text(text);
    subtitle.setFont(css.getOrbitronFont(fontSize, FontWeight.NORMAL));
    subtitle.setFill(css.getSpaceBlue().deriveColor(0, 1, 1, 0.7));
    return subtitle;
  }

  private Button createNewGameButton() {
    Button newGameBtn = css.createSpaceButton("New Mission");
    newGameBtn.setMaxWidth(200);
    newGameBtn.setOnAction(e -> new GameSetupMenu(controller, primaryStage).showGameSetup());

    Tooltip tooltip = new Tooltip("Start a new space adventure (N)");
    tooltip.setShowDelay(Duration.millis(500));
    Tooltip.install(newGameBtn, tooltip);

    return newGameBtn;
  }

  private Button createLoadGameButton() {
    Button loadGameBtn = css.createSpaceButton("Load Mission");
    loadGameBtn.setMaxWidth(200);
    loadGameBtn.setOnAction(e -> new GameSetupMenu(controller, primaryStage).showLoadGameDialog());

    Tooltip tooltip = new Tooltip("Continue a previous mission (L)");
    tooltip.setShowDelay(Duration.millis(500));
    Tooltip.install(loadGameBtn, tooltip);

    return loadGameBtn;
  }

  private Button createQuitButton() {
    Button quitBtn = css.createSpaceButton("Abort Mission");
    quitBtn.setMaxWidth(200);
    quitBtn.setOnAction(e -> Platform.exit());

    Tooltip tooltip = new Tooltip("Exit the game (Q)");
    tooltip.setShowDelay(Duration.millis(500));
    Tooltip.install(quitBtn, tooltip);

    return quitBtn;
  }

  private void setupKeyboardShortcuts(Scene scene) {
    scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.N),
            () -> new GameSetupMenu(controller, primaryStage).showGameSetup()
    );

    scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.L),
            () -> new GameSetupMenu(controller, primaryStage).showLoadGameDialog()
    );

    scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.Q),
            Platform::exit
    );
  }

  private void stopAnimations() {
    if (titleAnimation != null) {
      titleAnimation.stop();
    }
  }
}