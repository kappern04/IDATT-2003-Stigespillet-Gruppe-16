package edu.ntnu.iir.bidata.object;

import edu.ntnu.iir.bidata.view.Observer;

/**
 * Player class The player is created with a name and a position The player can move a number of
 * steps
 */
public class Player<T extends Observer> extends Observable<T> {

  private String name;
  private int position;

  public Player(String name) {
    this.name = name;
    this.position = 0;
  }

  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
    notifyObservers();
  }

  public void move(int steps) {
    this.position += steps;
    notifyObservers();
  }

  public String toString() {
    return "Player{" + "name='" + name + '\'' + ", position=" + position + '}';
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
    super.getObservers().forEach(to -> to.update(this, "MOVE"));
  }
}
