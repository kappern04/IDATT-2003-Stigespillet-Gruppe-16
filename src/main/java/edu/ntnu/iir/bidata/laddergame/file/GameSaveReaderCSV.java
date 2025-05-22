package edu.ntnu.iir.bidata.laddergame.file;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 * Reads a saved game state from a CSV file and reconstructs the {@link BoardGameController}.
 */
public class GameSaveReaderCSV {
  private static final Logger LOGGER = Logger.getLogger(GameSaveReaderCSV.class.getName());
  private final String savesDirectory;
  private final DecimalFormatSymbols symbols;

  public GameSaveReaderCSV() {
    this.savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
    this.symbols = new DecimalFormatSymbols(Locale.US);
  }

  /**
   * Loads a game from a CSV file and reconstructs the BoardGameController.
   */
  public BoardGameController loadGame(String filePath) throws IOException {
    if (!Paths.get(filePath).isAbsolute()) {
      filePath = savesDirectory + File.separator + filePath;
    }
    LOGGER.info("Loading game from: " + filePath);
    SaveFileTracker.getInstance().setCurrentSaveFilePath(filePath);

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String[] boardNameParts = parseCsvLine(reader.readLine());
      String boardName = getCsvValue(boardNameParts, 1);

      String[] indexParts = parseCsvLine(reader.readLine());
      int currentPlayerIndex = Integer.parseInt(getCsvValue(indexParts, 1));

      reader.readLine();

      String[] rankingParts = parseCsvLine(reader.readLine());
      List<Player> players = new ArrayList<>();
      List<String> rankingNames = getRankingNames(rankingParts);

      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) continue;
        String[] parts = parseCsvLine(line);
        if (parts.length >= 2) {
          Player player = parsePlayerFromData(parts);
          players.add(player);
        }
      }

      if (boardName == null || players.isEmpty()) {
        throw new IOException("Invalid save file: Missing required data.");
      }

      List<Player> rankings = getPlayerRankings(rankingNames, players);

      BoardGameController boardGameController = new BoardGameController();
      boardGameController.setBoard(loadBoardByName(boardName));
      boardGameController.setPlayers(players);
      boardGameController.setCurrentPlayerIndex(currentPlayerIndex);
      boardGameController.setPlayerRanks(rankings);

      LOGGER.info("Successfully loaded game with " + players.size() + " players");
      return boardGameController;
    }
  }

  private String getCsvValue(String[] arr, int idx) {
    if (arr == null || arr.length <= idx) return null;
    return arr[idx].replaceAll("^\"|\"$", "");
  }

  private List<String> getRankingNames(String[] rankingParts) {
    List<String> names = new ArrayList<>();
    for (int i = 1; i < rankingParts.length; i++) {
      names.add(rankingParts[i].replaceAll("^\"|\"$", ""));
    }
    return names;
  }

  private List<Player> getPlayerRankings(List<String> rankingNames, List<Player> players) {
    List<Player> rankings = new ArrayList<>();
    for (String name : rankingNames) {
      players.stream()
              .filter(p -> p.getName().equals(name))
              .findFirst()
              .ifPresent(rankings::add);
    }
    return rankings;
  }

  private Player parsePlayerFromData(String[] parts) {
    String playerName = parts[0].replaceAll("^\"|\"$", "");
    int position = Integer.parseInt(parts[1].trim());
    Player player = new Player(playerName);
    player.setPositionIndex(position);

    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
      Color color = parsePlayerColor(parts[2].trim(), playerName);
      if (color != null) player.setColor(color);
    }
    if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
      try {
        player.setShipType(Integer.parseInt(parts[3].trim()));
      } catch (NumberFormatException e) {
        LOGGER.warning("Could not parse ship type for player " + playerName);
      }
    }
    return player;
  }

  private Color parsePlayerColor(String colorString, String playerName) {
    try {
      String[] colorParts = colorString.split(";");
      if (colorParts.length < 3) {
        LOGGER.warning("Invalid color format for player " + playerName + ": " + colorString);
        return null;
      }
      double red = parseDouble(colorParts[0]);
      double green = parseDouble(colorParts[1]);
      double blue = parseDouble(colorParts[2]);
      double opacity = colorParts.length > 3 ? parseDouble(colorParts[3]) : 1.0;
      return new Color(
              clamp(red), clamp(green), clamp(blue), clamp(opacity)
      );
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to parse color for player " + playerName + ": " + colorString, e);
      return null;
    }
  }

  private double clamp(double value) {
    return Math.max(0, Math.min(1, value));
  }

  private double parseDouble(String value) {
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
    for (char c : line.toCharArray()) {
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