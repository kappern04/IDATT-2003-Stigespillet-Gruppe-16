package edu.ntnu.iir.bidata.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
  private MediaPlayer mediaPlayer;
  private boolean isPlaying = false;
  private Map<String, MediaPlayer> soundEffects;

  public MusicPlayer(String path) {
    try {
      Media music = new Media(getClass().getResource(path).toExternalForm());
      mediaPlayer = new MediaPlayer(music);
      mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      soundEffects = new HashMap<>();
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

  public void loadSoundEffect(String name, String path) {
    try {
      Media sound = new Media(getClass().getResource(path).toExternalForm());
      MediaPlayer player = new MediaPlayer(sound);
      soundEffects.put(name, player);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void playSoundEffect(String name) {
    MediaPlayer player = soundEffects.get(name);
    if (player != null) {
      player.stop();
      player.play();
    }
  }

  public void setSoundEffectVolume(String name, double volume) {
    MediaPlayer player = soundEffects.get(name);
    if (player != null) {
      player.setVolume(volume);
    }
  }
}