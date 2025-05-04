package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameSaveReaderCSV {
  private static final String DELIMITER = ",";
  private static final Logger LOGGER = Logger.getLogger(GameSaveReaderCSV.class.getName());

  /**
   * Reads a saved game state from a CSV file and reconstructs the BoardGame object.
   *
   * @param filePath The path to the saved CSV file
   * @return A reconstructed BoardGame object
   * @throws IOException If there's an error reading the file
   */
  public BoardGameController loadGame(String filePath) throws IOException {
    SaveFileTracker.getInstance().setCurrentSaveFilePath(filePath);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      String boardName = null;
      int currentPlayerIndex = 0;
      List<Player> players = new ArrayList<>();

      // Read the file line by line
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(DELIMITER);

        // Parse board name
        if (parts[0].equals("boardName")) {
          boardName = parts[1];
        }
        // Parse current player index
        else if (parts[0].equals("currentPlayerIndex")) {
          currentPlayerIndex = Integer.parseInt(parts[1]);
        }
        // Parse player data - check if this is a player data line and not a header
        else if (!parts[0].equals("playerName") && parts.length >= 2) {
          String playerName = parts[0];
          int positionIndex = Integer.parseInt(parts[1]);

          // Create player with default values
          Player player = new Player(playerName);
          player.setPositionIndex(positionIndex);

          // Check if color data exists (column 3)
          if (parts.length >= 3 && !parts[2].isEmpty()) {
            try {
              // Parse color in format "r,g,b,a" or similar
              String[] colorParts = parts[2].split(";");
              if (colorParts.length >= 3) {
                double r = Double.parseDouble(colorParts[0]);
                double g = Double.parseDouble(colorParts[1]);
                double b = Double.parseDouble(colorParts[2]);
                double a = colorParts.length > 3 ? Double.parseDouble(colorParts[3]) : 1.0;
                player.setColor(new javafx.scene.paint.Color(r, g, b, a));
              }
            } catch (Exception e) {
              LOGGER.warning("Could not parse color for player " + playerName);
            }
          }

          // Check if ship type exists (column 4)
          if (parts.length >= 4 && !parts[3].isEmpty()) {
            try {
              int shipType = Integer.parseInt(parts[3]);
              // Add the setter to Player class to support this
              player.setShipType(shipType);
            } catch (NumberFormatException e) {
              LOGGER.warning("Could not parse ship type for player " + playerName);
            }
          }

          players.add(player);
        }
      }

      // Validate that boardName and players were read
      if (boardName == null || players.isEmpty()) {
        throw new IOException("Invalid save file: Missing required data.");
      }

      // Reconstruct the BoardGame object
      BoardGameController boardGameController = new BoardGameController();

      // Load the board first
      Board board = loadBoardByName(boardName);
      boardGameController.setBoard(board);

      // Then set players and current player index
      boardGameController.setPlayers(players.toArray(new Player[0]));
      boardGameController.setCurrentPlayerIndex(currentPlayerIndex);

      return boardGameController;
    }
  }

  /**
   * Loads a board by its name. This method maps board names to resource paths
   * and loads the corresponding board.
   *
   * @param boardName The name of the board
   * @return A Board object
   */
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