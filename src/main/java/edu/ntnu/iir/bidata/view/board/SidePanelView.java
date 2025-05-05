package edu.ntnu.iir.bidata.view.board;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.util.CSS;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;

/**
 * SidePanelView contains an overview of players, their positions and their rankings, and the die
 * button.
 */
public class SidePanelView {

  private SidePanelController sidePanelController;
  private DieView dieView;
  private Button dieButton;
  private CSS css;
  private Map<Player, VBox> playerBoxes;
  private Map<Player, Label> positionLabels;
  private Map<Player, Label> rankLabels;

  /**
   * Constructor for SidePanelView.
   *
   * @param boardGameController The controller for the board game.
   */
  public SidePanelView(BoardGameController boardGameController) {
    // Create controller with responsibility for this view
    this.sidePanelController = new SidePanelController(boardGameController);

    // Initialize views
    this.dieView = new DieView(boardGameController.getDie());
    this.css = new CSS();
    this.playerBoxes = new HashMap<>();
    this.positionLabels = new HashMap<>();
    this.rankLabels = new HashMap<>();

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
    setDieButtonEnabled(false);

    // Use controller to handle game logic
    sidePanelController.playTurn(dieView, () -> {
      setDieButtonEnabled(true);

      updatePositionLabels();

      updateRankingLabels();

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

    if (sidePanelController.getCurrentPlayer() == null) {
      setDieButtonEnabled(false);
      return;
    }

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
    switch(rank){
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

    ImageView playerImage = new ImageView(
        sidePanelController.getPlayerImage(player)
    );
    playerImage.setFitWidth(80);
    playerImage.setFitHeight(80);

    Label nameLabel = css.createStyledLabel(player.getName(), FontWeight.BOLD, 16,
        getPlayerColor(player));

    Label positionLabel = css.createStyledLabel("Position: " + player.getPositionIndex(),
        FontWeight.NORMAL, 14, Color.WHITE);

    Label rankLabel = new Label();

    // Store position and rank label for updates
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