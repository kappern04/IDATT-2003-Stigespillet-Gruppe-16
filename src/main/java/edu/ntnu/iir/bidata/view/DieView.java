package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class DieView {
    private final Die die;
    private final ImageView imageView;
    private AnimationTimer timer;
    private long animationStartTime;
    private Runnable onAnimationEnd;

    public DieView(Die die) {
        this.die = die;
        this.imageView = new ImageView(getDieImage());
        initializeAnimationTimer();
    }

    public Button createDieButton(Runnable onAnimationEnd) {
        this.onAnimationEnd = onAnimationEnd;
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setOnAction(e -> startRollingAnimation());
        return button;
    }

    private void startRollingAnimation() {
        animationStartTime = System.currentTimeMillis();
        timer.start();
        playSound("dice-roll-sound.wav");
    }
    //Die Animation
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
                    imageView.setImage(getDieImage());

                    if (onAnimationEnd != null) {
                        onAnimationEnd.run();
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

    private void playSound(String soundFile) {
        try {
            Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}