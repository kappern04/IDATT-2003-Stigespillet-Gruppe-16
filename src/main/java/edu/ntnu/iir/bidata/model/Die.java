package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import java.util.Random;

/** Die class have last roll value and can be rolled. */
public class Die <T extends Observer> extends Observable<T> {
  private int lastRoll;

  public Die() {
    this.lastRoll = 0;
  }

  public int getLastRoll() {
    return lastRoll;
  }

  public void setLastRoll(int lastRoll) {
    this.lastRoll = lastRoll;
  }

  public void roll() {
    Random rand = new Random();
    this.lastRoll = rand.nextInt(6) + 1;
    notifyObservers();
  }

  public String toString() {
    return "Die{" + "lastRoll=" + lastRoll + '}';
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
    super.getObservers().forEach(Observer::update);
  }
}
