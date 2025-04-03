package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Die;
import edu.ntnu.iir.bidata.object.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;


public class DieView implements Observer {
    private final Die die;
    private final ImageView imageView;
    private AnimationTimer timer;
    private long animationStartTime;
    private Runnable runnable;

    public DieView(Die die) {
        this.die = die;
        initializeAnimationTimer();
        this.imageView = new ImageView(getDieImage());
    }

    public Button createDieButton(Runnable runnable) {
        this.runnable = runnable;
        Button button = new Button();
        button.setGraphic(imageView);
        button.setBackground(Background.EMPTY);
        button.setOnAction(event -> {runnable.run();});
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
                if (elapsed < 500) { // Roll for 1 second
                    setToRandomDieImage();
                } else {
                    timer.stop();
                    setToLastRollImage();
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

    private void playSound(String soundFile) {
        try {
            Media sound = new Media(getClass().getResource("/audio/" + soundFile).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Observer> void update(Observable<T> observable, String prompt) {
        startRollingAnimation();
        System.out.println("Animation rolling.");
    }
}