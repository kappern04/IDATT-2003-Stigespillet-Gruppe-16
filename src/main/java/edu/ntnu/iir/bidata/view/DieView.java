package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import edu.ntnu.iir.bidata.object.Observable;
import javafx.animation.PauseTransition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Duration;


public class DieView implements Observer {
    private final Die die;
    private final ImageView imageView;
    private AnimationTimer timer;
    private long animationStartTime;
    private MediaPlayer mediaPlayer;
    private Runnable onAnimationComplete; // New callback for animation completion

    public DieView(Die die) {
        this.die = die;
        initializeAnimationTimer();
        this.imageView = new ImageView(getDieImage());
        this.mediaPlayer = createMediaPlayer("dice-roll-sound.wav");
    }

    public void setOnAnimationComplete(Runnable callback) {
        this.onAnimationComplete = callback;
    }

    public Button createDieButton(Runnable runnable) {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setOnAction(event -> {runnable.run();});
        return button;
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
                    setToRandomDieImage();
                } else {
                    timer.stop();
                    setToLastRollImage();

                    // Add a pause before executing the callback
                    PauseTransition pause = new PauseTransition(Duration.millis(500)); // delay in ms
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

    private void setToRandomDieImage() {
        int randomRoll = (int) (Math.random() * 6) + 1;
        imageView.setImage(new Image(getClass().getResourceAsStream("/image/die_" + randomRoll + ".png")));
    }

    private void setToLastRollImage() {
        imageView.setImage(getDieImage());
    }

    private Image getDieImage() {
        return new Image(getClass().getResourceAsStream("/image/die_" + die.getLastRoll() + ".png"));
    }

    private MediaPlayer createMediaPlayer(String soundFile) {
        try {
            Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
            return new MediaPlayer(sound);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.play();
        }
    }

    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        startRollingAnimation();
        System.out.println("Animation rolling.");
    }
}