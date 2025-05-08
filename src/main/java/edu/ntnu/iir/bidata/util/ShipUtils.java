package edu.ntnu.iir.bidata.util;

import java.util.Objects;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ShipUtils {
    public static final int SPRITE_SIZE = 32;
    private static final String SHIP_SPRITE_PATH = "/image/player/Ship_%d.png";

    public static Image loadShipSprite(int shipType) {
        try {
            String path = String.format(SHIP_SPRITE_PATH, shipType);
            return new Image(Objects.requireNonNull(
                    ShipUtils.class.getResourceAsStream(path),
                    "Could not load ship sprite"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ship sprite image", e);
        }
    }

    public static ImageView createColoredShipImage(Color targetColor, Image baseSprite) {
        // Create a writable copy of the base sprite
        int width = (int)baseSprite.getWidth();
        int height = (int)baseSprite.getHeight();
        WritableImage coloredImage = new WritableImage(width, height);
        PixelReader reader = baseSprite.getPixelReader();
        PixelWriter writer = coloredImage.getPixelWriter();

        // Get target hue from player color
        double targetHue = targetColor.getHue();

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = reader.getColor(x, y);

                // Keep transparent pixels unchanged
                if (pixel.getOpacity() < 0.1) {
                    writer.setColor(x, y, pixel);
                    continue;
                }

                // Only recolor non-black, non-white pixels
                if (pixel.getSaturation() > 0.15) {
                    // Replace hue while keeping saturation and brightness
                    Color newColor = Color.hsb(
                            targetHue,
                            pixel.getSaturation(),
                            pixel.getBrightness(),
                            pixel.getOpacity()
                    );
                    writer.setColor(x, y, newColor);
                } else {
                    // Keep grayscale pixels unchanged
                    writer.setColor(x, y, pixel);
                }
            }
        }

        // Create ImageView with the colored image
        ImageView imageView = new ImageView(coloredImage);
        imageView.setFitWidth(SPRITE_SIZE);
        imageView.setFitHeight(SPRITE_SIZE);

        // Add a glow effect with the player's color
        DropShadow glow = new DropShadow();
        glow.setColor(targetColor);
        glow.setRadius(15);
        glow.setSpread(0.3);
        imageView.setEffect(glow);

        return imageView;
    }

    public static ImageView createColoredShipImage(Color targetColor, int shipType) {
        Image baseSprite = loadShipSprite(shipType);
        return createColoredShipImage(targetColor, baseSprite);
    }
}