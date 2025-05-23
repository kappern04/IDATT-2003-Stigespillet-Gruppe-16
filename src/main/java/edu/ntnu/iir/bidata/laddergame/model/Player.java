package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import javafx.scene.paint.Color;

public class Player extends Observable<Player> {
  private String name;
  private int positionIndex;
  private Color color;
  private int shipType;
  private boolean isMoving;

  public Player(String name) {
    this.name = name;
    this.positionIndex = 0;
    this.color = null;
    this.shipType = 1;
  }

  public Player(String name, Color color) {
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = 1;
  }

  public Player(String name, Color color, int shipType) {
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = shipType;
  }

  public Player getPlayer() {
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    notifyObservers("NAME_CHANGED");
  }

  public int getPositionIndex() {
    return positionIndex;
  }

  public boolean isMoving() {
    return isMoving;
  }

  public void setMoving(boolean moving) {
    isMoving = moving;
  }

  public void setPositionIndex(int positionIndex) {
    if (!isMoving) {
      this.positionIndex = positionIndex;
      notifyObservers("POSITION_CHANGED");
    } else {
      this.positionIndex = positionIndex;
    }
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
    notifyObservers("APPEARANCE_CHANGED");
  }

  public int getShipType() {
    return shipType;
  }

  public void setShipType(int shipType) {
    this.shipType = shipType;
    notifyObservers("APPEARANCE_CHANGED");
  }

  public void move(int steps) {
    setMoving(true);
    this.positionIndex += steps;
    notifyObservers("POSITION_CHANGED");
  }

  public void finishMove() {
    setMoving(false);
    notifyObservers("MOVEMENT_COMPLETE");
  }

  @Override
  public String toString() {
    return "Player{name='" + name + "', position=" + positionIndex + "}";
  }

  @Override
  public void addObserver(Observer<Player> observer) {
    super.addObserver(observer);
  }

  @Override
  public void removeObserver(Observer<Player> observer) {
    super.removeObserver(observer);
  }

  @Override
  public void notifyObservers() {
    notifyObservers("UPDATE");
  }

  public void notifyObservers(String eventType) {
    super.getObservers().forEach(o -> o.update(this, eventType));
  }
}