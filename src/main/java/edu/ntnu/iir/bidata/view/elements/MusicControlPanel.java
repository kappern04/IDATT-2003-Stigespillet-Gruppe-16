package edu.ntnu.iir.bidata.view.elements;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.view.MusicPlayer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MusicControlPanel {
  private static MusicPlayer musicPlayer;
  private Slider volumeSlider;
  private Button pauseButton;
  private boolean isPaused = false;
  private BoardGame boardGame;
  // Remove the boardName field as it's redundant

  public MusicControlPanel(MusicPlayer musicPlayer, BoardGame boardGame) {
    this.musicPlayer = musicPlayer;
    this.boardGame = boardGame;
  }

  public HBox createControlPanel() {
    HBox controlPanel = new HBox(10);
    controlPanel.setAlignment(Pos.CENTER);

    volumeSlider = new Slider(0, 1, 0.5);
    volumeSlider.setShowTickMarks(false);
    volumeSlider.setShowTickLabels(false);
    volumeSlider.setPrefWidth(150);
    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!isPaused) {
        musicPlayer.setVolume(newValue.doubleValue());
      }
    });

    ImageView pauseIcon = new ImageView(
        new Image(getClass().getResourceAsStream("/image/Pause.png")));
    pauseIcon.setFitWidth(24);
    pauseIcon.setFitHeight(24);
    pauseButton = new Button("", pauseIcon);
    pauseButton.setOnAction(e -> togglePause());

    controlPanel.getChildren().addAll(volumeSlider, pauseButton);
    return controlPanel;
  }

  private void togglePause() {
    if (musicPlayer.isPlaying()) {
      musicPlayer.pause();
      pauseButton.setGraphic(
          new ImageView(new Image(getClass().getResourceAsStream("/image/Play.png"))));
    } else {
      musicPlayer.play();
      pauseButton.setGraphic(
          new ImageView(new Image(getClass().getResourceAsStream("/image/Pause.png"))));
    }
  }
  public static void stopMusic() {
    musicPlayer.pause();
  }
}