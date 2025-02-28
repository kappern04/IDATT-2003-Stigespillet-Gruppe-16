package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.view.BoardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.ntnu.iir.bidata.controller.BoardGame;

public class Stigespillet extends Application {
  private BoardGame boardGame;
  private Label gameStatus;

  @Override
  public void start(Stage stage) {
    boardGame = new BoardGame();
    BoardView gameBoardGui = new BoardView(boardGame);

    gameStatus = new Label("Welcome to Stigespillet!");

    // Wrap layout in a VBox and set spacing
    VBox layout = new VBox(10, gameStatus, gameBoardGui.createGameBoard());
    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

    Scene scene = new Scene(layout);
    stage.setTitle("Stigespillet");
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
