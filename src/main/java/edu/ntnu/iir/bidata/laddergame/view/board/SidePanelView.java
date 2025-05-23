package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PixelArtUpscaler;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.util.*;

/**
 * UI view component for the side panel, including player stats and the die control.
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
        this.playerController = Objects.requireNonNull(playerController);
        this.sidePanelController = new SidePanelController(Objects.requireNonNull(boardGameController), playerController);
        this.dieView = new DieView();
        this.dieController = new DieController(boardGameController.getDie(), dieView);
        this.css = new CSS();

        this.sidePanelController.setOnStateChanged(event -> Platform.runLater(this::refreshUI));
        sidePanelController.getPlayers().forEach(player ->
                player.addObserver((observable, arg) -> {
                    if ("MOVEMENT_COMPLETE".equals(arg)) {
                        Platform.runLater(() -> {
                            animationInProgress = false;
                            refreshUI();
                            setDieButtonEnabled(true);
                        });
                    }
                })
        );
    }

    public HBox createSidePanels() {
        VBox playersPanel = new VBox(10);
        VBox diePanel = new VBox(20);
        playersPanel.getStyleClass().add("side-panel");
        diePanel.getStyleClass().add("side-panel");

        sidePanelController.getPlayers().forEach(player -> addPlayerToPanel(playersPanel, player));

        diePanel.setAlignment(Pos.TOP_CENTER);
        diePanel.setPrefWidth(200);
        diePanel.getChildren().add(createDieControl());

        updateAllLabels();
        highlightCurrentPlayer();

        if (isBusy()) {
            animationInProgress = true;
            setDieButtonEnabled(false);
        }

        HBox container = new HBox(20, playersPanel, diePanel);
        return container;
    }

    private HBox createDieControl() {
        HBox dieBox = new HBox();
        dieBox.getStyleClass().add("die-box");
        dieBox.setAlignment(Pos.CENTER);

        dieButton = dieView.createDieButton(this::handleDieRoll);
        dieButton.getStyleClass().add("die-button-enabled");
        dieButton.setDisable(animationInProgress);

        dieBox.getChildren().add(dieButton);
        return dieBox;
    }

    private void addPlayerToPanel(VBox panel, Player player) {
        VBox playerBox = createPlayerBox(player);
        panel.getChildren().add(playerBox);
        playerBoxes.put(player, playerBox);
    }

    private VBox createPlayerBox(Player player) {
        VBox box = new VBox();
        box.getStyleClass().add("player-box");

        ImageView avatar = PixelArtUpscaler.resizeImage(sidePanelController.getPlayerImage(player), 80, 80);
        Color color = getPlayerColor(player);

        Label nameLabel = css.sidePanelLabel(player.getName(), color);
        Label posLabel = css.sidePanelLabel("Position: " + player.getPositionIndex(), color);
        Label rankLabel = new Label();

        positionLabels.put(player, posLabel);
        rankLabels.put(player, rankLabel);

        box.getChildren().addAll(rankLabel, avatar, nameLabel, posLabel);
        return box;
    }

    private Color getPlayerColor(Player player) {
        return Optional.ofNullable(player.getColor()).orElse(Color.WHITE);
    }

    private void handleDieRoll() {
        if (animationInProgress) return;

        animationInProgress = true;
        setDieButtonEnabled(false);

        sidePanelController.playTurn(dieController, this::onTurnAnimationsComplete);
    }

    private void onTurnAnimationsComplete() {
        pollUntilIdle();
    }

    private void pollUntilIdle() {
        if (!isBusy()) {
            animationInProgress = false;
            Platform.runLater(this::refreshUI);
            return;
        }

        PauseTransition wait = new PauseTransition(Duration.millis(20));
        wait.setOnFinished(e -> pollUntilIdle());
        wait.play();
    }

    private void updateAllLabels() {
        updatePositionLabels();
        updateRankingLabels();
    }

    private void updatePositionLabels() {
        positionLabels.forEach((player, label) ->
                label.setText("Position: " + player.getPositionIndex())
        );
    }

    private void updateRankingLabels() {
        List<Player> rankings = sidePanelController.getPlayerRanks();
        for (int i = 0; i < rankings.size(); i++) {
            Player player = rankings.get(i);
            Label label = rankLabels.get(player);
            if (label != null) fillInRankLabel(label, i + 1);
        }
    }

    private void fillInRankLabel(Label label, int rank) {
        label.setText("#" + rank);
        label.getStyleClass().setAll("rank-label");
        DropShadow glow = new DropShadow();

        switch (rank) {
            case 1 -> applyRankStyle(label, glow, "rank-label-gold", Color.GOLD);
            case 2 -> applyRankStyle(label, glow, "rank-label-silver", Color.SILVER);
            case 3 -> applyRankStyle(label, glow, "rank-label-bronze", Color.BROWN);
            default -> {
                label.getStyleClass().add("rank-label-default");
                label.setEffect(null);
            }
        }
    }

    private void applyRankStyle(Label label, DropShadow glow, String cssClass, Color color) {
        label.getStyleClass().add(cssClass);
        glow.setColor(color);
        glow.setRadius(15);
        glow.setSpread(0.3);
        label.setEffect(glow);
    }

    private void highlightCurrentPlayer() {
        playerBoxes.values().forEach(box -> box.getStyleClass().setAll("player-box"));

        Player current = sidePanelController.getCurrentPlayer();
        if (current == null) {
            setDieButtonEnabled(false);
            return;
        }

        Optional.ofNullable(playerBoxes.get(current))
                .ifPresent(box -> box.getStyleClass().setAll("player-box-highlighted"));
        
    }

    private void setDieButtonEnabled(boolean enabled) {
        if (dieButton != null) {
            dieButton.setDisable(!enabled);
            dieButton.getStyleClass().setAll(enabled ? "die-button-enabled" : "die-button-disabled");
        }
    }

    private boolean isBusy() {
        return playerController.hasActiveAnimations() || dieController.isAnimating();
    }

    public void refreshUI() {
        updateAllLabels();
        highlightCurrentPlayer();
    }

    public void updateUI() {
        if (!animationInProgress) refreshUI();
    }
}
