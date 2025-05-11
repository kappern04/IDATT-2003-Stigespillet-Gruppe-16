package edu.ntnu.iir.bidata.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** This class represents the action of moving up or down a ladder. */
public class LadderAction extends TileAction {
  private MediaPlayer mediaPlayerUp;
  private MediaPlayer mediaPlayerDown;
  private int destinationTileIndex;

  public LadderAction(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
    this.mediaPlayerUp = createMediaPlayer("portal.wav");
    this.mediaPlayerDown = createMediaPlayer("portal2.wav");
  }

  @Override
  public void performAction(Player player) {
    // Just update position, but don't play sound yet
    player.setPositionIndex(destinationTileIndex);
  }

  /**
   * Plays the appropriate ladder sound based on whether it's an up or down ladder
   * This should be called after the animation completes
   */
  public void playLadderSound(int previousPosition) {
    if (destinationTileIndex > previousPosition) {
      playSound(mediaPlayerUp);
    } else {
      playSound(mediaPlayerDown);
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

  private MediaPlayer createMediaPlayer(String soundFile) {
    try {
      Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
      return new MediaPlayer(sound);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private void playSound(MediaPlayer mediaPlayer) {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.play();
    }
  }

  @Override
  public boolean leadsToPosition(Board board, int targetPosition) {
    return getDestinationTileIndex() == targetPosition;
  }
}