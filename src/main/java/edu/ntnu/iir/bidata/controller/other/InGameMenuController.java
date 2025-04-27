package edu.ntnu.iir.bidata.controller.other;

import edu.ntnu.iir.bidata.Stigespillet;
import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.file.SaveFileTracker;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InGameMenuController {
    private MusicController musicController;
    private BoardGameController boardGameController;

    public InGameMenuController(BoardGameController boardGameController, MusicController musicController) {
        this.boardGameController = boardGameController;
        this.musicController = musicController;
    }

    public void returnToMainMenu() {
        musicController.pause(); // Uses controller to interact with model
        closeAllStagesExcept(null);
        restartApplication();
    }

    public void exitGame() {
        System.exit(0);
    }

    public String saveGame(String fileName) throws IOException {
        GameSaveWriterCSV saveWriter = new GameSaveWriterCSV();

        if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
            if (fileName != null && !fileName.trim().isEmpty()) {
                return saveWriter.saveGame(boardGameController, null, fileName);
            } else {
                return saveWriter.saveGame(boardGameController, null);
            }
        } else {
            String existingFileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
            return saveWriter.saveGame(boardGameController, null, existingFileName);
        }
    }

    private void closeAllStagesExcept(Stage exceptStage) {
        List<Stage> stagesToClose = new ArrayList<>();
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage && window != exceptStage) {
                stagesToClose.add((Stage) window);
            }
        }

        for (Stage stage : stagesToClose) {
            stage.close();
        }
    }

    private void restartApplication() {
        Platform.runLater(() -> {
            Stigespillet stigespillet = new Stigespillet();
            try {
                stigespillet.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}