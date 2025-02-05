package org.example.stigespillet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.stigespillet.gui.GameBoardGui;
import org.example.stigespillet.object.BoardGame;

public class Stigespillet extends Application {
    private BoardGame boardGame;
    private Label gameStatus;

    @Override
    public void start(Stage stage) {
        boardGame = new BoardGame();
        GameBoardGui gameBoardGui = new GameBoardGui(boardGame.getBoard());

        gameStatus = new Label("Welcome to Stigespillet!");
        Button rollButton = new Button("Roll Dice");

        rollButton.setOnAction(event -> onRollButtonClick());

        VBox layout = new VBox(10, gameStatus, rollButton, gameBoardGui.createGameBoard());
        Scene scene = new Scene(layout, 320, 240);

        stage.setTitle("Stigespillet");
        stage.setScene(scene);
        stage.show();
    }

    private void onRollButtonClick() {
        boardGame.playTurn();
        gameStatus.setText("Player positions: " + boardGame.getPlayers()[0].getPosition() + ", " + boardGame.getPlayers()[1].getPosition());
    }

    public static void main(String[] args) {
        launch();
    }
}