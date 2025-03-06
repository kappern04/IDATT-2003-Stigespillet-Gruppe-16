package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import java.applet.AudioClip;
import java.io.File;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javax.print.attribute.standard.Media;

public class DieView {
    private final Die die;
    private final ImageView imageView;
    private AnimationTimer timer;
    private long animationStartTime;
    //private AudioClip dieAudioClip;

    public DieView(Die die) {
        this.die = die;
        this.imageView = new ImageView(getDieImage());
        initializeAnimationTimer();
        //dieAudioClip = new AudioClip();
    }

    public Button createDieButton() {
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setOnAction(e -> startRollingAnimation());
        return button;
    }

    private void startRollingAnimation() {
        animationStartTime = System.currentTimeMillis();
        timer.start();
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

    //private void AudioClip() {
      //  AudioClip dieAudioClip = new AudioClip(new File("src/main/resources/audio/die_roll.wav").toURI().toString());
        //dieAudioClip.play();
    //}
}