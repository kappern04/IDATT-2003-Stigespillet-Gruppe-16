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

public class DieController implements Observer {
    private final Die die;
    private final DieView dieView;
    private AnimationTimer timer;
    private long animationStartTime;
    private MediaPlayer mediaPlayer;
    private Runnable onAnimationComplete;

    public DieController(Die die, DieView dieView) {
        this.die = die;
        this.dieView = dieView;
        initializeAnimationTimer();
        this.mediaPlayer = createMediaPlayer("dice-roll-sound.wav");

        // Register as observer to the die model
        this.die.addObserver(this);
    }

    public void setOnAnimationComplete(Runnable callback) {
        this.onAnimationComplete = callback;
    }

    public Die getDie() {
        return die;
    }

    public void handleDieRoll(Runnable afterRollAction) {
        // Logic for when die button is clicked
        if (afterRollAction != null) {
            this.onAnimationComplete = afterRollAction;
        }

        startRollingAnimation();
    }

    private void startRollingAnimation() {
        animationStartTime = System.currentTimeMillis();
        timer.start();
        playSound();
    }

    private void initializeAnimationTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - animationStartTime;
                if (elapsed < 500) { // Roll for 0.5 seconds
                    dieView.setToRandomDieImage();
                } else {
                    timer.stop();
                    dieView.updateDieImage(die.getLastRoll());

                    // Add a pause before executing the callback
                    PauseTransition pause = new PauseTransition(Duration.millis(500));
                    pause.setOnFinished(event -> {
                        if (onAnimationComplete != null) {
                            onAnimationComplete.run();
                        }
                    });
                    pause.play();
                }
            }
        };
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = createMediaPlayer("dice-roll-sound.wav");

            if (mediaPlayer != null) {
                mediaPlayer.setCycleCount(1);
                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.stop();
                });
                mediaPlayer.play();
            }
        }
    }

    private MediaPlayer createMediaPlayer(String soundFile) {
        try {
            String resourcePath = "/audio/" + soundFile;
            Media sound = new Media(getClass().getResource(resourcePath).toExternalForm());
            MediaPlayer player = new MediaPlayer(sound);
            player.setOnError(() -> System.err.println("Media error: " + player.getError()));
            return player;
        } catch (Exception e) {
            System.err.println("Could not load sound file: " + soundFile);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        startRollingAnimation();
        System.out.println("Animation rolling.");
    }
}