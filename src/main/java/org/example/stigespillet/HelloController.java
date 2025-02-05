package org.example.stigespillet;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.stigespillet.object.Dice;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText((new Dice()).toString());
    }
}