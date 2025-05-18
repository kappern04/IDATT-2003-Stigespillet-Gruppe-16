package edu.ntnu.iir.bidata.view.util;

import java.util.Objects;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility class for loading and coloring ship sprites.
 */
public final class ShipUtils {
    public static final int SPRITE_SIZE = 32;
    private static final String SHIP_SPRITE_PATH = "/image/player/Ship_%d.png";
    private static final double TRANSPARENCY_THRESHOLD = 0.1;
    private static final double SATURATION_THRESHOLD = 0.15;
    private static final double GLOW_RADIUS = 15.0;
    private static final double GLOW_SPREAD = 0.3;

    private ShipUtils() {
        // Prevent instantiation
    }

    /**
     * Loads a ship sprite image for the given ship type.
     *
     * @param shipType the ship type index
     * @return the loaded Image
     * @throws IllegalArgumentException if the image cannot be loaded
     */
    public static Image loadShipSprite(int shipType) {
        String path = String.format(SHIP_SPRITE_PATH, shipType);
        try {
            var stream = ShipUtils.class.getResourceAsStream(path);
            if (stream == null) {
                throw new IllegalArgumentException("Could not load ship sprite: " + path);
            }
            return new Image(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ship sprite image: " + path, e);
        }
    }

    /**
     * Creates a colored ImageView of the ship sprite using the target color.
     *
     * @param targetColor the color to apply
     * @param baseSprite the base ship sprite image
     * @return an ImageView with the colored ship and glow effect
     */
    public static ImageView createColoredShipImage(Color targetColor, Image baseSprite) {
        Objects.requireNonNull(targetColor, "Target color cannot be null");
        Objects.requireNonNull(baseSprite, "Base sprite cannot be null");

        int width = (int) baseSprite.getWidth();
        int height = (int) baseSprite.getHeight();
        WritableImage coloredImage = new WritableImage(width, height);
        PixelReader reader = baseSprite.getPixelReader();
        PixelWriter writer = coloredImage.getPixelWriter();

        if (reader == null) {
            throw new IllegalArgumentException("Base sprite has no pixel reader");
        }

        double targetHue = targetColor.getHue();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = reader.getColor(x, y);

                if (pixel.getOpacity() < TRANSPARENCY_THRESHOLD) {
                    writer.setColor(x, y, pixel);
                } else if (pixel.getSaturation() > SATURATION_THRESHOLD) {
                    Color newColor = Color.hsb(
                            targetHue,
                            pixel.getSaturation(),
                            pixel.getBrightness(),
                            pixel.getOpacity()
                    );
                    writer.setColor(x, y, newColor);
                } else {
                    writer.setColor(x, y, pixel);
                }
            }
        }

        ImageView imageView = new ImageView(coloredImage);
        imageView.setFitWidth(SPRITE_SIZE);
        imageView.setFitHeight(SPRITE_SIZE);

        DropShadow glow = new DropShadow();
        glow.setColor(targetColor);
        glow.setRadius(GLOW_RADIUS);
        glow.setSpread(GLOW_SPREAD);
        imageView.setEffect(glow);

        return imageView;
    }

    /**
     * Loads and colors a ship sprite for the given type and color.
     *
     * @param targetColor the color to apply
     * @param shipType the ship type index
     * @return an ImageView with the colored ship
     */
    public static ImageView createColoredShipImage(Color targetColor, int shipType) {
        Image baseSprite = loadShipSprite(shipType);
        return createColoredShipImage(targetColor, baseSprite);
    }
}