package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.controller.other.MainMenuController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Stigespillet extends Application {

  @Override
  public void start(Stage primaryStage) {
    new MainMenuController(primaryStage);
  }

  public static void main(String[] args) {
    launch();
  }
}