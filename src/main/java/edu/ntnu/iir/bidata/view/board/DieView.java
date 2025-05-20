package edu.ntnu.iir.bidata.view.board;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View component for displaying and updating the die image.
 * Handles loading die face images and button creation.
 */
public class DieView {
    private static final Logger LOGGER = Logger.getLogger(DieView.class.getName());

    // Constants for die configuration
    private static final int DEFAULT_DIE_FACE = 0;
    private static final int MIN_VALID_FACE = 1;
    private static final int MAX_VALID_FACE = 6;
    private static final int DIE_SIZE = 96;
    private static final String DIE_IMAGE_PATH = "/image/die/die_%d.png";
    private static final String DEFAULT_DIE_IMAGE_PATH = "/image/die/die_0.png";

    private final ImageView imageView;

    /**
     * Creates a new DieView with default image.
     */
    public DieView() {
        this.imageView = new ImageView(getDieImage(DEFAULT_DIE_FACE));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(DIE_SIZE);
        imageView.setFitHeight(DIE_SIZE);
    }

    /**
     * Creates a die button with the die image and click action.
     *
     * @param onClickAction Action to perform on click
     * @return Configured die button with die image
     */
    public Button createDieButton(Runnable onClickAction) {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setMinSize(DIE_SIZE, DIE_SIZE);
        button.setPrefSize(DIE_SIZE, DIE_SIZE);
        button.setOnAction(event -> onClickAction.run());
        button.getStyleClass().add("die-button-enabled");

        // Add accessibility tooltip
        button.setTooltip(new Tooltip("Click to roll the die"));

        // Add ARIA attributes for screen readers
        button.getProperties().put("aria-label", "Roll die");

        return button;
    }

    /**
     * Updates the die image to the specified face value.
     *
     * @param faceValue Die face value (1-6)
     * @throws IllegalArgumentException if face value is not between 1 and 6
     */
    public void updateDieImage(int faceValue) {
        if (faceValue < MIN_VALID_FACE || faceValue > MAX_VALID_FACE) {
            LOGGER.warning("Invalid die face value: " + faceValue);
            throw new IllegalArgumentException(
                    "Die face value must be between " + MIN_VALID_FACE + " and " + MAX_VALID_FACE
            );
        }
        imageView.setImage(getDieImage(faceValue));
        LOGGER.fine("Die updated to face value: " + faceValue);
    }

    /**
     * Sets the die image to a random face (1-6).
     *
     * @return The randomly selected face value (1-6)
     */
    public int setToRandomDieImage() {
        int randomRoll = (int) (Math.random() * MAX_VALID_FACE) + MIN_VALID_FACE;
        imageView.setImage(getDieImage(randomRoll));
        LOGGER.fine("Die set to random value: " + randomRoll);
        return randomRoll;
    }

    /**
     * Loads the die image for the given face value.
     *
     * @param faceValue Die face value (0-6)
     * @return Image for the die face, or a placeholder if not found
     */
    private Image getDieImage(int faceValue) {
        String path = String.format(DIE_IMAGE_PATH, faceValue);

        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                LOGGER.warning("Die image not found: " + path);
                return loadDefaultDieImage();
            }
            return new Image(stream);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading die image: " + path, e);
            return loadDefaultDieImage();
        }
    }

    /**
     * Loads the default die image (placeholder).
     *
     * @return Default die image
     */
    private Image loadDefaultDieImage() {
        try (InputStream stream = getClass().getResourceAsStream(DEFAULT_DIE_IMAGE_PATH)) {
            return new Image(Objects.requireNonNull(stream, "Default die image not found"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load default die image", e);
            // Create a minimal blank image as last resort
            return new Image(Arrays.toString(new byte[0]));
        }
    }

    /**
     * Gets the current die image view.
     *
     * @return The ImageView displaying the die
     */
    public ImageView getDieImageView() {
        return imageView;
    }

    /**
     * Checks if any animation is currently running.
     * This implementation always returns false since animations are handled externally.
     *
     * @return false, as animations are managed by the controller
     */
    public boolean isAnimationRunning() {
        return false;
    }
}