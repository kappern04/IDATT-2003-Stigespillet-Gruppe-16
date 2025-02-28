package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DieView {
    private final Die die;
    private final ImageView imageView;

    public DieView(Die die) {
        this.die = die;
        this.imageView = new ImageView(getDieImage());
        imageView.setFitWidth(128);
        imageView.setFitHeight(128);
    }

    public Button createDieButton() {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setOnAction(e -> updateDie());
        return button;
    }

    private void updateDie() {
        die.roll();
        imageView.setImage(getDieImage());
    }

    private Image getDieImage() {
        return new Image(getClass().getResourceAsStream("/image/die_" + die.getLastRoll() + ".png"));
    }
}
