package edu.ntnu.iir.bidata.object;

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
    if (destinationTile > player.getPosition()) {
      playSound(mediaPlayerUp);
    } else {
      playSound(mediaPlayerDown);
    }
    player.setPositionIndex(destinationTileIndex);
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
}