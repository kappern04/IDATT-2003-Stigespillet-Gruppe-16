package edu.ntnu.iir.bidata.clickgame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class DummyMainMenuController extends MainMenuController {
    private final Stage primaryStage;

    public DummyMainMenuController(Stage stage) {
        super(stage);
        this.primaryStage = stage;
    }

    @Override
    public void startNewGame(String boardName, int numPlayers, List<PlayerData> playerDetails) {
        // Do nothing
    }

    @Override
    public void loadGame(File file) {
        // Do nothing
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}