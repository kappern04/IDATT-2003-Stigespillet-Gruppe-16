package edu.ntnu.iir.bidata.laddergame.controller.menu;

import edu.ntnu.iir.bidata.laddergame.controller.GameController;
import edu.ntnu.iir.bidata.laddergame.controller.other.MusicController;
import edu.ntnu.iir.bidata.laddergame.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.laddergame.file.SaveFileTracker;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;

/**
 * Controller for handling in-game menu actions such as saving, returning to menu,
 * and exiting the game.
 */
public class InGameMenuController {
    private static final Logger LOGGER = Logger.getLogger(InGameMenuController.class.getName());

    private final MusicController musicController;
    private final GameController boardGameController;
    private final Stage primaryStage;

    /**
     * Creates a new in-game menu controller.
     *
     * @param boardGameController the board game controller
     * @param musicController the music controller
     * @param primaryStage the primary stage the game is shown on, reused for navigation
     */
    public InGameMenuController(GameController boardGameController, MusicController musicController, Stage primaryStage) {
        this.boardGameController = boardGameController;
        this.musicController = musicController;
        this.primaryStage = primaryStage;
        LOGGER.info("InGameMenuController initialized");
    }

    /**
     * Returns to the main menu by showing it on the existing primary stage.
     */
    public void returnToMainMenu() {
        LOGGER.info("Returning to main menu");
        musicController.pause();
        new MainMenuController(primaryStage);
    }

    /**
     * Exits the game completely.
     */
    public void exitGame() {
        LOGGER.info("Exiting game");
        System.exit(0);
    }

    /**
     * Whether saving should prompt the user for a new name. A game loaded from a
     * save is re-saved over its existing file, so no name is needed then.
     *
     * @return true if the user should be asked for a save name
     */
    public boolean needsSaveName() {
        return !SaveFileTracker.getInstance().wasLoadedFromSave();
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

}