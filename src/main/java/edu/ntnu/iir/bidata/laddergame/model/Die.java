package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import java.util.Random;

public class Die extends Observable<Die> {
  private static final int MIN_FACE = 1;
  private static final int MAX_FACE = 6;
  private int lastRoll;
  private int secondDieRoll;
  private final Random random;

    /**
     * Constructor for the Die class. Initializes the die with a random roll.
     */
  public Die() {
    this.lastRoll = 0;
    this.secondDieRoll = 0;
    this.random = new Random();
  }


  public int getLastRoll() {
    return lastRoll;
  }

  public int getSecondDieRoll() {
    return secondDieRoll;
  }

  public int getTotalRoll() {
    return lastRoll + secondDieRoll;
  }

  public void setLastRoll(int lastRoll) {
    if (lastRoll < MIN_FACE || lastRoll > MAX_FACE) {
      throw new IllegalArgumentException("Die face must be between " + MIN_FACE + " and " + MAX_FACE);
    }
    this.lastRoll = lastRoll;
    notifyObservers("VALUE_CHANGED");
  }

  public void setSecondDieRoll(int secondDieRoll) {
    if (secondDieRoll < MIN_FACE || secondDieRoll > MAX_FACE) {
      throw new IllegalArgumentException("Die face must be between " + MIN_FACE + " and " + MAX_FACE);
    }
    this.secondDieRoll = secondDieRoll;
    notifyObservers("VALUE_CHANGED");
  }

  public void roll() {
    this.lastRoll = random.nextInt(MAX_FACE) + MIN_FACE;
    notifyObservers("ROLL");
  }

  public void rollDouble() {
    this.lastRoll = random.nextInt(MAX_FACE) + MIN_FACE;
    this.secondDieRoll = random.nextInt(MAX_FACE) + MIN_FACE;
    notifyObservers("DOUBLE_ROLL");
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
    return "Die{lastRoll=" + lastRoll + ", secondDieRoll=" + secondDieRoll + '}';
  }
}