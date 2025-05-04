package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads a saved game state from a CSV file and reconstructs the {@link BoardGameController}.
 * The expected CSV format is:
 * boardName,"Board Name"
 * currentPlayerIndex,0
 * playerName,position,color,shipTypeId
 * "Player 1",0,0.500;0.500;0.500;1.000,1
 * ...
 */
public class GameSaveReaderCSV {
  private static final String DELIMITER = ",";
  private static final Logger LOGGER = Logger.getLogger(GameSaveReaderCSV.class.getName());
  private final String savesDirectory;
  private final DecimalFormatSymbols symbols;

  public GameSaveReaderCSV() {
    this.savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
    // Use US locale for consistent decimal parsing (period as decimal separator)
    this.symbols = new DecimalFormatSymbols(Locale.US);
  }

  public BoardGameController loadGame(String filePath) throws IOException {
    if (!Paths.get(filePath).isAbsolute()) {
      filePath = savesDirectory + File.separator + filePath;
    }

    LOGGER.info("Loading game from: " + filePath);
    SaveFileTracker.getInstance().setCurrentSaveFilePath(filePath);

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      String boardName = null;
      int currentPlayerIndex = 0;
      List<Player> players = new ArrayList<>();
      boolean readingPlayerData = false;

      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }

        String[] parts = parseCsvLine(line);

        if (parts.length == 0) continue;

        if (parts[0].equals("boardName")) {
          boardName = parts[1].replaceAll("^\"|\"$", "");
          LOGGER.info("Loading board: " + boardName);
        } else if (parts[0].equals("currentPlayerIndex")) {
          currentPlayerIndex = Integer.parseInt(parts[1].trim());
          LOGGER.info("Current player index: " + currentPlayerIndex);
        } else if (parts[0].equals("playerName")) {
          readingPlayerData = true;
        } else if (readingPlayerData && parts.length >= 2) {
          String playerName = parts[0].replaceAll("^\"|\"$", "");
          int positionIndex = Integer.parseInt(parts[1].trim());

          Player player = new Player(playerName);
          player.setPositionIndex(positionIndex);

          // Parse color with improved error handling
          if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
            Color color = parsePlayerColor(parts[2].trim(), playerName);
            if (color != null) {
              player.setColor(color);
              LOGGER.info("Player " + playerName + " color loaded: rgba(" +
                      color.getRed() + "," + color.getGreen() + "," +
                      color.getBlue() + "," + color.getOpacity() + ")");
            }
          } else {
            LOGGER.warning("No color data for player: " + playerName);
          }

          // Parse shipTypeId
          if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
            try {
              int shipType = Integer.parseInt(parts[3].trim());
              player.setShipType(shipType);
              LOGGER.info("Player " + playerName + " ship type: " + shipType);
            } catch (NumberFormatException e) {
              LOGGER.warning("Could not parse ship type for player " + playerName + ": " + e.getMessage());
            }
          }

          players.add(player);
        }
      }

      if (boardName == null || players.isEmpty()) {
        throw new IOException("Invalid save file: Missing required data.");
      }

      BoardGameController boardGameController = new BoardGameController();
      Board board = loadBoardByName(boardName);
      boardGameController.setBoard(board);
      boardGameController.setPlayers(players.toArray(new Player[0]));
      boardGameController.setCurrentPlayerIndex(currentPlayerIndex);

      LOGGER.info("Successfully loaded game with " + players.size() + " players");
      return boardGameController;
    }
  }

  /**
   * Parses a color string in the format "r;g;b;a" and creates a JavaFX Color object.
   * Uses US locale to ensure consistent decimal parsing.
   */
  private Color parsePlayerColor(String colorString, String playerName) {
    try {
      String[] colorParts = colorString.split(";");
      if (colorParts.length < 3) {
        LOGGER.warning("Invalid color format for player " + playerName + ": " + colorString);
        return null;
      }

      // Parse using US locale to handle decimal points consistently
      double red = parseDouble(colorParts[0].trim());
      double green = parseDouble(colorParts[1].trim());
      double blue = parseDouble(colorParts[2].trim());
      double opacity = colorParts.length > 3 ? parseDouble(colorParts[3].trim()) : 1.0;

      // Verify values are within valid range (0.0-1.0)
      red = Math.max(0, Math.min(1, red));
      green = Math.max(0, Math.min(1, green));
      blue = Math.max(0, Math.min(1, blue));
      opacity = Math.max(0, Math.min(1, opacity));

      return new Color(red, green, blue, opacity);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to parse color for player " + playerName + ": " + colorString, e);
      return null;
    }
  }

  /**
   * Parse double values using US locale to ensure consistent handling of decimal points
   */
  private double parseDouble(String value) throws ParseException {
    // Replace any commas with periods to handle potential locale issues
    value = value.replace(',', '.');
    return Double.parseDouble(value);
  }

  /**
   * Parses a CSV line using the "," delimiter, handling quoted values.
   */
  private String[] parseCsvLine(String line) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;

    char[] chars = line.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];

      if (c == '"') {
        inQuotes = !inQuotes;
        current.append(c);
      } else if (!inQuotes && c == ',') {
        result.add(current.toString().trim());
        current = new StringBuilder();
      } else {
        current.append(c);
      }
    }
    result.add(current.toString().trim());
    return result.toArray(new String[0]);
  }

  private Board loadBoardByName(String boardName) throws IOException {
    BoardRegistry registry = BoardRegistry.getInstance();
    Board board = registry.getBoardByName(boardName);

    if (board == null) {
      LOGGER.warning("Unknown board name: '" + boardName + "', using default board");
      return new Board();
    }

    return board;
  }
}