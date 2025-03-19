package edu.ntnu.iir.bidata.object;

import java.util.Random;

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
    Random rand = new Random();
    this.lastRoll = rand.nextInt(6) + 1;
  }

  public String toString() {
    return "Die{" + "lastRoll=" + lastRoll + '}';
  }
}
