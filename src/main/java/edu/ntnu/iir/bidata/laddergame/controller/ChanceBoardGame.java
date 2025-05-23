package edu.ntnu.iir.bidata.laddergame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.ChanceTileInitializer;
import edu.ntnu.iir.bidata.laddergame.model.CosmicChanceAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;
import edu.ntnu.iir.bidata.laddergame.view.board.ChanceTileView;

import java.util.Random;

/**
 * Controller for a board game with chance tiles.
 * Handles the initialization, management and effects of chance tiles.
 */
public class ChanceBoardGame extends BoardGameController {

  private final int chancePercentage;
  private boolean chanceEnabled = true;
  private final Random random = new Random();
  private final ChanceTileView chanceTileView = new ChanceTileView();

  /**
   * Creates a new chance board game with the specified board and controllers.
   *
   * @param board the game board
   * @param playerController the player controller
   * @param dieController the die controller
   * @param chancePercentage the percentage of tiles that should be chance tiles (5-100%)
   */
  public ChanceBoardGame(Board board, PlayerController playerController, DieController dieController, int chancePercentage) {
    super(board, playerController, dieController);
    this.chancePercentage = Math.max(5, Math.min(100, chancePercentage)); // Constrain between 5-100%

    // Initialize chance tiles on the board
    if (board != null && chanceEnabled) {
      initializeChanceTiles(board);
    }
  }

  /**
   * Handles a player landing on a chance tile.
   *
   * @param player the player who landed on the chance tile
   * @param tile the chance tile
   * @return true if animation should continue, false if it should wait
   */
  public boolean handleChanceEffect(Player player, Tile tile) {
    if (!chanceEnabled || !tile.hasChanceAction()) {
      return true;
    }

    CosmicChanceAction chanceAction = (CosmicChanceAction) tile.getTileAction();

    // Notify observers that a chance tile was activated
    notifyObservers("chanceactivated_" + tile.getIndex());

    // Show popup and then execute the effect when the popup is dismissed
    chanceTileView.showChancePopup(player, chanceAction, () -> {
      chanceAction.executeEffect(player);
    });

    return false; // Wait for the popup and animation to complete
  }

  /**
   * Initializes chance tiles on the board.
   *
   * @param board the game board
   */
  private void initializeChanceTiles(Board board) {
    ChanceTileInitializer.addChanceTilesToBoard(board, chancePercentage);
    notifyObservers("chancetilesadded");
  }

  @Override
  public void setBoard(Board board) {
    super.setBoard(board);
    if (chanceEnabled && board != null) {
      initializeChanceTiles(board);
    }
  }

  /**
   * Enable or disable chance tiles.
   *
   * @param enabled true to enable chance tiles, false to disable
   */
  public void setChanceEnabled(boolean enabled) {
    if (this.chanceEnabled == enabled) {
      return; // No change needed
    }

    this.chanceEnabled = enabled;
    Board board = getBoard();

    if (board == null) {
      return;
    }

    if (!enabled) {
      // Remove all chance tiles
      for (Tile tile : board.getTiles()) {
        if (tile.hasChanceAction()) {
          tile.setTileAction(null);
          tile.setType("normal");
        }
      }
      notifyObservers("chancetilesdisabled");
    } else {
      // Re-add chance tiles
      initializeChanceTiles(board);
    }
  }

  /**
   * Gets whether chance tiles are enabled.
   *
   * @return true if chance tiles are enabled, false otherwise
   */
  public boolean isChanceEnabled() {
    return chanceEnabled;
  }

  /**
   * Gets the percentage of tiles that are chance tiles.
   *
   * @return the percentage of chance tiles
   */
  public int getChancePercentage() {
    return chancePercentage;
  }

  /**
   * Count how many chance tiles are on the board.
   *
   * @return the number of chance tiles
   */
  public int getChanceTileCount() {
    int count = 0;
    Board board = getBoard();

    if (board == null) {
      return 0;
    }

    for (Tile tile : board.getTiles()) {
      if (tile.hasChanceAction()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Get all chance tiles on the board.
   *
   * @return array of tile indices that are chance tiles
   */
  public int[] getChanceTileIndices() {
    Board board = getBoard();
    if (board == null) {
      return new int[0];
    }

    return board.getTiles().stream()
            .filter(Tile::hasChanceAction)
            .mapToInt(Tile::getIndex)
            .toArray();
  }

  protected void applyTileEffects(Player player) {
    int position = player.getPositionIndex();
    int lastIndex = getBoard().getTiles().size() - 1;

    if (position < 0) {
      player.setPositionIndex(0);
      position = 0;
    } else if (position > lastIndex) {
      player.setPositionIndex(lastIndex);
      position = lastIndex;
    }

    Tile currentTile = getBoard().getTiles().get(position);
    if (currentTile.hasChanceAction()) {
      handleChanceEffect(player, currentTile);
    } else {
      currentTile.landOn(player);
    }
  }
}