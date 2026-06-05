package edu.ntnu.iir.bidata.laddergame.view.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PixelArtUpscaler {

    /**
     * Upscales a BufferedImage using nearest-neighbor interpolation.
     *
     * @param input The input BufferedImage
     * @param scaleFactor The scale factor
     * @return The upscaled BufferedImage
     */
    public static BufferedImage upscale(BufferedImage input, int scaleFactor) {
        BufferedImage output = new BufferedImage(
                input.getWidth() * scaleFactor,
                input.getHeight() * scaleFactor,
                input.getType()
        );
        AffineTransform at = new AffineTransform();
        at.scale(scaleFactor, scaleFactor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        scaleOp.filter(input, output);
        return output;
    }

    /**
     * Resizes a JavaFX Image to the specified dimensions using pixel art scaling.
     *
     * @param image The JavaFX Image to resize
     * @param targetWidth Target width
     * @param targetHeight Target height
     * @return A new ImageView with the resized image
     */
    public static ImageView resizeImage(Image image, int targetWidth, int targetHeight) {
        // Calculate appropriate scale factor for pixel art
        int scaleFactor = Math.max(1, Math.min(
                targetWidth / (int)image.getWidth(),
                targetHeight / (int)image.getHeight())
        );

        // Convert to BufferedImage for manipulation
        BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);

        // Apply pixel art scaling if needed
        if (scaleFactor > 1) {
            buffered = upscale(buffered, scaleFactor);
        }

        // Convert back to JavaFX image
        Image resizedImage = SwingFXUtils.toFXImage(buffered, null);

        // Create and configure ImageView
        ImageView imageView = new ImageView(resizedImage);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        imageView.setPreserveRatio(true);

        return imageView;
    }
}