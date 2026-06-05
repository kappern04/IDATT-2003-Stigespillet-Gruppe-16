package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;

/**
 * Represents a player in the ladder game.
 * This class maintains player state including position, appearance, and special conditions.
 * It extends Observable to notify observers of changes to player state.
 */
public class Player extends Observable<Player> {
  private int id;
  private String name;
  private int positionIndex;
  private String color;
  private int shipType;
  private boolean isMoving;
  private boolean hasExtraTurn;
  private boolean chanceActivatedThisTurn;
  private static int nextId = 1;

  /**
   * Creates a new player with the specified name.
   *
   * @param name The player's name
   */
  public Player(String name) {
    this.id = nextId++;
    this.name = name;
    this.positionIndex = 0;
    this.color = null;
    this.shipType = 1;
    this.hasExtraTurn = false;
  }

  /**
   * Creates a new player with the specified name and color.
   *
   * @param name The player's name
   * @param color The player's color as a hex string (e.g. "#00BFFF")
   */
  public Player(String name, String color) {
    this.id = nextId++;
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = 1;
    this.hasExtraTurn = false;
  }

  /**
   * Creates a new player with the specified name, color, and ship type.
   *
   * @param name The player's name
   * @param color The player's color as a hex string (e.g. "#00BFFF")
   * @param shipType The type of ship representing the player
   */
  public Player(String name, String color, int shipType) {
    this.id = nextId++;
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = shipType;
    this.hasExtraTurn = false;
  }

  /**
   * Returns this player instance (used as a method reference by the click game).
   *
   * @return this player
   */
  public Player getPlayer() {
    return this;
  }

  /**
   * Gets the player's name.
   *
   * @return The player's name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the player's name and notifies observers.
   *
   * @param name The new name for the player
   */
  public void setName(String name) {
    this.name = name;
    notifyObservers("NAME_CHANGED");
  }

  /**
   * Gets the player's current position index on the game board.
   *
   * @return The position index
   */
  public int getPositionIndex() {
    return positionIndex;
  }

  /**
   * Checks if the player is currently in motion.
   *
   * @return True if the player is moving, false otherwise
   */
  public boolean isMoving() {
    return isMoving;
  }

  /**
   * Sets the player's moving state.
   *
   * @param moving True to set player as moving, false otherwise
   */
  public void setMoving(boolean moving) {
    isMoving = moving;
  }

  /**
   * Sets the player's position index and notifies observers if not currently moving.
   *
   * @param positionIndex The new position index
   */
  public void setPositionIndex(int positionIndex) {
    if (!isMoving) {
      this.positionIndex = positionIndex;
      notifyObservers("POSITION_CHANGED");
    } else {
      this.positionIndex = positionIndex;
    }
  }

  /**
   * Gets the player's color as a hex string (e.g. "#00BFFF").
   *
   * @return The player's color, or null if unset
   */
  public String getColor() {
    return color;
  }

  /**
   * Sets the player's color (a hex string, e.g. "#00BFFF") and notifies observers.
   *
   * @param color The new color for the player
   */
  public void setColor(String color) {
    this.color = color;
    notifyObservers("APPEARANCE_CHANGED");
  }

  /**
   * Gets the player's ship type.
   *
   * @return The ship type
   */
  public int getShipType() {
    return shipType;
  }

  /**
   * Sets the player's ship type and notifies observers.
   *
   * @param shipType The new ship type
   */
  public void setShipType(int shipType) {
    this.shipType = shipType;
    notifyObservers("APPEARANCE_CHANGED");
  }

  /**
   * Moves the player by the specified number of steps and notifies observers.
   * Sets the player as moving during this process.
   *
   * @param steps The number of steps to move (can be positive or negative)
   */
  public void move(int steps) {
    setMoving(true);
    this.positionIndex += steps;
    if (this.positionIndex < 0) {
      this.positionIndex = 0;
    }
    notifyObservers("POSITION_CHANGED");
  }

  /**
   * Checks if the player has an extra turn.
   *
   * @return True if the player has an extra turn, false otherwise
   */
  public boolean hasExtraTurn() {
    return hasExtraTurn;
  }

  /**
   * Sets whether the player has an extra turn and notifies observers.
   *
   * @param hasExtraTurn True to grant an extra turn, false otherwise
   */
  public void setExtraTurn(boolean hasExtraTurn) {
    this.hasExtraTurn = hasExtraTurn;
    notifyObservers("EXTRA_TURN_CHANGED");
  }

  /**
   * Checks whether a chance-tile effect has already been applied to this player
   * during the current turn.
   *
   * @return true if a chance tile has already triggered this turn, false otherwise
   */
  public boolean isChanceActivatedThisTurn() {
    return chanceActivatedThisTurn;
  }

  /**
   * Marks whether a chance-tile effect has been applied to this player during the
   * current turn. Used to prevent a chance effect that moves the player onto another
   * chance tile from triggering it again (no chance-tile chaining within one turn).
   *
   * @param chanceActivatedThisTurn true once a chance tile has triggered this turn
   */
  public void setChanceActivatedThisTurn(boolean chanceActivatedThisTurn) {
    this.chanceActivatedThisTurn = chanceActivatedThisTurn;
  }

  /**
   * Completes the player's movement, setting the moving flag to false and notifying observers.
   */
  public void finishMove() {
    setMoving(false);
    notifyObservers("MOVEMENT_COMPLETE");
  }

  /**
   * Gets the player's unique ID.
   *
   * @return The player's ID
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the player's ID and notifies observers.
   *
   * @param id The new ID for the player
   */
  public void setId(int id) {
    this.id = id;
    notifyObservers("ID_CHANGED");
  }

  /**
   * Returns a string representation of this player.
   *
   * @return String containing player name and position
   */
  @Override
  public String toString() {
    return "Player{name='" + name + "', position=" + positionIndex + "}";
  }

  /**
   * Adds an observer to this player.
   *
   * @param observer The observer to add
   */
  @Override
  public void addObserver(Observer<Player> observer) {
    super.addObserver(observer);
  }

  /**
   * Removes an observer from this player.
   *
   * @param observer The observer to remove
   */
  @Override
  public void removeObserver(Observer<Player> observer) {
    super.removeObserver(observer);
  }

  /**
   * Notifies all observers with a default "UPDATE" event type.
   */
  @Override
  public void notifyObservers() {
    notifyObservers("UPDATE");
  }

  /**
   * Notifies all observers with the specified event type.
   *
   * @param eventType The type of event that occurred
   */
  public void notifyObservers(String eventType) {
    super.getObservers().forEach(o -> o.update(this, eventType));
  }
}