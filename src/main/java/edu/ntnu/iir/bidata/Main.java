package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.laddergame.Stigespillet;
import edu.ntnu.iir.bidata.clickgame.ClickGameApp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button ladderGameBtn = new Button("Ladder Game");
        Button clickGameBtn = new Button("Click Game");

        ladderGameBtn.setOnAction(e -> {
            primaryStage.close();
            Stigespillet.main(new String[0]);
        });

        clickGameBtn.setOnAction(e -> {
            primaryStage.close();
            ClickGameApp.main(new String[0]);
        });

        VBox root = new VBox(20, ladderGameBtn, clickGameBtn);
        root.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #232946;");

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Choose a Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}