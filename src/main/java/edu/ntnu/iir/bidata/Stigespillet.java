package edu.ntnu.iir.bidata;


import edu.ntnu.iir.bidata.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import edu.ntnu.iir.bidata.controller.BoardGame;

public class Stigespillet extends Application {

  private BoardGame boardGame;
  private MainView mainView;

  @Override
  public void start(Stage stage) {
    boardGame = new BoardGame();
    MainView mainView = new MainView(boardGame);
    mainView.setUpStage(stage);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }

}
