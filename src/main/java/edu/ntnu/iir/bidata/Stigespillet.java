package edu.ntnu.iir.bidata;


import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.file.BoardGameFactory;
import edu.ntnu.iir.bidata.view.MainView;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import edu.ntnu.iir.bidata.controller.BoardGame;

public class Stigespillet extends Application {

  private BoardGame boardGame;
  private MainView mainView;

  @Override
  public void start(Stage stage) {
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
