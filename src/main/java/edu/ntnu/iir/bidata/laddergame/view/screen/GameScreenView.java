package edu.ntnu.iir.bidata.laddergame.view.screen;

import edu.ntnu.iir.bidata.laddergame.controller.GameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.controller.other.MusicController;
import edu.ntnu.iir.bidata.laddergame.view.board.BoardView;
import edu.ntnu.iir.bidata.laddergame.view.board.SidePanelView;
import edu.ntnu.iir.bidata.laddergame.view.component.ControlPanel;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.dialog.WinPopup;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.transform.Scale;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Main view class for the board game application.
 * Responsible for initializing and arranging all UI components,
 * including the board, side panels, control panel, and background.
 */
public class GameScreenView {
  private final GameController boardGameController;
  private final BoardView boardView;
  private final SidePanelView sidePanelView;
  private final ControlPanel controlPanel;
  private final MusicController musicController;
  private final CSS css = new CSS();
  private Scene scene;

  public GameScreenView(GameController boardGameController) {
    this.boardGameController = boardGameController;

    boardGameController.setOnGameOver(() -> {
      Player winner = boardGameController.getWinner();
      if (winner != null) {
        WinPopup winPopup = new WinPopup(winner, () -> System.exit(0));
        winPopup.show();
      }
    });

    this.musicController = new MusicController("/audio/bgmusic.wav");

    this.boardView = new BoardView(
            boardGameController.getBoard(),
            boardGameController.getPlayers()
    );

    // Use a single PlayerController everywhere: the one BoardView registers as a
    // player observer and uses to animate sprites. Sharing it lets the side panel
    // and the game controller's isBusy() reflect the real animation state.
    PlayerController playerController = boardView.getPlayerController();
    this.sidePanelView = new SidePanelView(boardGameController, playerController);
    this.controlPanel = new ControlPanel(boardGameController, musicController);

    // Give the game controller the controllers that drive the visible animations,
    // so its turn flow correctly waits for them to finish.
    boardGameController.setPlayerController(playerController);
    boardGameController.setDieController(sidePanelView.getDieController());
  }


  public void setUpStage(Stage stage) {
    Node boardPanel = boardView.createBoardPanel();

    HBox sidePanels = sidePanelView.createSidePanels();
    VBox leftPanel = (VBox)sidePanels.getChildren().get(0);
    VBox rightPanel = (VBox)sidePanels.getChildren().get(1);

    Node controlPanelNode = controlPanel.createControlPanel(stage);

    VBox centerLayout = new VBox(10);
    centerLayout.setPadding(new Insets(20));
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

  public StackPane getBoardPane() {
    return (StackPane) boardView.createBoardPanel();
  }

}