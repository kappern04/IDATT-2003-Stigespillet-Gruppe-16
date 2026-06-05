package edu.ntnu.iir.bidata.laddergame.util;

/**
 * Data transfer object for player information during creation.
 * The color is a hex string (e.g. "#00BFFF") so this stays free of JavaFX types.
 */
public class PlayerData {
    private final String name;
    private final String color;
    private int shipType;

    public PlayerData(String name, String color, int shipType) {
        this.name = name;
        this.color = color;
        this.shipType = shipType;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getShipType() {
        return shipType;
    }
}
