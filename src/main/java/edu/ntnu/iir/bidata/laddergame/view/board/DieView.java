package edu.ntnu.iir.bidata.laddergame.view.board;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DieView {
    private static final Logger LOGGER = Logger.getLogger(DieView.class.getName());
    private static final int DIE_SIZE = 96;
    private static final int MIN_FACE = 1;
    private static final int MAX_FACE = 6;
    private static final String DIE_IMAGE_PATH = "/image/die/die_%d.png";
    private static final String DEFAULT_DIE_IMAGE_PATH = "/image/die/die_0.png";

    private final ImageView imageView;

    public DieView() {
        this.imageView = new ImageView(getDieImage(0));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(DIE_SIZE);
        imageView.setFitHeight(DIE_SIZE);
    }

    public Button createDieButton(Runnable onClickAction) {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setMinSize(DIE_SIZE, DIE_SIZE);
        button.setPrefSize(DIE_SIZE, DIE_SIZE);
        button.setOnAction(e -> onClickAction.run());
        button.getStyleClass().add("die-button-enabled");
        button.setTooltip(new Tooltip("Click to roll the die"));
        button.getProperties().put("aria-label", "Roll die");
        return button;
    }

    public void updateDieImage(int faceValue) {
        if (faceValue < MIN_FACE || faceValue > MAX_FACE) {
            LOGGER.warning("Invalid die face value: " + faceValue);
            throw new IllegalArgumentException("Die face value must be between " + MIN_FACE + " and " + MAX_FACE);
        }
        imageView.setImage(getDieImage(faceValue));
    }

    public int setToRandomDieImage() {
        int randomRoll = (int) (Math.random() * MAX_FACE) + MIN_FACE;
        imageView.setImage(getDieImage(randomRoll));
        return randomRoll;
    }

    private Image getDieImage(int faceValue) {
        String path = String.format(DIE_IMAGE_PATH, faceValue);
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                return loadDefaultDieImage();
            }
            return new Image(stream);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading die image: " + path, e);
            return loadDefaultDieImage();
        }
    }

    private Image loadDefaultDieImage() {
        try (InputStream stream = getClass().getResourceAsStream(DEFAULT_DIE_IMAGE_PATH)) {
            return new Image(Objects.requireNonNull(stream, "Default die image not found"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load default die image", e);
            return null;
        }
    }

    public ImageView getDieImageView() {
        return imageView;
    }
}