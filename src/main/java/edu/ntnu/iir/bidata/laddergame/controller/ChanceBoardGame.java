package edu.ntnu.iir.bidata.laddergame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.ChanceTileInitializer;
import edu.ntnu.iir.bidata.laddergame.model.CosmicChanceAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Controller for a board game with chance tiles.
 * Handles the initialization, management and effects of chance tiles.
 */
public class ChanceBoardGame extends BoardGameController {

  private final int chancePercentage;
  private boolean chanceEnabled = true;
  private final Random random = new Random();
  private final Map<ChanceEffectType, Consumer<Player>> chanceEffectHandlers = new HashMap<>();

  // Constants for chance tile effects
  private static final int MAX_FORWARD_SPACES = 3;
  private static final int MAX_BACKWARD_SPACES = 3;

  /**
   * Creates a new chance board game with the specified board and controllers.
   *
   * @param board the game board
   * @param playerController the player controller
   * @param dieController the die controller
   * @param chancePercentage the percentage of tiles that should be chance tiles (5-25%)
   */
  public ChanceBoardGame(Board board, PlayerController playerController, DieController dieController, int chancePercentage) {
    super(board, playerController, dieController);
    this.chancePercentage = Math.max(5, Math.min(25, chancePercentage)); // Constrain between 5-25%
    initializeChanceEffectHandlers();

    // Initialize chance tiles on the board
    if (board != null && chanceEnabled) {
      initializeChanceTiles(board);
    }
  }

  /**
   * Initializes handlers for different chance effect types.
   */
  private void initializeChanceEffectHandlers() {
    chanceEffectHandlers.put(ChanceEffectType.FORWARD_SMALL, player -> {
      int spaces = 1 + random.nextInt(MAX_FORWARD_SPACES);
      movePlayerWithAnimation(player, player.getPositionIndex() + spaces);
      notifyObservers("chanceeffect_forward_" + spaces);
    });

    chanceEffectHandlers.put(ChanceEffectType.BACKWARD_SMALL, player -> {
      int spaces = 1 + random.nextInt(MAX_BACKWARD_SPACES);
      movePlayerWithAnimation(player, player.getPositionIndex() - spaces);
      notifyObservers("chanceeffect_backward_" + spaces);
    });

    chanceEffectHandlers.put(ChanceEffectType.EXTRA_TURN, player -> {
      // Fix: Replace with appropriate method or handle differently
      player.setHasExtraTurn(true);  // Assuming this method exists
      notifyObservers("chanceeffect_extraturn");
    });

    chanceEffectHandlers.put(ChanceEffectType.SKIP_TURN, player -> {
      // Fix: Replace with appropriate method or handle differently
      player.setSkipTurn(true);  // Assuming this method exists
      notifyObservers("chanceeffect_loseturn");
    });

    chanceEffectHandlers.put(ChanceEffectType.TELEPORT_RANDOM, player -> {
      int randomTile = 1 + random.nextInt(getBoard().getLastTile() - 1);
      movePlayerWithAnimation(player, randomTile);
      notifyObservers("chanceeffect_teleport_" + randomTile);
    });
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

    CosmicChanceAction chanceAction = (CosmicChanceAction)tile.getTileAction();
    // Fix: Get effect type through appropriate method
    ChanceEffectType effectType = chanceAction.getType();  // Assuming this method exists

    // Notify observers before applying the effect
    notifyObservers("chanceactivated_" + tile.getIndex());

    // Apply the effect using the registered handler
    Consumer<Player> handler = chanceEffectHandlers.get(effectType);
    if (handler != null) {
      handler.accept(player);
      return false; // Wait for animation to complete
    }

    return true;
  }

  /**
   * Moves a player to a new position with animation.
   * Ensures the position is within valid board boundaries.
   *
   * @param player the player to move
   * @param newPosition the new position
   */
  private void movePlayerWithAnimation(Player player, int newPosition) {
    // Ensure position is within board boundaries
    int lastTile = getBoard().getLastTile();
    newPosition = Math.max(1, Math.min(newPosition, lastTile));

    // Don't move if already at position
    if (player.getPositionIndex() == newPosition) {
      return;
    }

    // Fix: Use getter method to access playerController
    getPlayerController().movePlayerToPosition(player, newPosition);
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
    if (position >= 0 && position < getBoard().getTiles().size()) {
      Tile currentTile = getBoard().getTiles().get(position);
      if (currentTile.hasChanceAction()) {
        handleChanceEffect(player, currentTile); // Your custom logic
      } else {
        currentTile.landOn(player);
      }
    }
  }
}