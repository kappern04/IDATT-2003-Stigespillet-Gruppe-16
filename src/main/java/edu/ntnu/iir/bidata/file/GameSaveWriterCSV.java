package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Player;
import javafx.scene.paint.Color;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSaveWriterCSV {
  private static final String DELIMITER = ",";
  private static final Logger LOGGER = Logger.getLogger(GameSaveWriterCSV.class.getName());
  private final String savesDirectory;

  public GameSaveWriterCSV() {
    this.savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
    ensureSavesDirectoryExists();
  }

  /**
   * Saves the current game state to a CSV file using an auto-generated filename.
   *
   * @param boardGameController The BoardGame instance to save
   * @param boardName The name of the board being used (can be null to use board's name)
   * @return The path to the saved file
   * @throws IOException If there's an error writing the file
   */
  public String saveGame(BoardGameController boardGameController, String boardName) throws IOException {
    // Generate a filename with current timestamp
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "game_save_" + timestamp + ".csv";

    return saveGame(boardGameController, boardName, fileName);
  }

  /**
   * Saves the current game state to a CSV file with a custom filename.
   *
   * @param boardGameController The BoardGame instance to save
   * @param boardName The name of the board being used (can be null to use board's name)
   * @param fileName The custom filename to use (without path)
   * @return The path to the saved file
   * @throws IOException If there's an error writing the file
   */
  public String saveGame(BoardGameController boardGameController, String boardName, String fileName) throws IOException {
    // Use the board's name if boardName parameter is null or unknown
    if (boardName == null || boardName.equals("Unknown Board")) {
      boardName = boardGameController.getBoard().getBoardName();
    }

    // Ensure the filename ends with .csv
    if (!fileName.toLowerCase().endsWith(".csv")) {
      fileName += ".csv";
    }

    String filePath = savesDirectory + File.separator + fileName;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      // Write board information
      writer.write("boardName" + DELIMITER + boardName);
      writer.newLine();

      // Write current player index
      int currentPlayerIndex = boardGameController.getCurrentPlayerIndex();
      writer.write("currentPlayerIndex" + DELIMITER + currentPlayerIndex);
      writer.newLine();

      // Write header for player data, now including color and shipType
      writer.write("playerName" + DELIMITER + "position" + DELIMITER + "color" + DELIMITER + "shipType");
      writer.newLine();

      // Write each player's data including color and shipType
      for (Player player : boardGameController.getPlayers()) {
        StringBuilder line = new StringBuilder();
        line.append(player.getName()).append(DELIMITER);
        line.append(player.getPositionIndex()).append(DELIMITER);

        // Format color as "r;g;b;a"
        Color color = player.getColor();
        if (color != null) {
          line.append(color.getRed()).append(";")
                  .append(color.getGreen()).append(";")
                  .append(color.getBlue()).append(";")
                  .append(color.getOpacity());
        }
        line.append(DELIMITER);

        // Add ship type
        line.append(player.getShipType());

        writer.write(line.toString());
        writer.newLine();
      }

      return filePath;
    }
  }

  /**
   * Helper method to ensure the saves directory exists
   *
   * @return true if the directory exists or was created successfully
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