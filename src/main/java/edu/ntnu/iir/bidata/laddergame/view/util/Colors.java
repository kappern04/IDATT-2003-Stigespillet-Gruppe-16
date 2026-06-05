package edu.ntnu.iir.bidata.laddergame.view.util;

import javafx.scene.paint.Color;

/**
 * Converts between the model's hex color strings (e.g. "#00BFFF") and JavaFX
 * {@link Color}s. Keeps JavaFX color types at the view boundary, out of the model.
 */
public final class Colors {

    private static final Color DEFAULT = Color.WHITE;

    private Colors() {
        // Utility class: not instantiable.
    }

    /**
     * Parses a hex color string into a JavaFX {@link Color}.
     *
     * @param hex a hex color string such as "#00BFFF", or null
     * @return the parsed color, or white if the string is null/blank/invalid
     */
    public static Color toColor(String hex) {
        if (hex == null || hex.isBlank()) {
            return DEFAULT;
        }
        try {
            return Color.web(hex);
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }

    /**
     * Formats a JavaFX {@link Color} as a "#RRGGBB" hex string.
     *
     * @param color the color, or null
     * @return the hex string, or null if the color is null
     */
    public static String toHex(Color color) {
        if (color == null) {
            return null;
        }
        return String.format("#%02X%02X%02X",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255));
    }
}
