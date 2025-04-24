package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.model.Board;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry for managing all board definitions in the game.
 * Provides access to both built-in boards and user-created boards.
 */
public class BoardRegistry {
  private static final Logger LOGGER = Logger.getLogger(BoardRegistry.class.getName());
  private static BoardRegistry instance;

  // Maps board names to their resource paths or file paths
  private final Map<String, String> boardPathMap = new HashMap<>();

  // Built-in board resources
  private static final String[] NATIVE_BOARDS = {
      "/boards/spiral.json",
      "/boards/normal.json",
      "/boards/zigzag.json"
  };

  // Default directory for user-created boards
  private String userBoardsDirectory;
  private final BoardFileReaderGson reader;

  /**
   * Gets the singleton instance of BoardRegistry.
   *
   * @return The BoardRegistry instance
   */
  public static synchronized BoardRegistry getInstance() {
    if (instance == null) {
      instance = new BoardRegistry();
    }
    return instance;
  }

  /**
   * Private constructor for singleton pattern.
   */
  private BoardRegistry() {
    this.reader = new BoardFileReaderGson();
    this.userBoardsDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "boards";

    // Ensure user boards directory exists
    createUserBoardsDirectory();

    // Load board definitions
    loadNativeBoards();
    loadUserBoards();
  }

  /**
   * Creates the user boards directory if it doesn't exist.
   */
  private void createUserBoardsDirectory() {
    Path path = Paths.get(userBoardsDirectory);
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
        LOGGER.info("Created user boards directory at " + path);
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Could not create user boards directory: " + e.getMessage(), e);
      }
    }
  }

  /**
   * Loads native (built-in) boards from resources.
   */
  private void loadNativeBoards() {
    for (String resourcePath : NATIVE_BOARDS) {
      try {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
          LOGGER.warning("Could not find resource: " + resourcePath);
          continue;
        }

        Board board = reader.readBoard(stream);
        boardPathMap.put(board.getName(), resourcePath);
        LOGGER.fine("Loaded native board: " + board.getName());
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Failed to load native board: " + resourcePath, e);
      }
    }
  }

  /**
   * Loads user-created boards from the user boards directory.
   */
  private void loadUserBoards() {
    File directory = new File(userBoardsDirectory);
    if (!directory.exists() || !directory.isDirectory()) {
      return;
    }

    File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
    if (files == null) {
      return;
    }

    for (File file : files) {
      try {
        Board board = reader.readBoard(Files.newInputStream(file.toPath()));
        String filePath = file.getAbsolutePath();
        boardPathMap.put(board.getName(), filePath);
        LOGGER.fine("Loaded user board: " + board.getName());
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Failed to load user board: " + file.getName(), e);
      }
    }
  }

  /**
   * Sets a custom directory for user-created boards.
   *
   * @param directory The new directory path
   */
  public void setUserBoardsDirectory(String directory) {
    this.userBoardsDirectory = directory;
    createUserBoardsDirectory();
    loadUserBoards();
  }

  /**
   * Gets all available board names.
   *
   * @return List of board names
   */
  public List<String> getBoardNames() {
    return new ArrayList<>(boardPathMap.keySet());
  }

  /**
   * Loads a board by its name.
   *
   * @param boardName The name of the board to load
   * @return The loaded Board, or null if not found
   */
  public Board getBoardByName(String boardName) {
    String path = boardPathMap.get(boardName);
    if (path == null) {
      LOGGER.warning("Board not found: " + boardName);
      return null;
    }

    try {
      if (path.startsWith("/")) {
        // Resource path
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
          throw new IOException("Resource not found: " + path);
        }
        return reader.readBoard(stream);
      } else {
        // File path
        return reader.readBoard(Files.newInputStream(Paths.get(path)));
      }
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Failed to load board: " + boardName, e);
      return null;
    }
  }

  /**
   * Checks if a board with the given name exists.
   *
   * @param boardName The name to check
   * @return true if the board exists, false otherwise
   */
  public boolean boardExists(String boardName) {
    return boardPathMap.containsKey(boardName);
  }

  /**
   * Adds a new board to the registry.
   *
   * @param board The board to add
   * @param saveToFile Whether to save this board to a file
   * @return true if added successfully, false otherwise
   */
  public boolean addBoard(Board board, boolean saveToFile) {
    if (board == null || board.getName() == null || board.getName().isEmpty()) {
      return false;
    }

    if (saveToFile) {
      try {
        String fileName = board.getName().replaceAll("[^a-zA-Z0-9-_]", "_") + ".json";
        Path filePath = Paths.get(userBoardsDirectory, fileName);

        BoardFileWriterGson writer = new BoardFileWriterGson();
        writer.writeBoard(board, filePath.toString());

        boardPathMap.put(board.getName(), filePath.toString());
        return true;
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Failed to save board: " + board.getName(), e);
        return false;
      }
    } else {
      // Just add to memory without saving
      boardPathMap.put(board.getName(), null);
      return true;
    }
  }
}