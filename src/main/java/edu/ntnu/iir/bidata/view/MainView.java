package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Board;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {
  private BoardGame boardGame;
  private BoardView boardView;

  public MainView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.boardView = new BoardView(boardGame);
  }

  public Stage setUpStage(Stage stage) {
    // Wrap layout in a VBox and set spacing
    VBox layout = new VBox(10, boardView.createGameBoard());
    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");
    layout.setBackground(createBackground());

    Scene scene = new Scene(layout);
    stage.setTitle("Stigespillet");
    stage.setScene(scene);
    stage.setMaximized(true);
    return stage;
  }

  private Background createBackground() {
    Image image = new Image(getClass().getResourceAsStream("/image/background.png"));
    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    Background background = new Background(backgroundImage);
    return background;
  }
}
