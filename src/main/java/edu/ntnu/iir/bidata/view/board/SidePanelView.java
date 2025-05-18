package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.DieController;
import edu.ntnu.iir.bidata.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.controller.board.PlayerController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class SidePanelView {
    private SidePanelController sidePanelController;
    private PlayerController playerController;
    private DieView dieView;
    private DieController dieController;
    private Button dieButton;
    private CSS css;
    private Map<Player, VBox> playerBoxes;
    private Map<Player, Label> positionLabels;
    private Map<Player, Label> rankLabels;

    public SidePanelView(BoardGameController boardGameController, PlayerController playerController) {
        this.sidePanelController = new SidePanelController(boardGameController, playerController);
        this.dieView = new DieView();
        this.dieController = new DieController(boardGameController.getDie(), dieView);
        this.css = new CSS();
        this.playerBoxes = new HashMap<>();
        this.positionLabels = new HashMap<>();
        this.rankLabels = new HashMap<>();
    }

    public VBox createControlPanel() {
        VBox controlPanel = new VBox(20);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setMinWidth(200);

        List<Player> players = sidePanelController.getPlayers();
        int half = players.size() / 2;
        for (int i = 0; i < half; i++) {
            VBox playerBox = createPlayerBox(players.get(i));
            playerBoxes.put(players.get(i), playerBox);
            controlPanel.getChildren().add(playerBox);
        }

        HBox dieBox = new HBox(10);
        dieButton = dieView.createDieButton(this::handleDieRoll);
        dieBox.getChildren().add(dieButton);
        dieBox.setAlignment(Pos.CENTER);
        controlPanel.getChildren().add(dieBox);

        for (int i = half; i < players.size(); i++) {
            VBox playerBox = createPlayerBox(players.get(i));
            playerBoxes.put(players.get(i), playerBox);
            controlPanel.getChildren().add(playerBox);
        }

        highlightCurrentPlayer();
        updateRankingLabels();
        return controlPanel;
    }

    private void handleDieRoll() {
        setDieButtonEnabled(false);
        sidePanelController.playTurn(dieController, () -> {
            setDieButtonEnabled(true);
            updatePositionLabels();
            updateRankingLabels();
            highlightCurrentPlayer();
        });
    }

    private void highlightCurrentPlayer() {
        playerBoxes.values().forEach(box ->
                box.getStyleClass().setAll("player-box"));

        Player currentPlayer = sidePanelController.getCurrentPlayer();

        if (currentPlayer == null) {
            setDieButtonEnabled(false);
            return;
        }

        VBox playerBox = playerBoxes.get(currentPlayer);
        if (playerBox != null) {
            playerBox.getStyleClass().setAll("player-box-highlighted");
        }
    }

    private void setDieButtonEnabled(boolean enabled) {
        dieButton.setDisable(!enabled);
        dieButton.getStyleClass().setAll(enabled ? "die-button-enabled" : "die-button-disabled");
    }

    private void updatePositionLabels() {
        for (Player player : positionLabels.keySet()) {
            Label label = positionLabels.get(player);
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
        switch(rank) {
            case 1 -> {
                rankLabel.setText("#1");
                rankLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));
                rankLabel.setTextFill(Color.GOLD);
                glow.setColor(Color.GOLD);
                glow.setRadius(15);
                glow.setSpread(0.3);
                rankLabel.setEffect(glow);
            }
            case 2 -> {
                rankLabel.setText("#2");
                rankLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));
                rankLabel.setTextFill(Color.SILVER);
                glow.setColor(Color.SILVER);
                glow.setRadius(15);
                glow.setSpread(0.3);
                rankLabel.setEffect(glow);
            }
            case 3 -> {
                rankLabel.setText("#3");
                rankLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));
                rankLabel.setTextFill(Color.BROWN);
                glow.setColor(Color.BROWN);
                glow.setRadius(15);
                glow.setSpread(0.3);
                rankLabel.setEffect(glow);
            }
            default -> {
                rankLabel.setText("#" + rank);
                rankLabel.setFont(css.getOrbitronFont(16, FontWeight.BOLD));
                rankLabel.setTextFill(Color.WHITE);
            }
        }
    }

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(5));
        playerBox.getStyleClass().add("player-box");

        // Get original player image
        Image originalImage = sidePanelController.getPlayerImage(player);

        // Use PixelArtUpscaler to create a properly scaled version
        int targetSize = 80;
        ImageView playerImage = PixelArtUpscaler.resizeImage(originalImage, targetSize, targetSize);
        playerImage.getStyleClass().add("player-image");

        Label nameLabel = css.sidePanelLabel(player.getName(), 16, getPlayerColor(player));
        Label positionLabel = css.sidePanelLabel("Position: " + player.getPositionIndex(),
                14, getPlayerColor(player));
        Label rankLabel = new Label();

        positionLabels.put(player, positionLabel);
        rankLabels.put(player, rankLabel);

        playerBox.getChildren().addAll(rankLabel, playerImage, nameLabel, positionLabel);
        return playerBox;
    }

    /**
     * @param player The player whose color is to be retrieved.
     * @return The color of the player.
     */
    private Color getPlayerColor(Player player) {
        if (player.getColor() != null) {
            return player.getColor();
        }
        return Color.WHITE;
    }
}