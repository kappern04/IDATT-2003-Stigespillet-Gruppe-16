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

    private void playSound() {
        if (mediaPlayer != null) {
            // Create a new MediaPlayer for each roll
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = createMediaPlayer("dice-roll-sound.wav");

            // Set the media player to play once
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