package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.view.elements.CSS;
import edu.ntnu.iir.bidata.view.elements.InGameMenu;
import edu.ntnu.iir.bidata.view.elements.MusicControlPanel;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {
  private BoardGame boardGame;
  private BoardView boardView;
  private MusicPlayer musicPlayer;
  private MusicControlPanel musicControlPanel;
  private CSS css = new CSS();
  private static final Map<String, String> BOARD_BACKGROUNDS = new HashMap<>();
  static {
    BOARD_BACKGROUNDS.put("Spiral Way", "/image/background/background_1.png");
    BOARD_BACKGROUNDS.put("Ladderia Prime", "/image/background/background_2.png");
    BOARD_BACKGROUNDS.put("ZigZag Heights", "/image/background/background_3.png");
  }
  private static final String DEFAULT_BACKGROUND = "/image/default_background.png";


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

    // Get background for the current board using the map
    String boardName = boardGame.getBoard().getName();
    String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(boardName, DEFAULT_BACKGROUND);
    Background background = css.createSpaceBackground(backgroundPath);
    layout.setBackground(background);

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
    return css.createSpaceBackground("/image/background/background_1.png");
  }
}