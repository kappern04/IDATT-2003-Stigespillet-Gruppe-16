package edu.ntnu.iir.bidata.laddergame.view.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Plays short board sound effects. Keeps audio (a view/output concern) out of the
 * model classes such as {@code LadderAction}.
 */
public final class SoundEffects {

    private static final Logger LOGGER = Logger.getLogger(SoundEffects.class.getName());

    private static MediaPlayer ladderUp;
    private static MediaPlayer ladderDown;

    private SoundEffects() {
        // Utility class: not instantiable.
    }

    /**
     * Plays the ladder/snake travel sound.
     *
     * @param climbingUp true when moving to a higher tile, false when moving down
     */
    public static void playLadder(boolean climbingUp) {
        MediaPlayer player = climbingUp ? ladderUpPlayer() : ladderDownPlayer();
        if (player != null) {
            player.stop();
            player.play();
        }
    }

    private static MediaPlayer ladderUpPlayer() {
        if (ladderUp == null) {
            ladderUp = load("portal.wav");
        }
        return ladderUp;
    }

    private static MediaPlayer ladderDownPlayer() {
        if (ladderDown == null) {
            ladderDown = load("portal2.wav");
        }
        return ladderDown;
    }

    private static MediaPlayer load(String fileName) {
        try {
            Media sound = new Media(SoundEffects.class.getResource("/audio/" + fileName).toExternalForm());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load sound: " + fileName, e);
            return null;
        }
    }
}
