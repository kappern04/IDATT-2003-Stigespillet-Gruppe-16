package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameSaveReaderCSV {
  private static final String DELIMITER = ",";

  /**
   * Reads a saved game state from a CSV file and reconstructs the BoardGame object.
   *
   * @param filePath The path to the saved CSV file
   * @return A reconstructed BoardGame object
   * @throws IOException If there's an error reading the file
   */
  public BoardGame loadGame(String filePath) throws IOException {
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
        // Parse player data
        else if (!parts[0].equals("playerName") && parts.length == 2) {
          String playerName = parts[0];
          int positionIndex = Integer.parseInt(parts[1]);
          Player player = new Player(playerName);
          player.setPositionIndex(positionIndex);
          players.add(player);
        }
      }

      // Validate that boardName and players were read
      if (boardName == null || players.isEmpty()) {
        throw new IOException("Invalid save file: Missing required data.");
      }

      // Reconstruct the BoardGame object
      BoardGame boardGame = new BoardGame();

      // Load the board first
      Board board = loadBoardByName(boardName);
      boardGame.setBoard(board);

      // Then set players and current player index
      boardGame.setPlayers(players.toArray(new Player[0]));
      boardGame.setCurrentPlayerIndex(currentPlayerIndex);

      return boardGame;
    }
  }

  /**
   * Loads a board by its name. This method maps board names to resource paths
   * and loads the corresponding board.
   *
   * @param boardName The name of the board
   * @return A Board object
   */
  private Board loadBoardByName(String boardName) {
    // Map board names to resource paths
    String resourcePath;
    switch (boardName) {
      case "Spiral Way":
        resourcePath = "/boards/spiral.json";
        break;
      case "Ladderia Prime":
      case "Normal Board":  // Add support for legacy saved game format
        resourcePath = "/boards/normal.json";
        break;
      default:
        // For unknown board names, attempt to use default board
        System.out.println("Warning: Unknown board name '" + boardName + "', using default board");
        return new Board();
    }

    return loadBoardFromResource(resourcePath);
  }

  /**
   * Loads a board from a resource file.
   *
   * @param resourcePath The path to the resource file
   * @return A Board object
   */
  private Board loadBoardFromResource(String resourcePath) {
    try {
      InputStream inputStream = getClass().getResourceAsStream(resourcePath);
      if (inputStream == null) {
        throw new IOException("Resource not found: " + resourcePath);
      }
      return new BoardGameFactory().createBoardGameFromStream(inputStream).getBoard();
    } catch (Exception e) {
      // Instead of propagating the exception, log and return default board
      System.err.println("Failed to load board from resource: " + resourcePath + " - " + e.getMessage());
      return new Board();
    }
  }
}