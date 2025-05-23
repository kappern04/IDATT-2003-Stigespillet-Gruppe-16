package edu.ntnu.iir.bidata.laddergame.util;

/**
 * Represents different types of chance effects that can occur when players
 * land on chance tiles in the Cosmic Ladder game.
 */
public enum ChanceEffectType {
    // Positive effects
    FORWARD_SMALL("Rocket Boost", "Move forward 1-3 spaces", true),
    FORWARD_MEDIUM("Warp Drive", "Move forward 4-6 spaces", true),
    FORWARD_LARGE("Hyperspace Jump", "Move forward 7-10 spaces", true),
    EXTRA_TURN("Time Warp", "Take an extra turn", true),
    TELEPORT_RANDOM("Teleportation", "Teleport to a random tile", true),

    // Negative effects
    BACKWARD_SMALL("Asteroid Field", "Move back 1-3 spaces", false),
    BACKWARD_MEDIUM("Gravity Well", "Move back 4-6 spaces", false),
    BACKWARD_LARGE("Black Hole", "Move back 7-10 spaces", false),
    SKIP_TURN("Stasis Field", "Skip your next turn", false),
    RETURN_START("Wormhole Mishap", "Return to the starting tile", false);

    private final String name;
    private final String description;
    private final boolean positive;

    ChanceEffectType(String name, String description, boolean positive) {
        this.name = name;
        this.description = description;
        this.positive = positive;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPositive() {
        return positive;
    }
}