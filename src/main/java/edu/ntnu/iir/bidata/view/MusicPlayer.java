package edu.ntnu.iir.bidata.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class MusicPlayer {
  private MediaPlayer mediaPlayer;
  private boolean isPlaying = false;

  public MusicPlayer(String path) {
    try {
      Media music = new Media(getClass().getResource(path).toExternalForm());
      mediaPlayer = new MediaPlayer(music);
      mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play() {
    if (!isPlaying) {
      isPlaying = true;
      mediaPlayer.play();
    }
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public void setVolume(double volume) {
    mediaPlayer.setVolume(volume);
  }

  public void pause() {
    if (isPlaying) {
      isPlaying = false;
      mediaPlayer.stop();
    }
  }
}
