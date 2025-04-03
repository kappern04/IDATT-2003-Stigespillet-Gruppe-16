package edu.ntnu.iir.bidata;

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
    // Create and show the main menu
    MainMenu mainMenu = new MainMenu(stage);
    
    BoardGameFactory boardFactory = new BoardGameFactory();
    try {
      boardGame = boardFactory.createBoardGameFromFile("C:\\Users\\alexa\\Documents\\Skole\\IDATT2003\\1\\src\\main\\resources\\boards\\spiral.json");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    MainView mainView = new MainView(boardGame);
    mainView.setUpStage(stage);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}