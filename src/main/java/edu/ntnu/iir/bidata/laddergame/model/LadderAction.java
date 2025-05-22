package edu.ntnu.iir.bidata.laddergame.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class LadderAction extends DefaultTileAction {
  private MediaPlayer mediaPlayerUp;
  private MediaPlayer mediaPlayerDown;
  private int destinationTileIndex;

  public LadderAction(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
    this.mediaPlayerUp = createMediaPlayer("portal.wav");
    this.mediaPlayerDown = createMediaPlayer("portal2.wav");
  }

  @Override
  public void execute(Player player) {
    player.setPositionIndex(destinationTileIndex);
  }

  @Override
  public String getDescription() {
    if (destinationTileIndex > 0) {
      return "Ladder to position " + destinationTileIndex;
    } else {
      return "Snake to position " + destinationTileIndex;
    }
  }

  public int getDestinationTileIndex() {
    return destinationTileIndex;
  }

  public void setDestinationTileIndex(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
  }

  @Override
  public String toString() {
    return "LadderAction{" + "destinationTile=" + destinationTileIndex + '}';
  }

  public void playLadderSound(int previousPosition) {
    if (destinationTileIndex > previousPosition) {
      playSound(mediaPlayerUp);
    } else {
      playSound(mediaPlayerDown);
    }
  }

  private void playSound(MediaPlayer mediaPlayer) {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.play();
    }
  }

  private MediaPlayer createMediaPlayer(String soundFile) {
    try {
      Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
      return new MediaPlayer(sound);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean leadsToPosition(Board board, int targetPosition) {
    return getDestinationTileIndex() == targetPosition;
  }
}