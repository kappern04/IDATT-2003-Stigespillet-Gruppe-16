package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import java.util.Random;

public class Die extends Observable<Die> {
  private static final int MIN_FACE = 1;
  private static final int MAX_FACE = 6;
  private int lastRoll;

  public Die() {
    this.lastRoll = 0;
  }

  public int getLastRoll() {
    return lastRoll;
  }

  public void setLastRoll(int lastRoll) {
    if (lastRoll < MIN_FACE || lastRoll > MAX_FACE) {
      throw new IllegalArgumentException("Die face must be between " + MIN_FACE + " and " + MAX_FACE);
    }
    this.lastRoll = lastRoll;
    notifyObservers("VALUE_CHANGED");
  }

  public void roll() {
    this.lastRoll = new Random().nextInt(MAX_FACE) + MIN_FACE;
    notifyObservers("ROLL");
  }

  @Override
  public void addObserver(Observer<Die> observer) {
    super.addObserver(observer);
  }

  @Override
  public void removeObserver(Observer<Die> observer) {
    super.removeObserver(observer);
  }

  @Override
  public void notifyObservers() {
    notifyObservers("UPDATE");
  }

  public void notifyObservers(String eventType) {
    super.getObservers().forEach(o -> o.update(this, eventType));
  }

  @Override
  public String toString() {
    return "Die{lastRoll=" + lastRoll + '}';
  }
}