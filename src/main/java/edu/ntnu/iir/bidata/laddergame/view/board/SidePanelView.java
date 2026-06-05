package edu.ntnu.iir.bidata.laddergame.view.board;

import edu.ntnu.iir.bidata.laddergame.controller.GameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.util.Colors;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import edu.ntnu.iir.bidata.laddergame.view.util.PixelArtUpscaler;
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

    public SidePanelView(GameController boardGameController, PlayerController playerController) {
        this.playerController = Objects.requireNonNull(playerController);
        this.sidePanelController = new SidePanelController(Objects.requireNonNull(boardGameController), playerController);
        this.dieView = new DieView();
        this.dieController = new DieController(boardGameController.getDie(), dieView);
        this.css = new CSS();

        // Player state changes (position, rank, highlight) refresh the panel live.
        // Re-enabling the die / clearing the in-progress flag happens only when the
        // whole turn has settled (see onTurnAnimationsComplete).
        this.sidePanelController.setOnStateChanged(event -> Platform.runLater(this::refreshUI));
    }

    /**
     * @return the die controller driving the visible die, shared with the game controller
     */
    public DieController getDieController() {
        return dieController;
    }

    public HBox createSidePanels() {
        int playerCount = sidePanelController.getPlayers().size();
        VBox playersPanel = new VBox(playerCount <= 4 ? 10 : 6);
        VBox diePanel = new VBox(20);
        playersPanel.getStyleClass().add("side-panel");
        diePanel.getStyleClass().add("side-panel");

        sidePanelController.getPlayers().forEach(player -> addPlayerToPanel(playersPanel, player, playerCount));

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

    private void addPlayerToPanel(VBox panel, Player player, int playerCount) {
        VBox playerBox = createPlayerBox(player, playerCount);
        panel.getChildren().add(playerBox);
        playerBoxes.put(player, playerBox);
    }

    private VBox createPlayerBox(Player player, int playerCount) {
        VBox box = new VBox();
        box.getStyleClass().add("player-box");

        int avatarSize = playerCount <= 4 ? 80 : 48;
        ImageView avatar = PixelArtUpscaler.resizeImage(sidePanelController.getPlayerImage(player), avatarSize, avatarSize);
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
        return Colors.toColor(player.getColor());
    }

    private void handleDieRoll() {
        if (animationInProgress) return;

        animationInProgress = true;
        setDieButtonEnabled(false);

        sidePanelController.playTurn(dieController, this::onTurnAnimationsComplete);
    }

    /**
     * Invoked once the whole turn (movement, tile effects, resulting slides and any
     * chance popup) has fully settled. Re-enables the die and refreshes the panel.
     */
    private void onTurnAnimationsComplete() {
        Platform.runLater(() -> {
            animationInProgress = false;
            setDieButtonEnabled(true);
            refreshUI();
        });
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
