package edu.ntnu.iir.bidata.view.elements;

import edu.ntnu.iir.bidata.view.MusicPlayer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MusicControlPanel {

  private MusicPlayer musicPlayer;
  private Slider volumeSlider;
  private Button pauseButton;
  private Button menuButton;
  private boolean isPaused = false;

  public MusicControlPanel(MusicPlayer musicPlayer) {
    this.musicPlayer = musicPlayer;
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

    menuButton = new Button("Menu");
    menuButton.setOnAction(e -> {
      openMenu();
    });

    controlPanel.getChildren().addAll(volumeSlider, pauseButton, menuButton);
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

  private void openMenu() {
    new InGameMenu().show();
  }
}
