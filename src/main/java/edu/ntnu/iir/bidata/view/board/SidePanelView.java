package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.controller.board.DieController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SidePanelView {
    private SidePanelController sidePanelController;
    private DieView dieView;
    private DieController dieController;
    private Button dieButton;
    private CSS css;
    private Map<Player, VBox> playerBoxes;
    private Map<Player, Label> positionLabels;

    public SidePanelView(BoardGameController boardGameController) {
        this.sidePanelController = new SidePanelController(boardGameController);
        this.dieView = new DieView();
        this.dieController = new DieController(boardGameController.getDie(), dieView);
        this.css = new CSS();
        this.playerBoxes = new HashMap<>();
        this.positionLabels = new HashMap<>();
    }

    public VBox createControlPanel() {
        VBox controlPanel = new VBox(20);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setMinWidth(200);

        Player[] players = sidePanelController.getPlayers();
        for (int i = 0; i < players.length / 2; i++) {
            VBox playerBox = createPlayerBox(players[i]);
            playerBoxes.put(players[i], playerBox);
            controlPanel.getChildren().add(playerBox);
        }

        HBox dieBox = new HBox(10);
        dieButton = dieView.createDieButton(this::handleDieRoll);
        dieBox.getChildren().add(dieButton);
        dieBox.setAlignment(Pos.CENTER);
        controlPanel.getChildren().add(dieBox);

        for (int i = players.length / 2; i < players.length; i++) {
            VBox playerBox = createPlayerBox(players[i]);
            playerBoxes.put(players[i], playerBox);
            controlPanel.getChildren().add(playerBox);
        }

        highlightCurrentPlayer();
        return controlPanel;
    }

    private void handleDieRoll() {
        setDieButtonEnabled(false);
        sidePanelController.playTurn(dieController, () -> {
            setDieButtonEnabled(true);
            updatePositionLabels();
            highlightCurrentPlayer();
        });
    }

    private void highlightCurrentPlayer() {
        playerBoxes.values().forEach(box ->
                box.getStyleClass().setAll("player-box"));

        Player currentPlayer = sidePanelController.getCurrentPlayer();
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

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(5));
        playerBox.getStyleClass().add("player-box");

        Image originalImage = sidePanelController.getPlayerImage(player);
        int scaleFactor = Math.max(1, (int)(80 / Math.max(originalImage.getWidth(), originalImage.getHeight())));
        Image upscaledImage = originalImage;

        if (scaleFactor > 1) {
            BufferedImage buffered = SwingFXUtils.fromFXImage(originalImage, null);
            BufferedImage upscaled = PixelArtUpscaler.upscale(buffered, scaleFactor);
            upscaledImage = SwingFXUtils.toFXImage(upscaled, null);
        }

        ImageView playerImage = new ImageView(upscaledImage);
        playerImage.getStyleClass().add("player-image");

        Label nameLabel = css.createStyledLabel(player.getName(), FontWeight.BOLD, 16, getPlayerColor(player));
        Label positionLabel = css.createStyledLabel("Position: " + player.getPositionIndex(),
                FontWeight.NORMAL, 14, Color.WHITE);

        positionLabels.put(player, positionLabel);
        playerBox.getChildren().addAll(playerImage, nameLabel, positionLabel);
        return playerBox;
    }

    private Color getPlayerColor(Player player) {
        if (player.getColor() != null) {
            return player.getColor();
        }
        return Color.WHITE;
    }
}