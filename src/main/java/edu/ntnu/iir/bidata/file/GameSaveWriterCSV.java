package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Player;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 * Handles saving the current game state to a CSV file in a true tabular format.
 * Each value is written in its own cell (column).
 */
public class GameSaveWriterCSV {
  private static final String DELIMITER = ",";
  private static final Logger LOGGER = Logger.getLogger(GameSaveWriterCSV.class.getName());
  private final String savesDirectory;
  private final DecimalFormat colorFormat;

  public GameSaveWriterCSV() {
    this.savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
    ensureSavesDirectoryExists();

    // Create a decimal format with period as decimal separator regardless of locale
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    this.colorFormat = new DecimalFormat("0.000", symbols);
  }

  public String saveGame(BoardGameController boardGameController, String boardName) throws IOException {
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "game_save_" + timestamp + ".csv";
    return saveGame(boardGameController, boardName, fileName);
  }

  public String saveGame(BoardGameController boardGameController, String boardName, String fileName) throws IOException {
    if (boardName == null || boardName.equals("Unknown Board")) {
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
      // Row 3: playerName,position,color,shipTypeId
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

        // Color as r;g;b;a with consistent decimal format
        String colorStr = formatPlayerColor(player.getColor());

        // Get shipTypeId
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
   * Format player color with consistent decimal notation using US locale
   * to avoid issues with different decimal separators.
   */
  private String formatPlayerColor(Color color) {
    if (color == null) {
      return "";
    }

    return colorFormat.format(color.getRed()) + ";" +
            colorFormat.format(color.getGreen()) + ";" +
            colorFormat.format(color.getBlue()) + ";" +
            colorFormat.format(color.getOpacity());
  }

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