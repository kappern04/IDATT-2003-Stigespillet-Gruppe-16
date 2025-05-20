package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Die;
import edu.ntnu.iir.bidata.util.Observable;
import edu.ntnu.iir.bidata.util.Observer;
import edu.ntnu.iir.bidata.view.board.DieView;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the die component of the game.
 * Handles die rolling animation, sound effects, and observer updates from the die model.
 */
public class DieController implements Observer {
    private static final Logger LOGGER = Logger.getLogger(DieController.class.getName());

    // Animation constants
    private static final long ROLL_ANIMATION_DURATION_MS = 500;
    private static final long PAUSE_DURATION_MS = 500;

    // Sound constants
    private static final String SOUND_FILE_PATH = "dice-roll-sound.wav";
    private static final String SOUND_DIRECTORY = "/audio/";
    private static final double DEFAULT_VOLUME = 0.8;

    private final Die die;
    private final DieView dieView;
    private final AnimationTimer timer;

    private long animationStartTime;
    private Runnable onAnimationComplete;
    private final AtomicBoolean isAnimating = new AtomicBoolean(false);
    private MediaPlayer currentPlayer;

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

        LOGGER.fine("DieController initialized with die model and view");
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
     * Checks if animation is currently running.
     *
     * @return true if animation is in progress
     */
    public boolean isAnimating() {
        return isAnimating.get();
    }

    /**
     * Handles a die roll action with an optional callback.
     *
     * @param afterRollAction Action to perform after the roll completes
     */
    public void handleDieRoll(Runnable afterRollAction) {
        if (isAnimating.get()) {
            LOGGER.fine("Die roll requested while animation in progress - ignoring");
            return;
        }

        if (afterRollAction != null) {
            this.onAnimationComplete = afterRollAction;
        }

        // Roll the die model
        die.roll();

        startRollingAnimation();
    }

    /**
     * Starts the die rolling animation and sound.
     */
    private void startRollingAnimation() {
        LOGGER.fine("Starting die roll animation");
        isAnimating.set(true);

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
                    int rollResult = die.getLastRoll();
                    LOGGER.fine("Die roll animation ended with result: " + rollResult);

                    // Update UI on the JavaFX thread
                    Platform.runLater(() -> dieView.updateDieImage(rollResult));

                    // Schedule completion callback
                    scheduleCompletionCallback();
                }
            }
        };
    }

    /**
     * Schedules the completion callback after a short pause.
     */
    private void scheduleCompletionCallback() {
        PauseTransition pause = new PauseTransition(Duration.millis(PAUSE_DURATION_MS));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                try {
                    if (onAnimationComplete != null) {
                        Runnable callback = onAnimationComplete;
                        onAnimationComplete = null; // Clear before executing to avoid re-entrancy issues
                        callback.run();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error in animation completion callback", e);
                } finally {
                    // Always mark animation as complete
                    isAnimating.set(false);
                }
            });
        });
        pause.play();
    }

    /**
     * Plays the die roll sound effect.
     */
    private void playSound() {
        // Clean up previous sound player if exists
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
            player.setOnError(() -> {
                LOGGER.log(Level.WARNING, "Media error: " + player.getError());
                player.dispose();
                currentPlayer = null;
            });
            player.play();
            LOGGER.fine("Die roll sound started");
        }
    }

    /**
     * Creates a media player for the given sound file.
     *
     * @param soundFile The name of the sound file in the resources folder
     * @return A configured MediaPlayer or null if the sound couldn't be loaded
     */
    private MediaPlayer createMediaPlayer(String soundFile) {
        String resourcePath = SOUND_DIRECTORY + soundFile;

        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                LOGGER.warning("Sound file not found: " + resourcePath);
                return null;
            }

            Media sound = new Media(resourceUrl.toExternalForm());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load sound file: " + resourcePath, e);
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
        if (observable == die && !isAnimating.get()) {
            LOGGER.fine("Die model updated: " + prompt);
            startRollingAnimation();
        }
    }

    /**
     * Releases resources used by the controller.
     * Should be called when the controller is no longer needed.
     */
    public void dispose() {
        LOGGER.fine("Disposing DieController resources");
        timer.stop();
        die.removeObserver(this);

        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }

        onAnimationComplete = null;
    }
}