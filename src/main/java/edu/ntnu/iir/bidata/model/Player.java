package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import javafx.scene.paint.Color;

public class Player<T extends Observer> extends Observable<T> {

  private String name;
  private int positionIndex;
  private Color color;
    private int shipType;

  public Player(String name) {
    this.name = name;
    this.positionIndex = 0;
    this.color = null; // Default color will be assigned in PlayerView
    this.shipType = 1; // Default to Ship_1
  }

  public Player(String name, Color color) {
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = 1; // Default to Ship_1
  }

  // Add a new constructor that accepts shipType
  public Player(String name, Color color, int shipType) {
    this.name = name;
    this.positionIndex = 0;
    this.color = color;
    this.shipType = shipType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    notifyObservers();
  }

  public int getPositionIndex() {
    return positionIndex;
  }

  public void setPositionIndex(int positionIndex) {
    notifyObservers();
    this.positionIndex = positionIndex;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
    notifyObservers();
  }

  public int getShipType() {
    return shipType;
  }

  public void setShipType(int shipType) {
    this.shipType = shipType;
    notifyObservers();
  }

  public void move(int steps) {
    this.positionIndex += steps;
    notifyObservers();
  }

  public String toString() {
    return "Player{" + "name='" + name + '\'' + ", position=" + positionIndex + '}';
  }

  public void numPlayers(int number) {
    for (int i = 0; i < number; i++) {}
  }

  @Override
  public void addObserver(T observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer can not be null");
    }
    if (!super.getObservers().contains(observer)) {
      super.getObservers().add(observer);
    }
  }

  @Override
  public void removeObserver(T observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer can not be null");
    }
    if (super.getObservers().contains(observer)) {
      super.getObservers().remove(observer);
    }
  }

  @Override
  public void notifyObservers() {
    System.out.println(super.getObservers());
    super.getObservers().forEach(to -> to.update(this, "Move"));
  }
}
