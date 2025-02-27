package edu.ntnu.iir.bidata;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.ntnu.iir.bidata.view.BoardGameView;
import edu.ntnu.iir.bidata.controller.BoardGame;

public class Stigespillet extends Application {
  private BoardGame boardGame;
  private Label gameStatus;

  @Override
  public void start(Stage stage) {
    boardGame = new BoardGame();
    BoardGameView gameBoardGui = new BoardGameView(boardGame.getBoard());

    gameStatus = new Label("Welcome to Stigespillet!");
    Button rollButton = new Button("Roll Dice");

    rollButton.setOnAction(event -> onRollButtonClick());

    VBox layout = new VBox(10, gameStatus, rollButton, gameBoardGui.createGameBoard());
    Scene scene = new Scene(layout);

    stage.setTitle("Stigespillet");
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
  }

  private void onRollButtonClick() {
    boardGame.playTurn();
    gameStatus.setText(
        "Player positions: "
            + boardGame.getPlayers()[0].getPosition()
            + ", "
            + boardGame.getPlayers()[1].getPosition());
    if (boardGame.getPlayers()[0].getPosition() == 90) {
      gameStatus.setText("Player 1 has won the game!");
    } else if (boardGame.getPlayers()[1].getPosition() == 90) {
      gameStatus.setText("Player 2 has won the game!");
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
