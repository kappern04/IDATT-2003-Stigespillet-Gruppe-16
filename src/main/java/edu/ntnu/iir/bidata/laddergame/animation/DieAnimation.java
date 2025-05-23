package edu.ntnu.iir.bidata.laddergame.animation;

import edu.ntnu.iir.bidata.laddergame.view.board.DieView;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DieAnimation {
    private static final Logger LOGGER = Logger.getLogger(DieAnimation.class.getName());
    private static final long ROLL_ANIMATION_DURATION_MS = 500;
    private static final long PAUSE_DURATION_MS = 500;
    private static final String SOUND_FILE_PATH = "dice-roll-sound.wav";
    private static final String SOUND_DIRECTORY = "/audio/";
    private static final double DEFAULT_VOLUME = 0.8;

    private final DieView dieView;
    private MediaPlayer currentPlayer;
    private AnimationTimer timer;
    private long animationStartTime;
    private long pauseDurationMs = PAUSE_DURATION_MS;


    public DieAnimation(DieView dieView) {
        this.dieView = dieView;
    }

    public void setPauseDuration(long pauseDurationMs) {
        this.pauseDurationMs = pauseDurationMs;
    }

    public void playRollAnimation(int finalValue, Runnable onComplete) {
        animationStartTime = System.currentTimeMillis();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - animationStartTime;
                if (elapsed < ROLL_ANIMATION_DURATION_MS) {
                    dieView.setToRandomDieImage();
                } else {
                    stop();
                    Platform.runLater(() -> dieView.updateDieImage(finalValue));
                    scheduleCompletionCallback(onComplete);
                }
            }
        };
        timer.start();
        playSound();
    }

    private void scheduleCompletionCallback(Runnable onComplete) {
        if (pauseDurationMs <= 0) {
            // Execute immediately if no pause needed
            Platform.runLater(onComplete);
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.millis(pauseDurationMs));
        pause.setOnFinished(event -> Platform.runLater(onComplete));
        pause.play();
    }

    private void playSound() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
        MediaPlayer player = createMediaPlayer(SOUND_FILE_PATH);
        if (player != null) {
            currentPlayer = player;
            player.setVolume(DEFAULT_VOLUME);
            player.setCycleCount(1);
            player.setOnEndOfMedia(() -> {
                player.stop();
                player.dispose();
                currentPlayer = null;
            });
            player.play();
        }
    }

    private MediaPlayer createMediaPlayer(String soundFile) {
        String resourcePath = SOUND_DIRECTORY + soundFile;
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) return null;
            Media sound = new Media(resourceUrl.toExternalForm());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load sound file: " + resourcePath, e);
            return null;
        }
    }

    public void dispose() {
        if (timer != null) timer.stop();
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }
}