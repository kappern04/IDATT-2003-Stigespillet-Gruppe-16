package edu.ntnu.iir.bidata.laddergame.controller.other;

import edu.ntnu.iir.bidata.laddergame.model.MusicPlayer;

public class MusicController {
    private final MusicPlayer musicPlayer;

    public MusicController(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
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