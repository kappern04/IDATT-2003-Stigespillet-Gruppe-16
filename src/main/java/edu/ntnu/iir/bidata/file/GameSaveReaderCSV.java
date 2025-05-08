package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

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
      String[] boardNameParts = parseCsvLine(reader.readLine());
      String boardName = boardNameParts[1].replaceAll("^\"|\"$", "");

      String[] indexParts = parseCsvLine(reader.readLine());
      int currentPlayerIndex = Integer.parseInt(indexParts[1].trim());

      // skips first line
      reader.readLine();

      String[] rankingParts = parseCsvLine(reader.readLine());
      List<Player> players = new ArrayList<>();
      List<Player> rankings = new ArrayList<>();

      // reads player data
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) continue;
        String[] parts = parseCsvLine(line);
        if (parts.length >= 2) {
          Player player = parsePlayerFromData(parts);
          players.add(player);
          String playerName = player.getName();
          if (Arrays.asList(rankingParts).contains("\"" + playerName + "\"")) {
            rankings.add(player);
          }
        }
      }

      if (boardName == null || players.isEmpty()) {
        throw new IOException("Invalid save file: Missing required data.");
      }

      BoardGameController boardGameController = new BoardGameController();
      boardGameController.setBoard(loadBoardByName(boardName));
      boardGameController.setPlayers(players.toArray(new Player[0]));
      boardGameController.setCurrentPlayerIndex(currentPlayerIndex);
      boardGameController.setPlayerRanks(rankings);

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

  private Player parsePlayerFromData(String[] parts) {
    String playerName = parts[0].replaceAll("^\"|\"$", "");
    int position = Integer.parseInt(parts[1].trim());

    Player player = new Player(playerName);
    player.setPositionIndex(position);

    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
      Color color = parsePlayerColor(parts[2].trim(), playerName);
      if (color != null) {
        player.setColor(color);
      }
    }

    if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
      try {
        int shipType = Integer.parseInt(parts[3].trim());
        player.setShipType(shipType);
      } catch (NumberFormatException e) {
        LOGGER.warning("Could not parse ship type for player " + playerName);
      }
    }

    return player;
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

  private List<Player> parsePlayerRanks(String[] parts, List<Player> players) {
    List<Player> rankings = new ArrayList<>();
    if (parts.length <= 1) {
      return rankings;
    }

    for (int i = 1; i < parts.length; i++) {
      String playerName = parts[i].replaceAll("\"", "");
      for (Player player : players) {
        if (player.getName().equals(playerName)) {
          rankings.add(player);
          break;
        }
      }
    }
    return rankings;
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