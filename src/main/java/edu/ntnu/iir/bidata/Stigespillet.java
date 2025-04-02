package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.view.MainMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class Stigespillet extends Application {

  @Override
  public void start(Stage stage) {
    // Create and show the main menu
    MainMenu mainMenu = new MainMenu(stage);
  }

  public static void main(String[] args) {
    launch();
  }
}