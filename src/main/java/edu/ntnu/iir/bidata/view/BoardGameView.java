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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.transform.Scale;
import javafx.scene.layout.Pane;

/**
 * Main view class for the board game application.
 * Responsible for initializing and arranging all UI components,
 * including the board, side panels, control panel, and background.
 */
public class BoardGameView {
  private BoardGameController boardGameController;
  private BoardView boardView;
  private SidePanelView sidePanelView;
  private ControlPanel controlPanel;
  private MusicController musicController;
  private CSS css = new CSS();
  private StackPane boardPanel;
  private Scene scene;

  /**
   * Constructs a new BoardGameView with the given controller.
   *
   * @param boardGameController the main game controller
   */
  public BoardGameView(BoardGameController boardGameController) {
    this.boardGameController = boardGameController;

    BoardController boardController = new BoardController(
            boardGameController.getBoard(),
            boardGameController.getPlayers()
    );

    PlayerController playerController = new PlayerController(
            boardGameController.getBoard(),
            boardGameController.getPlayers()
    );

    MusicPlayer musicPlayer = new MusicPlayer("/audio/bgmusic.wav");
    this.musicController = new MusicController(musicPlayer);

    this.boardView = new BoardView(boardController);
    this.sidePanelView = new SidePanelView(boardGameController, playerController);
    this.controlPanel = new ControlPanel(boardGameController, musicController);
  }

  /**
   * Sets up the main application stage with all UI components and scaling.
   *
   * @param stage the primary stage to set up
   */
  public void setUpStage(Stage stage) {
    Node boardPanel = boardView.createBoardPanel();

    HBox sidePanels = sidePanelView.createSidePanels();
    VBox leftPanel = (VBox)sidePanels.getChildren().get(0);
    VBox rightPanel = (VBox)sidePanels.getChildren().get(1);

    Node controlPanelNode = controlPanel.createControlPanel();

    VBox centerLayout = new VBox(10);
    centerLayout.setStyle("-fx-padding: 20px;");
    centerLayout.getChildren().addAll(boardPanel, controlPanelNode);
    centerLayout.setAlignment(Pos.CENTER);

    HBox gameArea = new HBox(20);
    gameArea.setAlignment(Pos.CENTER);
    gameArea.getChildren().addAll(leftPanel, centerLayout, rightPanel);

    String boardName = boardGameController.getBoard().getBoardName();
    gameArea.setBackground(boardView.getBackgroundForBoard(boardName));

    final int initWidth = 1536;
    final int initHeight = 864;

    Pane scalingRoot = new Pane();
    gameArea.setPrefWidth(initWidth);
    gameArea.setPrefHeight(initHeight);
    scalingRoot.getChildren().add(gameArea);

    Scale scale = new Scale(1, 1, 0, 0);
    scale.xProperty().bind(scalingRoot.widthProperty().divide(initWidth));
    scale.yProperty().bind(scalingRoot.heightProperty().divide(initHeight));
    scalingRoot.getTransforms().add(scale);

    scene = new Scene(scalingRoot, initWidth, initHeight);
    css.applyDefaultStylesheet(scene);

    stage.setTitle("Cosmic Ladder - " + boardGameController.getBoard().getBoardName());
    stage.setScene(scene);
    stage.setMaximized(true);

    musicController.play();
  }
}