package edu.ntnu.iir.bidata.laddergame.view.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View-layer audio output: wraps a JavaFX {@link MediaPlayer} for looping music.
 * Keeping the JavaFX media here means controllers can coordinate playback without
 * depending on JavaFX themselves.
 */
public class MusicPlayer {
    private static final Logger LOGGER = Logger.getLogger(MusicPlayer.class.getName());

    private MediaPlayer mediaPlayer;
    private boolean playing = false;

    /**
     * Creates a looping music player for the audio resource at the given classpath path.
     *
     * @param resourcePath classpath path to the music file (e.g. "/audio/bgmusic.wav")
     */
    public MusicPlayer(String resourcePath) {
        try {
            Media music = new Media(getClass().getResource(resourcePath).toExternalForm());
            mediaPlayer = new MediaPlayer(music);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load music: " + resourcePath, e);
        }
    }

    public void play() {
        if (!playing && mediaPlayer != null) {
            playing = true;
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (playing && mediaPlayer != null) {
            playing = false;
            mediaPlayer.stop();
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}
