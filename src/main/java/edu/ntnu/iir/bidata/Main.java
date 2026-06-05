package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.laddergame.Stigespillet;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application entry point and game launcher.
 *
 * <p>Shows a menu of the available games and starts the chosen one on the same
 * (primary) stage. To add another game, add a single entry to {@link #availableGames()} —
 * a display name paired with a factory for that game's {@link Application}.
 */
public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String STYLESHEET = "/css/space-theme.css";

    /**
     * The selectable games, in display order. Add new games here.
     *
     * @return map of display name to a factory that builds the game's application
     */
    private Map<String, Supplier<Application>> availableGames() {
        Map<String, Supplier<Application>> games = new LinkedHashMap<>();
        games.put("Cosmic Ladder", Stigespillet::new);
        // To add a game later, e.g.:
        // games.put("Click Game", ClickGameApp::new);
        return games;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setId("game-launcher");
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #121e3d, #0a0f1f); -fx-padding: 40;");

        Label title = new Label("SELECT A GAME");
        title.getStyleClass().add("styled-label");
        root.getChildren().add(title);

        availableGames().forEach((name, factory) -> {
            Button button = new Button(name);
            button.getStyleClass().add("space-button");
            button.setMaxWidth(240);
            button.setOnAction(e -> launchGame(factory, primaryStage));
            root.getChildren().add(button);
        });

        Scene scene = new Scene(root, 380, 320);
        var stylesheet = getClass().getResource(STYLESHEET);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        primaryStage.setTitle("Game Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts the chosen game on the existing stage. Calling the game's
     * {@code start} directly (rather than its {@code main}) avoids a second
     * {@link Application#launch}, which would throw on the JavaFX thread.
     *
     * @param factory      factory for the game's application
     * @param primaryStage the stage to reuse
     */
    private void launchGame(Supplier<Application> factory, Stage primaryStage) {
        try {
            factory.get().start(primaryStage);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to launch game", ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
