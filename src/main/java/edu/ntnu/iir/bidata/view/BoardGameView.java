package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.BoardController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.model.MusicPlayer;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.util.Arrays;

import edu.ntnu.iir.bidata.view.board.BoardView;
import edu.ntnu.iir.bidata.view.board.SidePanelView;
import edu.ntnu.iir.bidata.view.other.ControlPanel;
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

    // Initialize music components
    MusicPlayer musicPlayer = new MusicPlayer("/audio/bgmusic.wav");
    this.musicController = new MusicController(musicPlayer);

    // Initialize view components
    this.boardView = new BoardView(boardController);
    this.sidePanelView = new SidePanelView(boardGameController);
    this.controlPanel = new ControlPanel(boardGameController, musicController);


  }

  public Stage setUpStage(Stage stage) {
    // Create main layout
    VBox mainLayout = new VBox(10);
    mainLayout.setStyle("-fx-padding: 20px;");

    // Create game area with board and side panel
    HBox gameArea = new HBox(20);
    gameArea.setAlignment(Pos.CENTER);
    gameArea.getChildren().addAll(boardView.createBoardPanel(), sidePanelView.createControlPanel());

    // Create control panel and set to align left
    HBox controlPanelBox = controlPanel.createControlPanel();
    controlPanelBox.setAlignment(Pos.CENTER_LEFT);

    // Add game area FIRST
    mainLayout.getChildren().add(gameArea);

    // Add control panel SECOND (to place it under the board)
    mainLayout.getChildren().add(controlPanelBox);

    // Set background
    String boardName = boardGameController.getBoard().getBoardName();
    mainLayout.setBackground(boardView.getBackgroundForBoard(boardName));

    // Setup stage
    Scene scene = new Scene(mainLayout);
    // Apply stylesheet
    css.applyDefaultStylesheet(scene);

    stage.setTitle("Cosmic Ladder - " + boardGameController.getBoard().getBoardName());
    stage.setScene(scene);
    stage.setMaximized(true);

    // Start music
    musicController.play();

    return stage;
  }
}