package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameSaveWriterCSV {
  private static final String DELIMITER = ",";

  /**
   * Saves the current game state to a CSV file using an auto-generated filename.
   *
   * @param boardGame The BoardGame instance to save
   * @param boardName The name of the board being used (can be null to use board's name)
   * @return The path to the saved file
   * @throws IOException If there's an error writing the file
   */
  public String saveGame(BoardGame boardGame, String boardName) throws IOException {
    // Generate a filename with current timestamp
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "game_save_" + timestamp + ".csv";

    return saveGame(boardGame, boardName, fileName);
  }

  /**
   * Saves the current game state to a CSV file with a custom filename.
   *
   * @param boardGame The BoardGame instance to save
   * @param boardName The name of the board being used (can be null to use board's name)
   * @param fileName The custom filename to use (without path)
   * @return The path to the saved file
   * @throws IOException If there's an error writing the file
   */
  public String saveGame(BoardGame boardGame, String boardName, String fileName) throws IOException {
    // Ensure the saves directory exists
    if (!ensureSavesDirectoryExists()) {
      throw new IOException("Failed to create saves directory");
    }

    // Use the board's name if boardName parameter is null or unknown
    if (boardName == null || boardName.equals("Unknown Board")) {
      boardName = boardGame.getBoard().getName();
    }

    // Ensure the filename ends with .csv
    if (!fileName.toLowerCase().endsWith(".csv")) {
      fileName += ".csv";
    }

    String filePath = "saves/" + fileName;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      // Write board information
      writer.write("boardName" + DELIMITER + boardName);
      writer.newLine();

      // Write current player index
      int currentPlayerIndex = boardGame.getCurrentPlayerIndex();
      writer.write("currentPlayerIndex" + DELIMITER + currentPlayerIndex);
      writer.newLine();

      // Write header for player data
      writer.write("playerName" + DELIMITER + "position");
      writer.newLine();

      // Write each player's data
      for (Player player : boardGame.getPlayers()) {
        writer.write(player.getName() + DELIMITER + player.getPositionIndex());
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
    java.io.File savesDir = new java.io.File("saves");
    if (!savesDir.exists()) {
      return savesDir.mkdirs();
    }
    return true;
  }
}