package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

public class DieView {
    private final Die die;
    private final ImageView imageView;
    private AnimationTimer timer;
    private long animationStartTime;
    private Runnable onAnimationEnd;
    private MediaPlayer mediaPlayer;

    public DieView(Die die) {
        this.die = die;
        initializeAnimationTimer();
        this.imageView = new ImageView(getDieImage());
        this.mediaPlayer = createMediaPlayer("dice-roll-sound.wav");
    }

    public Button createDieButton(Runnable onAnimationEnd) {
        this.onAnimationEnd = onAnimationEnd;
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setOnAction(e -> {
            button.setDisable(true);
            startRollingAnimation();
            button.setDisable(false);
        });
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
                if (elapsed < 1000) { // Roll for 1 second
                    updateDie();
                } else {
                    timer.stop();
                    die.roll();
                    if (onAnimationEnd != null) {
                        onAnimationEnd.run();
                        imageView.setImage(getDieImage());
                    }
                }
            }
        };
    }

    private void updateDie() {
        int randomRoll = (int) (Math.random() * 6) + 1;
        imageView.setImage(new Image(getClass().getResourceAsStream("/image/die_" + randomRoll + ".png")));
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
}