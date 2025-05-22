package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PixelArtUpscaler;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * View for the side panel, responsible for rendering and updating UI.
 */
public class SidePanelView {
    private final PlayerController playerController;
    private final SidePanelController sidePanelController;
    private final DieController dieController;
    private final DieView dieView;
    private final CSS css;
    private Button dieButton;

    private final Map<Player, VBox> playerBoxes = new HashMap<>();
    private final Map<Player, Label> positionLabels = new HashMap<>();
    private final Map<Player, Label> rankLabels = new HashMap<>();
    private boolean animationInProgress = false;

    public SidePanelView(BoardGameController boardGameController, PlayerController playerController) {
        Objects.requireNonNull(boardGameController);
        this.playerController = Objects.requireNonNull(playerController);

        this.sidePanelController = new SidePanelController(boardGameController, playerController);
        this.dieView = new DieView();
        this.dieController = new DieController(boardGameController.getDie(), dieView);
        this.css = new CSS();

        // Register for state changes from the controller
        this.sidePanelController.setOnStateChanged(eventType -> Platform.runLater(this::refreshUI));
    }

    public HBox createSidePanels() {
        VBox playersPanel = new VBox(10);
        VBox diePanel = new VBox(10);

        playersPanel.getStyleClass().add("side-panel");
        diePanel.getStyleClass().add("side-panel");

        List<Player> players = sidePanelController.getPlayers();
        for (Player player : players) {
            addPlayerToPanel(playersPanel, player);
        }

        HBox dieBox = createDieControl();

        diePanel.setAlignment(Pos.TOP_CENTER);
        diePanel.getChildren().addAll(dieBox);
        diePanel.setPrefWidth(200);
        diePanel.setSpacing(20);

        updatePositionLabels();
        updateRankingLabels();
        highlightCurrentPlayer();

        if (isBusy()) {
            setDieButtonEnabled(false);
            animationInProgress = true;
        }

        HBox sidePanelsContainer = new HBox(20);
        sidePanelsContainer.getChildren().addAll(playersPanel, diePanel);
        return sidePanelsContainer;
    }

    private HBox createDieControl() {
        HBox dieBox = new HBox();
        dieBox.getStyleClass().add("die-box");
        dieBox.setAlignment(Pos.CENTER);
        dieButton = dieView.createDieButton(this::handleDieRoll);
        dieButton.getStyleClass().add("die-button-enabled");
        dieBox.getChildren().add(dieButton);
        return dieBox;
    }

    private void addPlayerToPanel(VBox controlPanel, Player player) {
        VBox playerBox = createPlayerBox(player);
        playerBoxes.put(player, playerBox);
        controlPanel.getChildren().add(playerBox);
    }

    private void checkAnimationStatusAndUpdateUI() {
        if (isBusy()) {
            schedulePoll(100);
            return;
        }
        javafx.animation.PauseTransition cooldown =
                new javafx.animation.PauseTransition(javafx.util.Duration.millis(800));

        cooldown.setOnFinished(e -> {
            if (!isBusy()) {
                animationInProgress = false;
                Platform.runLater(this::refreshUI);
            } else {
                checkAnimationStatusAndUpdateUI();
            }
        });

        cooldown.play();
    }

    private void schedulePoll(int delayMs) {
        Platform.runLater(() -> {
            javafx.animation.PauseTransition wait =
                    new javafx.animation.PauseTransition(javafx.util.Duration.millis(delayMs));
            wait.setOnFinished(e -> checkAnimationStatusAndUpdateUI());
            wait.play();
        });
    }

    private void handleDieRoll() {
        if (animationInProgress) return;
        setDieButtonEnabled(false);
        animationInProgress = true;
        sidePanelController.playTurn(dieController, this::onTurnAnimationsComplete);
    }

    private void onTurnAnimationsComplete() {
        // Instead of immediately enabling, poll for animation status
        checkAnimationStatusAndUpdateUI();
    }

    private void highlightCurrentPlayer() {
        playerBoxes.values().forEach(box -> box.getStyleClass().setAll("player-box"));
        Player currentPlayer = sidePanelController.getCurrentPlayer();
        if (currentPlayer == null) {
            setDieButtonEnabled(false);
            return;
        }
        VBox playerBox = playerBoxes.get(currentPlayer);
        if (playerBox != null) {
            playerBox.getStyleClass().setAll("player-box-highlighted");
        }
        setDieButtonEnabled(!animationInProgress);
    }

    private void setDieButtonEnabled(boolean enabled) {
        if (dieButton != null) {
            dieButton.setDisable(!enabled);
            dieButton.getStyleClass().setAll(enabled ? "die-button-enabled" : "die-button-disabled");
        }
    }

    private void updatePositionLabels() {
        for (Map.Entry<Player, Label> entry : positionLabels.entrySet()) {
            Player player = entry.getKey();
            Label label = entry.getValue();
            label.setText("Position: " + player.getPositionIndex());
        }
    }

    private void updateRankingLabels() {
        List<Player> playerRanks = sidePanelController.getPlayerRanks();
        for (Player player : playerRanks) {
            Label rankLabel = rankLabels.get(player);
            if (rankLabel != null) {
                int rank = playerRanks.indexOf(player) + 1;
                fillInRankLabel(rankLabel, rank);
            }
        }
    }

    private void fillInRankLabel(Label rankLabel, int rank) {
        DropShadow glow = new DropShadow();
        rankLabel.setText("#" + rank);
        rankLabel.getStyleClass().setAll("rank-label");
        switch(rank) {
            case 1 -> {
                rankLabel.getStyleClass().add("rank-label-gold");
                configureGlow(glow, Color.GOLD);
            }
            case 2 -> {
                rankLabel.getStyleClass().add("rank-label-silver");
                configureGlow(glow, Color.SILVER);
            }
            case 3 -> {
                rankLabel.getStyleClass().add("rank-label-bronze");
                configureGlow(glow, Color.BROWN);
            }
            default -> {
                rankLabel.getStyleClass().add("rank-label-default");
                rankLabel.setEffect(null);
                return;
            }
        }
        rankLabel.setEffect(glow);
    }

    private void configureGlow(DropShadow glow, Color color) {
        glow.setColor(color);
        glow.setRadius(15);
        glow.setSpread(0.3);
    }

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox();
        playerBox.getStyleClass().add("player-box");

        Image originalImage = sidePanelController.getPlayerImage(player);
        ImageView playerImage = PixelArtUpscaler.resizeImage(originalImage, 80, 80);

        Color playerColor = getPlayerColor(player);
        Label nameLabel = css.sidePanelLabel(player.getName(), playerColor);
        Label positionLabel = css.sidePanelLabel("Position: " + player.getPositionIndex(), playerColor);
        Label rankLabel = new Label();

        positionLabels.put(player, positionLabel);
        rankLabels.put(player, rankLabel);

        playerBox.getChildren().addAll(rankLabel, playerImage, nameLabel, positionLabel);
        return playerBox;
    }

    private Color getPlayerColor(Player player) {
        return player.getColor() != null ? player.getColor() : Color.WHITE;
    }

    /**
     * Returns true if either the player or die animation is in progress.
     */
    private boolean isBusy() {
        return playerController.hasActiveAnimations() || dieController.isAnimating();
    }

    public void refreshUI() {
        updatePositionLabels();
        updateRankingLabels();
        highlightCurrentPlayer();
    }

    public void updateUI() {
        if (!animationInProgress) {
            refreshUI();
        }
    }
}