package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.animation.DieAnimation;
import edu.ntnu.iir.bidata.laddergame.model.Die;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import edu.ntnu.iir.bidata.laddergame.view.board.DieView;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controls the interaction between the die model and its view, handling
 * die rolling logic and playing associated animations.
 */
public class DieController implements Observer<Die> {

    private final Die die;
    private final DieView dieView;
    private final DieAnimation dieAnimation;

    private final AtomicBoolean isAnimating = new AtomicBoolean(false); // Ensures thread-safe animation state
    private final AtomicReference<Runnable> onAnimationComplete = new AtomicReference<>();

    /**
     * Constructs a DieController.
     *
     * @param die the die model
     * @param dieView the die view
     * @throws NullPointerException if die or dieView is null
     */
    public DieController(Die die, DieView dieView) {
        this.die = Objects.requireNonNull(die, "Die cannot be null");
        this.dieView = Objects.requireNonNull(dieView, "DieView cannot be null");
        this.dieAnimation = new DieAnimation(dieView);
        this.die.addObserver(this);
    }

    /**
     * Sets a callback to run after the die animation completes.
     *
     * @param callback the callback to execute
     */
    public void setOnAnimationComplete(Runnable callback) {
        onAnimationComplete.set(callback);
    }

    /**
     * Returns whether an animation is currently playing.
     *
     * @return true if animating, false otherwise
     */
    public boolean isAnimating() {
        return isAnimating.get();
    }

    /**
     * Rolls the die and optionally triggers an action after animation.
     *
     * @param afterRollAction action to perform after the die animation
     */
    public void handleDieRoll(Runnable afterRollAction) {
        if (isAnimating.get()) return;
        setOnAnimationComplete(afterRollAction);
        die.roll();
    }

    /**
     * Responds to die events and triggers the appropriate animation.
     *
     * @param observable the observable die
     * @param eventType the event type (e.g., "ROLL")
     */
    @Override
    public void update(Observable<Die> observable, String eventType) {
        if (observable == die && "ROLL".equals(eventType) && isAnimating.compareAndSet(false, true)) {
            int roll = die.getLastRoll();
            dieAnimation.playRollAnimation(roll, () -> {
                isAnimating.set(false);
                Runnable callback = onAnimationComplete.getAndSet(null);
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }

    /**
     * Releases resources and removes observer bindings.
     */
    public void dispose() {
        dieAnimation.dispose();
        die.removeObserver(this);
        onAnimationComplete.set(null);
    }
}
