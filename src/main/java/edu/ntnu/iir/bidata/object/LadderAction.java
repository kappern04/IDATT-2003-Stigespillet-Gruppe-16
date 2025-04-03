package edu.ntnu.iir.bidata.object;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** This class represents the action of moving up or down a ladder. */
public class LadderAction extends TileAction {
  private int destinationTileIndex;


  public LadderAction(int destinationTileIndex) {
    this.destinationTileIndex = destinationTileIndex;
  }

  @Override
  public void performAction(Player player) {
    playSound("portal.wav");
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

  private void playSound(String soundFile) {
    try {
      Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
      MediaPlayer mediaPlayer = new MediaPlayer(sound);
      mediaPlayer.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
