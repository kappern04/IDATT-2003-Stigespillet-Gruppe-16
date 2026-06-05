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
     * Responds to die events and triggers the appropriate animation.
     *
     * @param observable the observable die
     * @param eventType the event type (e.g., "ROLL")
     */
    @Override
    public void update(Observable<Die> observable, String eventType) {
        if (observable != die) {
            return;
        }

        if ("ROLL".equals(eventType) && isAnimating.compareAndSet(false, true)) {
            dieAnimation.playRollAnimation(die.getLastRoll(), this::completeAnimation);
        } else if ("DOUBLE_ROLL".equals(eventType) && isAnimating.compareAndSet(false, true)) {
            // Reuse the single die view: roll it once to show the first die, then
            // again to show the second. The player moves by the combined total.
            int first = die.getLastRoll();
            int second = die.getSecondDieRoll();
            dieAnimation.playRollAnimation(first,
                    () -> dieAnimation.playRollAnimation(second, this::completeAnimation));
        }
    }

    /**
     * Clears the animating flag and runs the pending completion callback, if any.
     */
    private void completeAnimation() {
        isAnimating.set(false);
        Runnable callback = onAnimationComplete.getAndSet(null);
        if (callback != null) {
            callback.run();
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
