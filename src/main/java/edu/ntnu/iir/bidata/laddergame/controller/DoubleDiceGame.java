//package edu.ntnu.iir.bidata.laddergame.controller;
//
//import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
//import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
//import edu.ntnu.iir.bidata.laddergame.model.Board;
//import edu.ntnu.iir.bidata.laddergame.model.Die;
//import edu.ntnu.iir.bidata.laddergame.model.Player;
//
//import java.util.logging.Logger;
//
///**
// * Controller for a board game with double dice functionality.
// * Players roll two dice and move the sum of both values.
// */
//public class DoubleDiceGame extends BoardGameController {
//
//    private static final Logger LOGGER = Logger.getLogger(DoubleDiceGame.class.getName());
//    private boolean doubleDiceEnabled = true;
//
//    /**
//     * Creates a new double dice game with the specified board and controllers.
//     *
//     * @param board the game board
//     * @param playerController the player controller
//     * @param dieController the die controller
//     */
//    public DoubleDiceGame(Board board, PlayerController playerController, DieController dieController) {
//        super(board, playerController, dieController);
//        LOGGER.info("Double dice game initialized");
//    }
//
//    /**
//     * Plays a turn for the current player using two dice.
//     *
//     * @param dieController the die controller
//     * @param onTurnComplete callback after turn is complete
//     */
//    @Override
//    public void playTurn(DieController dieController, Runnable onTurnComplete) {
//        if (doubleDiceEnabled) {
//            super.playTurn(dieController, onTurnComplete);
//            return;
//        }
//
//        if (isBusy()) {
//            LOGGER.warning("Turn already in progress, ignoring playTurn request");
//            return;
//        }
//
//        if (isGameOver()) {
//            LOGGER.info("Game is already over, ignoring turn request");
//            if (onTurnComplete != null) onTurnComplete.run();
//            return;
//        }
//
//        skipFinishedPlayers();
//
//        Player currentPlayer = getCurrentPlayer();
//        if (currentPlayer == null) {
//            LOGGER.warning("No current player available for turn");
//            if (onTurnComplete != null) onTurnComplete.run();
//            return;
//        }
//
//        LOGGER.info("Starting double dice turn for player: " + currentPlayer.getName());
//
//        dieController.setOnAnimationComplete(() -> handleDoubleDiceRoll(currentPlayer, onTurnComplete));
//        getDie().rollDouble();
//    }
//
//    /**
//     * Handles player movement after rolling two dice.
//     *
//     * @param currentPlayer the player whose turn it is
//     * @param onTurnComplete callback to run after turn is complete
//     */
//    private void handleDoubleDiceRoll(Player currentPlayer, Runnable onTurnComplete) {
//        Die die = getDie();
//        int roll1 = die.getLastRoll();
//        int roll2 = die.getSecondDieRoll();
//        int totalRoll = roll1 + roll2;
//
//        int boardSize = getBoard().getTiles().size();
//        int currentPosition = currentPlayer.getPositionIndex();
//        int targetPosition = currentPosition + totalRoll;
//
//        if (targetPosition >= boardSize - 1) {
//            int moveDistance = boardSize - 1 - currentPosition;
//            currentPlayer.move(moveDistance);
//
//            if (!getPlayerRanks().contains(currentPlayer)) {
//                getPlayerRanks().add(currentPlayer);
//                LOGGER.info(currentPlayer.getName() + " finished in position " + getPlayerRanks().size());
//            }
//        } else {
//            currentPlayer.move(totalRoll);
//        }
//
//        waitForAnimationsToComplete(() -> {
//            applyTileEffects(currentPlayer);
//            advanceToNextPlayer();
//
//            LOGGER.info(currentPlayer.getName() + " rolled " + roll1 + " and " + roll2 +
//                    " (total: " + totalRoll + ") and is now at position " + currentPlayer.getPositionIndex());
//
//            if (onTurnComplete != null) {
//                onTurnComplete.run();
//            }
//        });
//    }
//
//    /**
//     * Waits for animations to complete before executing the callback.
//     */
//    private void waitForAnimationsToComplete(Runnable onComplete) {
//        if (!isBusy()) {
//            onComplete.run();
//        } else {
//            javafx.animation.PauseTransition wait =
//                    new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
//            wait.setOnFinished(e -> waitForAnimationsToComplete(onComplete));
//            wait.play();
//        }
//    }
//
//    /**
//     * Enable or disable double dice mode.
//     *
//     * @param enabled true to enable double dice, false to disable
//     */
//    public void setDoubleDiceEnabled(boolean enabled) {
//        this.doubleDiceEnabled = enabled;
//        notifyObservers("doubledicemodechanged_" + enabled);
//        LOGGER.info("Double dice mode " + (enabled ? "enabled" : "disabled"));
//    }
//
//    /**
//     * Gets whether double dice mode is enabled.
//     *
//     * @return true if double dice is enabled, false otherwise
//     */
//    public boolean isDoubleDiceEnabled() {
//        return doubleDiceEnabled;
//    }
//}