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
   * Saves the current game state to a CSV file.
   *
   * @param boardGame The BoardGame instance to save
   * @param boardName The name of the board being used
   * @return The path to the saved file
   * @throws IOException If there's an error writing the file
   */
  public String saveGame(BoardGame boardGame, String boardName) throws IOException {
    // Ensure the saves directory exists
    if (!ensureSavesDirectoryExists()) {
      throw new IOException("Failed to create saves directory");
    }

    if (boardName == null || boardName.equals("Unknown Board")) {
      boardName = boardGame.getBoard().getName();
    }

    // Generate a filename with current timestamp
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "game_save_" + timestamp + ".csv";
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
   * Returns the current player index from the board game.
   * Assumes the BoardGame keeps track of the current player index.
   *
   * @param boardGame The BoardGame instance
   * @return The current player index or 0 if can't be determined
   */
  private int getCurrentPlayerIndex(BoardGame boardGame) {
    return boardGame.getCurrentPlayerIndex();
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