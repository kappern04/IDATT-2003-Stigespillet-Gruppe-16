package edu.ntnu.iir.bidata;

import com.sun.tools.javac.Main;
import edu.ntnu.iir.bidata.view.BoardView;
import edu.ntnu.iir.bidata.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
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
