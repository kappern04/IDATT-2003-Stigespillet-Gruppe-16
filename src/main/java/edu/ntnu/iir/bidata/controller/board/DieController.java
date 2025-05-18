package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import edu.ntnu.iir.bidata.view.board.DieView;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Controller for the die component of the game.
 * Handles die rolling animation, sound effects, and observer updates from the die model.
 */
public class DieController implements Observer {
    private static final long ROLL_ANIMATION_DURATION_MS = 500;
    private static final long PAUSE_DURATION_MS = 500;
    private static final String SOUND_FILE_PATH = "dice-roll-sound.wav";

    private final Die die;
    private final DieView dieView;
    private final AnimationTimer timer;

    private long animationStartTime;
    private Runnable onAnimationComplete;

    /**
     * Creates a new die controller.
     *
     * @param die The die model to control
     * @param dieView The view component for the die
     * @throws NullPointerException if die or dieView is null
     */
    public DieController(Die die, DieView dieView) {
        this.die = Objects.requireNonNull(die, "Die cannot be null");
        this.dieView = Objects.requireNonNull(dieView, "DieView cannot be null");
        this.timer = createAnimationTimer();
        this.die.addObserver(this);
    }

    /**
     * Sets a callback to run after the die animation completes.
     *
     * @param callback The callback to run
     */
    public void setOnAnimationComplete(Runnable callback) {
        this.onAnimationComplete = callback;
    }

    /**
     * Gets the die model.
     *
     * @return The die model
     */
    public Die getDie() {
        return die;
    }

    /**
     * Handles a die roll action with an optional callback.
     *
     * @param afterRollAction Action to perform after the roll completes
     */
    public void handleDieRoll(Runnable afterRollAction) {
        if (afterRollAction != null) {
            this.onAnimationComplete = afterRollAction;
        }
        startRollingAnimation();
    }

    /**
     * Starts the die rolling animation and sound.
     */
    private void startRollingAnimation() {
        animationStartTime = System.currentTimeMillis();
        timer.start();
        playSound();
    }

    /**
     * Creates the animation timer that handles die face changes during roll.
     *
     * @return A configured AnimationTimer
     */
    private AnimationTimer createAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - animationStartTime;
                if (elapsed < ROLL_ANIMATION_DURATION_MS) {
                    dieView.setToRandomDieImage();
                } else {
                    stop();
                    dieView.updateDieImage(die.getLastRoll());

                    PauseTransition pause = new PauseTransition(Duration.millis(PAUSE_DURATION_MS));
                    pause.setOnFinished(event -> {
                        if (onAnimationComplete != null) {
                            onAnimationComplete.run();
                            onAnimationComplete = null; // Clear callback after use
                        }
                    });
                    pause.play();
                }
            }
        };
    }

    /**
     * Plays the die roll sound effect.
     */
    private void playSound() {
        MediaPlayer player = createMediaPlayer(SOUND_FILE_PATH);
        if (player != null) {
            player.setCycleCount(1);
            player.setOnEndOfMedia(() -> {
                player.stop();
                player.dispose();
            });
            player.setOnError(() -> {
                System.err.println("Media error: " + player.getError());
                player.dispose();
            });
            player.play();
        }
    }

    /**
     * Creates a media player for the given sound file.
     *
     * @param soundFile The name of the sound file in the resources folder
     * @return A configured MediaPlayer or null if the sound couldn't be loaded
     */
    private MediaPlayer createMediaPlayer(String soundFile) {
        try {
            String resourcePath = "/audio/" + soundFile;
            Media sound = new Media(getClass().getResource(resourcePath).toExternalForm());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            System.err.println("Could not load sound file: " + soundFile);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the controller when the die model changes.
     *
     * @param observable The observable object
     * @param prompt Additional information about the update
     */
    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        if (observable == die) {
            startRollingAnimation();
        }
    }

    /**
     * Releases resources used by the controller.
     * Should be called when the controller is no longer needed.
     */
    public void dispose() {
        timer.stop();
        die.removeObserver(this);
    }
}