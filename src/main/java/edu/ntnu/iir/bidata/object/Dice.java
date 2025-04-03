package edu.ntnu.iir.bidata.object;

import java.util.Random;
/**
 * Dice class The dice is thrown by the player to determine how many steps the player can move 1-6
 */
public class Dice {
  private int value;

  /** Constructor The dice is created with a random value between 1-6 */
  public Dice() {
      Random rng = new Random();
      this.value = rng.nextInt(6) + 1;
  }

  /**
   * Get the value of the dice
   *
   * @return the value of the dice
   */
  public int getValue() {
    return value;
  }

  /**
   * Set the value of the dice
   *
   * @param value the value of the dice
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * Get the string representation of the dice
   *
   * @return the string representation of the dice
   */
  public String toString() {
    return "Dice{" + "value=" + value + '}';
  }
}
