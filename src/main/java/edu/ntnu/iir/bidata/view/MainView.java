package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.file.GameSaveWriterCSV;
import edu.ntnu.iir.bidata.view.elements.InGameMenu;
import edu.ntnu.iir.bidata.view.elements.MusicControlPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {
  private BoardGame boardGame;
  private BoardView boardView;
  private MusicPlayer musicPlayer;
  private MusicControlPanel musicControlPanel;

  public MainView(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.boardView = new BoardView(boardGame);
    this.musicPlayer = new MusicPlayer("/audio/bgmusic.wav");
    this.musicControlPanel = new MusicControlPanel(musicPlayer, boardGame);
  }

  public Stage setUpStage(Stage stage) {
    // Wrap layout in a VBox and set spacing
    VBox layout = new VBox(10, boardView.createGameBoard());
    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");
    layout.setBackground(createBackground());

    // Create menu button
    Button menuButton = new Button("Menu");
    menuButton.setOnAction(e -> showInGameMenu());

    HBox musicPanel = musicControlPanel.createControlPanel();
    musicPanel.setAlignment(Pos.TOP_LEFT);
    musicPanel.getChildren().add(menuButton);

    layout.getChildren().addAll(musicPanel);

    Scene scene = new Scene(layout);
    stage.setTitle("Cosmic Ladder - " + boardGame.getBoard().getName());
    stage.setScene(scene);
    stage.setMaximized(true);
    musicPlayer.play();
    return stage;
  }

  private void showInGameMenu() {
    InGameMenu menu = new InGameMenu(boardGame);
    menu.show();
  }

  private Background createBackground() {
    Image image = new Image(getClass().getResourceAsStream("/image/background.png"));
    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    Background background = new Background(backgroundImage);
    return background;
  }
}