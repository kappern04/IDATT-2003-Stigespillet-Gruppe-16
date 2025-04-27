package edu.ntnu.iir.bidata.controller.other;

import edu.ntnu.iir.bidata.model.MusicPlayer;

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

    public void loadSoundEffect(String name, String path) {
        musicPlayer.loadSoundEffect(name, path);
    }

    public void playSoundEffect(String name) {
        musicPlayer.playSoundEffect(name);
    }

    public void setSoundEffectVolume(String name, double volume) {
        musicPlayer.setSoundEffectVolume(name, volume);
    }
}