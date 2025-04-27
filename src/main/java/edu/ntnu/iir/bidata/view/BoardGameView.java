package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.other.InGameMenuController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.model.MusicPlayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.ntnu.iir.bidata.view.board.BoardView;
import edu.ntnu.iir.bidata.view.board.SidePanelView;
import edu.ntnu.iir.bidata.view.other.InGameMenu;
import edu.ntnu.iir.bidata.view.other.MusicControlPanel;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardGameView {
  private BoardGameController boardGameController;
  private BoardController boardController;
  private BoardView boardView;
  private SidePanelView sidePanelView;
  private MusicPlayer musicPlayer;
  private MusicController musicController;
  private MusicControlPanel musicControlPanel;
  private CSS css;
  private static final Map<String, String> BOARD_BACKGROUNDS = new HashMap<>();
  static {
    BOARD_BACKGROUNDS.put("Spiral Way", "/image/background/background_1.png");
    BOARD_BACKGROUNDS.put("Ladderia Prime", "/image/background/background_2.png");
    BOARD_BACKGROUNDS.put("ZigZag Heights", "/image/background/background_3.png");
  }
  private static final String DEFAULT_BACKGROUND = "/image/default_background.png";

  public BoardGameView(BoardGameController boardGameController) {
    this.boardGameController = boardGameController;

    // Create the board controller with the board and players from the game controller
    this.boardController = new BoardController(
            boardGameController.getBoard(),
            Arrays.asList(boardGameController.getPlayers())
    );

    // Now initialize the views with proper controllers
    this.boardView = new BoardView(boardController);
    this.sidePanelView = new SidePanelView(boardGameController);

    // Initialize music components
    this.musicPlayer = new MusicPlayer("/audio/bgmusic.wav");
    this.musicController = new MusicController(musicPlayer);
    this.musicControlPanel = new MusicControlPanel(musicController);
    this.css = new CSS();
  }

  public Stage setUpStage(Stage stage) {
    // Compose board and control panels in an HBox
    HBox mainLayout = new HBox(20, boardView.createBoardPanel(), sidePanelView.createControlPanel());
    mainLayout.setAlignment(Pos.CENTER);

    VBox layout = new VBox(10, mainLayout);
    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

    // Set background
    String boardName = boardGameController.getBoard().getBoardName();
    String backgroundPath = BOARD_BACKGROUNDS.getOrDefault(boardName, DEFAULT_BACKGROUND);
    Background background = css.createSpaceBackground(backgroundPath);
    layout.setBackground(background);

    // Menu button and music panel
    Button menuButton = css.createSpaceButton("Menu");
    menuButton.setOnAction(e -> showInGameMenu());

    HBox musicPanel = musicControlPanel.createControlPanel();
    musicPanel.setAlignment(Pos.TOP_LEFT);
    musicPanel.getChildren().add(menuButton);

    layout.getChildren().addAll(musicPanel);

    Scene scene = new Scene(layout);
    stage.setTitle("Cosmic Ladder - " + boardGameController.getBoard().getBoardName());
    stage.setScene(scene);
    stage.setMaximized(true);
    musicController.play();
    return stage;
  }

  private void showInGameMenu() {
    // Create controller with board game and music controller
    InGameMenuController controller = new InGameMenuController(boardGameController, musicController);
    // Pass controller to view
    InGameMenu menu = new InGameMenu(controller);
    menu.show();
  }
}