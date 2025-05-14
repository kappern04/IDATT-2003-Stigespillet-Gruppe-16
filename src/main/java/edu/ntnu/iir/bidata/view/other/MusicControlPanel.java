package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.other.MusicController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MusicControlPanel {
  private final MusicController musicController;
  private Slider volumeSlider;
  private Button pauseButton;

  public MusicControlPanel(MusicController musicController) {
    this.musicController = musicController;
  }

  public HBox createControlPanel() {
    HBox controlPanel = new HBox(10);
    controlPanel.setAlignment(Pos.CENTER);

    volumeSlider = new Slider(0, 1, 0.5);
    volumeSlider.setPrefWidth(150);
    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            musicController.setVolume(newVal.doubleValue())
    );

    pauseButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/image/music/Pause.png"))));
    pauseButton.setOnAction(e -> togglePause());

    controlPanel.getChildren().addAll(volumeSlider, pauseButton);
    return controlPanel;
  }

  private void togglePause() {
    if (musicController.isPlaying()) {
      musicController.pause();
      pauseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/image/music/Play.png"))));
    } else {
      musicController.play();
      pauseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/image/music/Pause.png"))));
    }
  }
}