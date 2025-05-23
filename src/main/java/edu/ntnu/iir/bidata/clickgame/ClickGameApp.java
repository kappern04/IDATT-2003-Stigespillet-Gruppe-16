package edu.ntnu.iir.bidata.clickgame;

import edu.ntnu.iir.bidata.clickgame.controller.ClickGameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClickGameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting ClickGame...");
        try {
            new ClickGameController().start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Main started");
        launch();
    }
}