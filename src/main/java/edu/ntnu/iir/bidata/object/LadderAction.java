package edu.ntnu.iir.bidata.object;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** This class represents the action of moving up or down a ladder. */
public class LadderAction extends TileAction {
  private int destinationTile;


  public LadderAction(int destinationTile) {
    this.destinationTile = destinationTile;
  }

  @Override
  public void performAction(Player player) {
    playSound("portal.wav");
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
