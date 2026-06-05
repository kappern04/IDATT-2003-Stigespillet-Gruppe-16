package edu.ntnu.iir.bidata.laddergame.controller.other;

import edu.ntnu.iir.bidata.laddergame.view.util.MusicPlayer;

/**
 * Coordinates background music playback. Decides <em>when</em> to play/pause and
 * delegates the actual audio output to the view-layer {@link MusicPlayer}, so the
 * controller stays free of JavaFX.
 */
public class MusicController {
    private final MusicPlayer musicPlayer;

    /**
     * Creates a music controller for the audio resource at the given classpath path.
     *
     * @param resourcePath classpath path to the music file (e.g. "/audio/bgmusic.wav")
     */
    public MusicController(String resourcePath) {
        this.musicPlayer = new MusicPlayer(resourcePath);
    }

    public void play() {
        musicPlayer.play();
    }

    public void pause() {
        musicPlayer.pause();
    }

    public void setVolume(double volume) {
        musicPlayer.setVolume(volume);
    }

    public boolean isPlaying() {
        return musicPlayer.isPlaying();
    }
}
