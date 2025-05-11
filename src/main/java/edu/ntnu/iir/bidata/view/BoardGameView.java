package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.board.PlayerController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.model.MusicPlayer;
import edu.ntnu.iir.bidata.view.board.BoardView;
import edu.ntnu.iir.bidata.view.board.SidePanelView;
import edu.ntnu.iir.bidata.view.other.ControlPanel;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardGameView {
  private BoardGameController boardGameController;
  private BoardView boardView;
  private SidePanelView sidePanelView;
  private ControlPanel controlPanel;
  private MusicController musicController;
  private CSS css = new CSS();

  public BoardGameView(BoardGameController boardGameController) {
    this.boardGameController = boardGameController;

    // Create the board controller
    BoardController boardController = new BoardController(
            boardGameController.getBoard(),
            Arrays.asList(boardGameController.getPlayers())
    );

    // Create a single PlayerController instance to be shared
    PlayerController playerController = new PlayerController(
            boardGameController.getBoard(),
            boardGameController.getPlayers()
    );

    // Register the player controller as observer for all players
    for (var player : boardGameController.getPlayers()) {
      player.addObserver(playerController);
    }

    // Initialize music components
    MusicPlayer musicPlayer = new MusicPlayer("/audio/bgmusic.wav");
    this.musicController = new MusicController(musicPlayer);

    // Initialize view components
    this.boardView = new BoardView(boardController);
    this.sidePanelView = new SidePanelView(boardGameController, playerController);
    this.controlPanel = new ControlPanel(boardGameController, musicController);
  }

  public void setUpStage(Stage stage) {
    // Create main layout
    VBox mainLayout = new VBox(10);
    mainLayout.setStyle("-fx-padding: 20px;");
    mainLayout.getChildren().addAll(boardView.createBoardPanel(), controlPanel.createControlPanel());
    mainLayout.setAlignment(Pos.CENTER);

    // Create game area with board and side panel
    HBox gameArea = new HBox(20);
    gameArea.setAlignment(Pos.CENTER);
    gameArea.getChildren().addAll(mainLayout, sidePanelView.createControlPanel());

    // Set background
    String boardName = boardGameController.getBoard().getBoardName();
    gameArea.setBackground(boardView.getBackgroundForBoard(boardName));

    // Setup stage
    Scene scene = new Scene(gameArea);
    // Apply stylesheet
    css.applyDefaultStylesheet(scene);

    stage.setTitle("Cosmic Ladder - " + boardGameController.getBoard().getBoardName());
    stage.setScene(scene);
    stage.setMaximized(true);

    // Start music
    musicController.play();
  }
}