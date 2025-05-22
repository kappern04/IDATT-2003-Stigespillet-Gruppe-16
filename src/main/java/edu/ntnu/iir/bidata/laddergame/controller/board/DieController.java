package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.animation.DieAnimation;
import edu.ntnu.iir.bidata.laddergame.model.Die;
import edu.ntnu.iir.bidata.laddergame.util.Observable;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import edu.ntnu.iir.bidata.laddergame.view.board.DieView;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DieController implements Observer<Die> {
    private final Die die;
    private final DieView dieView;
    private final DieAnimation dieAnimation;
    private final AtomicBoolean isAnimating = new AtomicBoolean(false);
    private Runnable onAnimationComplete;

    public DieController(Die die, DieView dieView) {
        this.die = Objects.requireNonNull(die, "Die cannot be null");
        this.dieView = Objects.requireNonNull(dieView, "DieView cannot be null");
        this.dieAnimation = new DieAnimation(dieView);
        this.die.addObserver(this);
    }

    public void setOnAnimationComplete(Runnable callback) {
        this.onAnimationComplete = callback;
    }

    public boolean isAnimating() {
        return isAnimating.get();
    }

    public void handleDieRoll(Runnable afterRollAction) {
        if (isAnimating.get()) return;
        if (afterRollAction != null) this.onAnimationComplete = afterRollAction;
        die.roll();
    }

    @Override
    public void update(Observable<Die> observable, String eventType) {
        if (observable == die && "ROLL".equals(eventType) && !isAnimating.get()) {
            isAnimating.set(true);
            dieAnimation.playRollAnimation(die.getLastRoll(), () -> {
                isAnimating.set(false);
                if (onAnimationComplete != null) {
                    Runnable callback = onAnimationComplete;
                    onAnimationComplete = null;
                    callback.run();
                }
            });
        }
    }

    public void dispose() {
        dieAnimation.dispose();
        die.removeObserver(this);
        onAnimationComplete = null;
    }
}