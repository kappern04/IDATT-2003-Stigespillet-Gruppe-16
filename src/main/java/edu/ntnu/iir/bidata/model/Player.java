package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;

/**
 * Player class The player is created with a name and a position The player can move a number of
 * steps
 */
public class  Player<T extends Observer> extends Observable<T> {

  private String name;
  private int positionIndex;

  public Player(String name) {
    this.name = name;
    this.positionIndex = 0;
  }

  public String getName() {
    return name;
  }

  public int getPositionIndex() {
    return positionIndex;
  }

  public void setPositionIndex(int positionIndex) {
    notifyObservers();
    this.positionIndex = positionIndex;
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
    super.getObservers().forEach(to -> to.update(this, ""));
  }
}
