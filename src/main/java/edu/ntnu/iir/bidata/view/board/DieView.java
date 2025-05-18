package edu.ntnu.iir.bidata.view.board;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

/**
 * View component for displaying and updating the die image.
 */
public class DieView {
    private final ImageView imageView;

    public DieView() {
        this.imageView = new ImageView(getDieImage(0)); // Default to 0
    }

    /**
     * Creates a die button with the die image and click action.
     * @param onClickAction Action to perform on click
     * @return Configured die button
     */
    public Button createDieButton(Runnable onClickAction) {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setMinSize(96, 96);
        button.setPrefSize(96, 96);
        button.setOnAction(event -> onClickAction.run());
        button.getStyleClass().add("die-button-enabled");
        return button;
    }

    /**
     * Updates the die image to the specified face value.
     * @param faceValue Die face value (1-6)
     */
    public void updateDieImage(int faceValue) {
        imageView.setImage(getDieImage(faceValue));
    }

    /**
     * Sets the die image to a random face (1-6).
     */
    public void setToRandomDieImage() {
        int randomRoll = (int) (Math.random() * 6) + 1;
        imageView.setImage(getDieImage(randomRoll));
    }

    /**
     * Loads the die image for the given face value.
     * @param faceValue Die face value (0-6)
     * @return Image for the die face, or a placeholder if not found
     */
    private Image getDieImage(int faceValue) {
        String path = "/image/die/die_" + faceValue + ".png";
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.err.println("Die image not found: " + path);
                return new Image(getClass().getResourceAsStream("/image/die/die_0.png"));
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading die image: " + e.getMessage());
            return new Image(getClass().getResourceAsStream("/image/die/die_0.png"));
        }
    }
}