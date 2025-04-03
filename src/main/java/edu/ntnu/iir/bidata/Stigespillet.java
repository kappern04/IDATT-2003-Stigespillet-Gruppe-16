package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.view.MainMenu;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.file.BoardGameFactory;
import edu.ntnu.iir.bidata.view.MainView;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class Stigespillet extends Application {

  @Override
  public void start(Stage stage) {
    MainMenu mainMenu = new MainMenu(stage);
  }

  public static void main(String[] args) {
    launch();
  }
}