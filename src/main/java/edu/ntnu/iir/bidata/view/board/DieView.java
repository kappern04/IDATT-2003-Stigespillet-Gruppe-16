package edu.ntnu.iir.bidata.view.board;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

public class DieView {
    private final ImageView imageView;

    public DieView() {
        this.imageView = new ImageView(getDieImage(0)); // Default to 0
    }

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

    public void updateDieImage(int faceValue) {
        imageView.setImage(getDieImage(faceValue));
    }

    public void setToRandomDieImage() {
        int randomRoll = (int) (Math.random() * 6) + 1;
        imageView.setImage(getDieImage(randomRoll));
    }

    private Image getDieImage(int faceValue) {
        return new Image(getClass().getResourceAsStream("/image/die/die_" + faceValue + ".png"));
    }
}