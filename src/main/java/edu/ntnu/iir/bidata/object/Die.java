package edu.ntnu.iir.bidata.object;

/** Die class have last roll value and can be rolled. */
public class Die {
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
    this.lastRoll = (int) (Math.random() * 6) + 1;
  }

  public String toString() {
    return "Die{" + "lastRoll=" + lastRoll + '}';
  }
}
