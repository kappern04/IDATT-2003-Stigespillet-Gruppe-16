package edu.ntnu.iir.bidata.object;

/**
 * Player class
 * The player is created with a name and a position
 * The player can move a number of steps
 */
public class Player {
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
    }

    public void move(int steps) {
        this.position += steps;
    }

    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
