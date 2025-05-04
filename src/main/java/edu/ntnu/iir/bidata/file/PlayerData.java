package edu.ntnu.iir.bidata.file;

import javafx.scene.paint.Color;

/**
 * Data transfer object for player information during creation
 */
public class PlayerData {
    private final String name;
    private final Color color;
    private int shipType;

    public PlayerData(String name, Color color, int shipType) {
        this.name = name;
        this.color = color;
        this.shipType = shipType;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getShipType() {
        return shipType;
    }
}