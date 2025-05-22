package edu.ntnu.iir.bidata.laddergame.file;

/**
 * Singleton class that tracks the currently loaded save file.
 * This allows the game to overwrite the original save file when saving.
 */
public class SaveFileTracker {
  private static SaveFileTracker instance;
  private String currentSaveFilePath;
  private boolean loadedFromSave;

  private SaveFileTracker() {
    this.currentSaveFilePath = null;
    this.loadedFromSave = false;
  }

  /**
   * Gets the singleton instance
   * @return The SaveFileTracker instance
   */
  public static synchronized SaveFileTracker getInstance() {
    if (instance == null) {
      instance = new SaveFileTracker();
    }
    return instance;
  }

  /**
   * Sets the current save file path when a game is loaded
   * @param filePath The path to the save file
   */
  public void setCurrentSaveFilePath(String filePath) {
    this.currentSaveFilePath = filePath;
    this.loadedFromSave = true;
  }

  /**
   * Gets the current save file path
   * @return The save file path or null if no file was loaded
   */
  public String getCurrentSaveFilePath() {
    return currentSaveFilePath;
  }

  /**
   * Checks if the current game was loaded from a save file
   * @return true if the game was loaded from a save file
   */
  public boolean wasLoadedFromSave() {
    return loadedFromSave;
  }

  /**
   * Resets the tracker for a new game
   */
  public void reset() {
    this.currentSaveFilePath = null;
    this.loadedFromSave = false;
  }

  /**
   * Extracts the filename from a full path
   * @return The filename without path
   */
  public String getCurrentSaveFileName() {
    if (currentSaveFilePath == null) {
      return null;
    }

    int lastSeparatorIndex = Math.max(
        currentSaveFilePath.lastIndexOf('/'),
        currentSaveFilePath.lastIndexOf('\\')
    );

    if (lastSeparatorIndex == -1) {
      return currentSaveFilePath;
    }

    return currentSaveFilePath.substring(lastSeparatorIndex + 1);
  }
}