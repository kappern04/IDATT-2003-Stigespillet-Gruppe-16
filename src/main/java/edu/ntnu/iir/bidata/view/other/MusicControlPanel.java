package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.other.MusicController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * UI panel for controlling music playback and volume.
 */
public class MusicControlPanel {
  private final MusicController musicController;
  private Slider volumeSlider;
  private Button pauseButton;
  private Image playImage;
  private Image pauseImage;

  public MusicControlPanel(MusicController musicController) {
    this.musicController = musicController;
    loadImages();
  }

  /**
   * Loads play and pause images from resources.
   */
  private void loadImages() {
    playImage = loadImage("/image/music/Play.png");
    pauseImage = loadImage("/image/music/Pause.png");
  }

  private Image loadImage(String path) {
    var stream = getClass().getResourceAsStream(path);
    if (stream == null) {
      throw new IllegalArgumentException("Image not found: " + path);
    }
    return new Image(stream);
  }

  /**
   * Creates the music control panel UI.
   * @return HBox containing the controls
   */
  public HBox createControlPanel() {
    HBox controlPanel = new HBox(10);
    controlPanel.setAlignment(Pos.CENTER);

    volumeSlider = new Slider(0, 1, 0.5);
    volumeSlider.setPrefWidth(150);
    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            musicController.setVolume(newVal.doubleValue())
    );

    pauseButton = new Button("", new ImageView(pauseImage));
    pauseButton.setFocusTraversable(false);
    pauseButton.setOnAction(e -> togglePause());

    controlPanel.getChildren().addAll(volumeSlider, pauseButton);
    return controlPanel;
  }

  /**
   * Toggles music playback and updates the button icon.
   */
  private void togglePause() {
    if (musicController.isPlaying()) {
      musicController.pause();
      pauseButton.setGraphic(new ImageView(playImage));
    } else {
      musicController.play();
      pauseButton.setGraphic(new ImageView(pauseImage));
    }
  }
}