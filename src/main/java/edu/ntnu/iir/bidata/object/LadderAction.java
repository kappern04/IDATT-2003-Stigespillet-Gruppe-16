package edu.ntnu.iir.bidata.object;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** This class represents the action of moving up or down a ladder. */
public class LadderAction extends TileAction {
  private int destinationTile;
  private MediaPlayer mediaPlayerUp;
  private MediaPlayer mediaPlayerDown;

  public LadderAction(int destinationTile) {
    this.destinationTile = destinationTile;
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
    player.setPosition(destinationTile);
  }

  public int getDestinationTile() {
    return destinationTile;
  }

  public void setDestinationTile(int destinationTile) {
    this.destinationTile = destinationTile;
  }

  @Override
  public String toString() {
    return "LadderAction{" + "destinationTile=" + destinationTile + '}';
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