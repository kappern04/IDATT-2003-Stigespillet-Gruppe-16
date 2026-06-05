package edu.ntnu.iir.bidata.laddergame.file;

import edu.ntnu.iir.bidata.laddergame.controller.GameController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles saving the current game state to a CSV file in a true tabular format.
 * Each value is written in its own cell (column).
 */
public class GameSaveWriterCSV {
  private static final String DELIMITER = ",";
  private static final Logger LOGGER = Logger.getLogger(GameSaveWriterCSV.class.getName());
  private final String savesDirectory;

  /**
   * Constructor for GameSaveWriterCSV.
   * Initializes the saves directory and ensures it exists.
   */
  public GameSaveWriterCSV() {
    this.savesDirectory = SaveFileTracker.getSavesDirectory();
    ensureSavesDirectoryExists();
  }

  /**
   * Saves the game state to a CSV file with a timestamped filename.
   *
   * @param boardGameController The game controller containing the current game state.
   * @param boardName The name of the board. If null, the default board name is used.
   * @return The path to the saved file.
   * @throws IOException If an error occurs while writing to the file.
   */
  public String saveGame(GameController boardGameController, String boardName) throws IOException {
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "game_save_" + timestamp + ".csv";
    return saveGame(boardGameController, boardName, fileName);
  }

  /**
   * Saves the game state to a CSV file with a specified filename.
   *
   * @param boardGameController The game controller containing the current game state.
   * @param boardName The name of the board. If null, the default board name is used.
   * @param fileName The name of the file to save the game state to.
   * @return The path to the saved file.
   * @throws IOException If an error occurs while writing to the file.
   */
  public String saveGame(GameController boardGameController, String boardName, String fileName) throws IOException {
    if (boardGameController == null) {
      throw new IllegalArgumentException("GameController cannot be null.");
    }
    if (boardName == null || boardName.isBlank() || boardName.equals("Unknown Board")) {
      boardName = boardGameController.getBoard().getBoardName();
    }
    if (!fileName.toLowerCase().endsWith(".csv")) {
      fileName += ".csv";
    }
    String filePath = savesDirectory + File.separator + fileName;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      // Row 1: boardName,"Board Name"
      writer.write("boardName" + DELIMITER + "\"" + boardName + "\"");
      writer.newLine();

      // Row 2: currentPlayerIndex,0
      writer.write("currentPlayerIndex" + DELIMITER + boardGameController.getCurrentPlayerIndex());
      writer.newLine();

      // Row 3: doubleDiceMode,true|false
      writer.write("doubleDiceMode" + DELIMITER + boardGameController.isDoubleDiceMode());
      writer.newLine();

      // Row 4: playerName,position,color,shipTypeId
      writer.write("playerName" + DELIMITER + "position" + DELIMITER + "color" + DELIMITER + "shipTypeId");
      writer.newLine();

      // Row 4: player ranking
      StringBuilder rankings = new StringBuilder("rankings");
      for (Player rankedPlayer : boardGameController.getPlayerRanks()) {
        rankings.append(DELIMITER).append("\"").append(rankedPlayer.getName()).append("\"");
      }
      writer.write(rankings.toString());
      writer.newLine();

      // Row 5+: player data
      for (Player player : boardGameController.getPlayers()) {
        String formattedName = "\"" + player.getName() + "\"";
        int position = player.getPositionIndex();
        String colorStr = player.getColor() != null ? player.getColor() : "";
        int shipTypeId = player.getShipType();

        writer.write(formattedName + DELIMITER +
                position + DELIMITER +
                colorStr + DELIMITER +
                shipTypeId);
        writer.newLine();

        LOGGER.fine("Saved player: " + player.getName() +
                ", position: " + position +
                ", color: " + colorStr +
                ", shipTypeId: " + shipTypeId);
      }

      SaveFileTracker.getInstance().setCurrentSaveFilePath(filePath);
      LOGGER.info("Game saved to: " + filePath);
      return filePath;
    }
  }

  /**
   * Ensures the saves directory exists. If it doesn't, it attempts to create it.
   *
   * @return true if the directory exists or was created successfully, false otherwise.
   */
  private boolean ensureSavesDirectoryExists() {
    Path path = Paths.get(savesDirectory);
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
        LOGGER.info("Created saves directory at " + path);
        return true;
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Could not create saves directory: " + e.getMessage(), e);
        return false;
      }
    }
    return true;
  }
}