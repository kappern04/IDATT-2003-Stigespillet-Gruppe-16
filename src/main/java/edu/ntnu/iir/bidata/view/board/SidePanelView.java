package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;

public class SidePanelView {
    private SidePanelController sidePanelController;
    private DieView dieView;
    private Button dieButton;
    private CSS css;
    private Map<Player, VBox> playerBoxes;
    private Map<Player, Label> positionLabels;

    public SidePanelView(BoardGameController boardGameController) {
        // Create controller with responsibility for this view
        this.sidePanelController = new SidePanelController(boardGameController);

        // Initialize views
        this.dieView = new DieView(boardGameController.getDie());
        this.css = new CSS();
        this.playerBoxes = new HashMap<>();
        this.positionLabels = new HashMap<>();

        // Register observers
        boardGameController.getDie().addObserver(this.dieView);
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

        // Initially highlight current player
        highlightCurrentPlayer();

        return controlPanel;
    }

    private void handleDieRoll() {
        // Disable button during roll and animation
        setDieButtonEnabled(false);

        // Use controller to handle game logic
        sidePanelController.playTurn(dieView, () -> {
            // Re-enable button when animation completes
            setDieButtonEnabled(true);

            // Update position labels
            updatePositionLabels();

            // Update player highlighting to show new current player
            highlightCurrentPlayer();
        });
    }

    private void setDieButtonEnabled(boolean enabled) {
        dieButton.setDisable(!enabled);
        if (enabled) {
            dieButton.setStyle("-fx-opacity: 1.0;");
        } else {
            dieButton.setStyle("-fx-opacity: 1.0;");
        }
    }

    private void highlightCurrentPlayer() {
        // Reset all player boxes to normal style
        playerBoxes.values().forEach(box ->
                box.setStyle("-fx-border-width: 0; -fx-background-color: transparent; -fx-padding: 5;"));

        Player currentPlayer = sidePanelController.getCurrentPlayer();
        VBox playerBox = playerBoxes.get(currentPlayer);
        if (playerBox != null) {
            // Apply highlighting style to current player
            playerBox.setStyle("-fx-border-color: yellow; -fx-border-width: 2; " +
                    "-fx-border-radius: 5; -fx-background-color: rgba(255, 255, 0, 0.2); " +
                    "-fx-padding: 3;");
        }
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

        ImageView playerImage = new ImageView(
                sidePanelController.getPlayerImage(player)
        );
        playerImage.setFitWidth(96);
        playerImage.setFitHeight(96);


        Label nameLabel = css.createStyledLabel(player.getName(), FontWeight.BOLD, 14, getPlayerColor(player));

        Label positionLabel = css.createStyledLabel("Position: " + player.getPositionIndex(),
                FontWeight.NORMAL, 12, Color.WHITE);

        // Store position label for updates
        positionLabels.put(player, positionLabel);

        playerBox.getChildren().addAll(playerImage, nameLabel, positionLabel);
        return playerBox;
    }

    private Color getPlayerColor(Player player) {
        Player[] players = sidePanelController.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                switch (i) {
                    case 0: return css.PLAYER_1_COLOR;
                    case 1: return css.PLAYER_2_COLOR;
                    case 2: return css.PLAYER_3_COLOR;
                    case 3: return css.PLAYER_4_COLOR;
                    default: return Color.WHITE;
                }
            }
        }
        return Color.WHITE;
    }
}