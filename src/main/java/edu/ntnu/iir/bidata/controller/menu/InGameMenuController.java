package edu.ntnu.iir.bidata.controller.menu;

import edu.ntnu.iir.bidata.Stigespillet;
import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.file.SaveFileTracker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller for handling in-game menu actions such as saving, returning to menu,
 * and exiting the game.
 */
public class InGameMenuController {
    private static final Logger LOGGER = Logger.getLogger(InGameMenuController.class.getName());

    private final MusicController musicController;
    private final BoardGameController boardGameController;

    /**
     * Creates a new in-game menu controller.
     *
     * @param boardGameController the board game controller
     * @param musicController the music controller
     */
    public InGameMenuController(BoardGameController boardGameController, MusicController musicController) {
        this.boardGameController = boardGameController;
        this.musicController = musicController;
        LOGGER.info("InGameMenuController initialized");
    }

    /**
     * Returns to the main menu by closing all open stages and restarting the application.
     */
    public void returnToMainMenu() {
        LOGGER.info("Returning to main menu");
        musicController.pause();
        closeAllStagesExcept(null);
        restartApplication();
    }

    /**
     * Exits the game completely.
     */
    public void exitGame() {
        LOGGER.info("Exiting game");
        System.exit(0);
    }

    /**
     * Saves the current game state.
     *
     * @param fileName optional filename for the save file, can be null
     * @return the path to the saved file
     * @throws IOException if saving fails
     */
    public String saveGame(String fileName) throws IOException {
        LOGGER.info("Saving game" + (fileName != null ? " as " + fileName : ""));

        GameSaveWriterCSV saveWriter = new GameSaveWriterCSV();
        String savedFilePath;

        try {
            if (!SaveFileTracker.getInstance().wasLoadedFromSave()) {
                savedFilePath = fileName != null ?
                        saveWriter.saveGame(boardGameController, null, fileName) :
                        saveWriter.saveGame(boardGameController, null);
            } else {
                String currentFileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
                savedFilePath = saveWriter.saveGame(boardGameController, null, currentFileName);
            }

            LOGGER.info("Game successfully saved to: " + savedFilePath);
            return savedFilePath;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save game", e);
            throw e;
        }
    }

    /**
     * Closes all open stages except the specified one.
     *
     * @param exceptStage the stage to keep open, can be null to close all stages
     */
    private void closeAllStagesExcept(Stage exceptStage) {
        List<Stage> stagesToClose = new ArrayList<>();

        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage && window != exceptStage) {
                stagesToClose.add((Stage) window);
            }
        }

        LOGGER.info("Closing " + stagesToClose.size() + " stages");
        for (Stage stage : stagesToClose) {
            stage.close();
        }
    }

    /**
     * Restarts the application by creating a new instance of the main application class.
     */
    private void restartApplication() {
        LOGGER.info("Restarting application");
        Platform.runLater(() -> {
            try {
                Stigespillet stigespillet = new Stigespillet();
                stigespillet.start(new Stage());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to restart application", ex);
            }
        });
    }
}