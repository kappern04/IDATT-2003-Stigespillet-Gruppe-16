package edu.ntnu.iir.bidata.clickgame.gui;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.board.SidePanelView;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import edu.ntnu.iir.bidata.laddergame.view.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.clickgame.controller.DummySidePanelController;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Gui extends SidePanelView {
    private static final int PANEL_SPACING = 24;
    private static final int PANEL_PADDING = 28;
    private static final int PLAYER_BOX_SPACING = 18;
    private static final int PLAYER_BOX_PADDING = 18;
    private static final int SIDE_PANELS_SPACING = 40;
    private static final int PLAYER_IMAGE_SIZE = 160;
    private static final int PANEL_CORNER_RADIUS = 22;
    private static final int PLAYER_BOX_CORNER_RADIUS = 16;
    private static final double ANIMATION_DURATION_MS = 300;
    private int clicks = 0;

    private final PlayerController playerController;
    private final CSS css = new CSS();
    private final DummySidePanelController dummySidePanelController;
    private final Map<Player, VBox> playerBoxes = new HashMap<>();
    private final Map<Player, Label> playerClicksLabels = new HashMap<>();
    private Player currentPlayer;

    public Gui(BoardGameController boardGameController, PlayerController playerController) {
        super(boardGameController, playerController);
        this.playerController = Objects.requireNonNull(playerController, "Player controller cannot be null");
        this.dummySidePanelController = new DummySidePanelController(boardGameController, playerController);
    }

    @Override
    public HBox createSidePanels() {
        HBox playersPanel = createPlayersPanel();
        HBox sidePanelsContainer = new HBox(SIDE_PANELS_SPACING, playersPanel);
        sidePanelsContainer.setPadding(new Insets(PANEL_PADDING));
        sidePanelsContainer.setAlignment(Pos.CENTER);
        sidePanelsContainer.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#232946")),
                        new Stop(1, Color.web("#121629"))),
                CornerRadii.EMPTY, Insets.EMPTY
        )));
        return sidePanelsContainer;
    }

    // Only keep this version!
    private HBox createPlayersPanel() {
        HBox playersPanel = new HBox(PLAYER_BOX_SPACING);
        playersPanel.setPadding(new Insets(PANEL_PADDING));
        playersPanel.setAlignment(Pos.CENTER);
        playersPanel.setMinHeight(PLAYER_IMAGE_SIZE + 40);
        playersPanel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#3e4c7a")),
                        new Stop(1, Color.web("#232946"))),
                new CornerRadii(PANEL_CORNER_RADIUS), Insets.EMPTY
        )));
        playersPanel.setEffect(new DropShadow(18, Color.rgb(30, 30, 60, 0.18)));

        playerClicksLabels.clear();
        playerBoxes.clear();
        for (Player player : playerController.getPlayers()) {
            VBox playerBox = createPlayerBox(player);
            playerBoxes.put(player, playerBox);
            playersPanel.getChildren().add(0, playerBox);
        }
        return playersPanel;
    }

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox(8);
        playerBox.setPadding(new Insets(PLAYER_BOX_PADDING));
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setMinWidth(PLAYER_IMAGE_SIZE + 40);
        playerBox.setPrefWidth(PLAYER_IMAGE_SIZE + 40);
        playerBox.setMinHeight(PLAYER_IMAGE_SIZE + 80);
        playerBox.setPrefHeight(PLAYER_IMAGE_SIZE + 80);
        playerBox.setBackground(new Background(new BackgroundFill(
                Color.web("#232946", 0.7),
                new CornerRadii(PLAYER_BOX_CORNER_RADIUS), Insets.EMPTY
        )));
        playerBox.setBorder(new Border(new BorderStroke(
                Color.web("#eebbc3", 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(PLAYER_BOX_CORNER_RADIUS),
                new BorderWidths(2)
        )));
        playerBox.setEffect(new DropShadow(8, Color.rgb(30, 30, 60, 0.10)));

        Image originalImage = dummySidePanelController.getPlayerImage(player);
        ImageView playerImage = PixelArtUpscaler.resizeImage(originalImage, PLAYER_IMAGE_SIZE, PLAYER_IMAGE_SIZE);
        // Remove the circular clip to show the full sprite
        // Circle clip = new Circle(PLAYER_IMAGE_SIZE / 2.0, PLAYER_IMAGE_SIZE / 2.0, PLAYER_IMAGE_SIZE / 2.0);
        // playerImage.setClip(clip);

        Label nameLabel = css.sidePanelLabel(player.getName(), Color.web("#eebbc3"));
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label clicksLabel = css.sidePanelLabel("Clicks: 0", Color.web("#b8c1ec"));
        clicksLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: normal;");
        playerClicksLabels.put(player, clicksLabel);

        playerBox.getChildren().addAll(playerImage, nameLabel, clicksLabel);
        return playerBox;
    }

    public void setCurrentPlayer(Player player) {
        if (player == null) return;
        if (currentPlayer != null) {
            VBox previousPlayerBox = playerBoxes.get(currentPlayer);
            if (previousPlayerBox != null) {
                previousPlayerBox.setBackground(new Background(new BackgroundFill(
                        Color.web("#232946", 0.7),
                        new CornerRadii(PLAYER_BOX_CORNER_RADIUS), Insets.EMPTY
                )));
            }
        }
        currentPlayer = player;
        highlightCurrentPlayer();
    }

    public void highlightCurrentPlayer() {
        if (currentPlayer != null) {
            VBox playerBox = playerBoxes.get(currentPlayer);
            if (playerBox != null) {
                playerBox.setBackground(new Background(new BackgroundFill(
                        Color.web("#eebbc3", 0.35),
                        new CornerRadii(PLAYER_BOX_CORNER_RADIUS), Insets.EMPTY
                )));
                ScaleTransition st = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS * 2), playerBox);
                st.setFromX(1.0);
                st.setFromY(1.0);
                st.setToX(1.04);
                st.setToY(1.04);
                st.setCycleCount(2);
                st.setAutoReverse(true);
                st.play();
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void updatePlayerClicks(Player player) {
        Label clicksLabel = playerClicksLabels.get(player);
        if (clicksLabel != null) {
            clicksLabel.setText("Clicks: " + getClicks());
        }
    }
    public int getClicks() {
        return this.clicks;
    }

    public void incrementClicks() {
        this.clicks++;
    }
}