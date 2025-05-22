package edu.ntnu.iir.bidata.laddergame;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Stigespillet extends Application {

  @Override
  public void start(Stage primaryStage) {
    System.out.println("Starting Stigespillet...");
    new MainMenuController(primaryStage);
  }

  public static void main(String[] args) {
    System.out.println("Main started");
    launch();
  }
}