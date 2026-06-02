package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.laddergame.util.ShipUtils;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerMenu {
    private static final int SHIP_BUTTON_SIZE = 96;
    private static final int CARD_SPACING = 8;
    private static final int CARD_PADDING = 10;
    private static final int MAX_NAME_LENGTH = 15;
    private static final int TOTAL_SHIP_TYPES = 5;

    private final CSS css;

    public PlayerMenu(CSS css) {
        this.css = css;
    }

    public List<PlayerData> collectPlayerDetails(int numPlayers) {
        Dialog<List<PlayerData>> dialog = new Dialog<>();
        dialog.setTitle("Space Traveler Configuration");

        DialogPane dialogPane = setupDialogPane(dialog, numPlayers);
        createDialogHeader(numPlayers, dialogPane);

        TextField[] nameFields = new TextField[numPlayers];
        ColorPicker[] colorPickers = new ColorPicker[numPlayers];
        Button[] shipButtons = new Button[numPlayers];
        int[] selectedShips = new int[numPlayers];

        HBox cardsBox = createPlayerCards(numPlayers, nameFields, colorPickers, shipButtons, selectedShips);

        VBox content = new VBox(10, cardsBox, createInstructionsPanel());
        content.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(content);

        setupDialogButtons(dialog);
        setupResultConverter(dialog, numPlayers, nameFields, colorPickers, selectedShips, shipButtons);

        Optional<List<PlayerData>> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private DialogPane setupDialogPane(Dialog<List<PlayerData>> dialog, int numPlayers) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("space-dialog-pane");
        dialogPane.setPrefWidth(Math.max(440, numPlayers * 135 + 40));
        dialogPane.setPrefHeight(460);
        return dialogPane;
    }

    private void createDialogHeader(int numPlayers, DialogPane dialogPane) {
        Label headerLabel = css.createStyledLabel(
                "CONFIGURE YOUR " + numPlayers + " SPACE TRAVELERS",
                FontWeight.BOLD, 18, css.getSpaceBlue());
        headerLabel.setEffect(new javafx.scene.effect.Glow(0.6));

        Label subtitleLabel = css.createStyledLabel(
                "Customize each traveler's identity before departure",
                FontWeight.NORMAL, 14, Color.WHITE);

        VBox headerBox = new VBox(6, headerLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10, 0, 10, 0));
        dialogPane.setHeader(headerBox);
    }

    private HBox createPlayerCards(int numPlayers, TextField[] nameFields, ColorPicker[] colorPickers,
                                   Button[] shipButtons, int[] selectedShips) {
        HBox cardsBox = new HBox(10);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(10));

        for (int i = 0; i < numPlayers; i++) {
            cardsBox.getChildren().add(
                    createPlayerCard(i, nameFields, colorPickers, shipButtons, selectedShips)
            );
        }

        return cardsBox;
    }

    private VBox createPlayerCard(int playerIndex, TextField[] nameFields, ColorPicker[] colorPickers,
                                  Button[] shipButtons, int[] selectedShips) {
        VBox card = new VBox(CARD_SPACING);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(CARD_PADDING));
        card.getStyleClass().add("player-box");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label label = css.createStyledLabel("PILOT " + (playerIndex + 1), FontWeight.BOLD, 13, Color.WHITE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        colorPickers[playerIndex] = createColorPicker(playerIndex);
        selectedShips[playerIndex] = playerIndex % TOTAL_SHIP_TYPES + 1;
        shipButtons[playerIndex] = createShipSelectorButton(selectedShips, playerIndex, colorPickers);
        nameFields[playerIndex] = createNameField(playerIndex);

        colorPickers[playerIndex].valueProperty().addListener((obs, oldVal, newVal) ->
                updateShipButtonImage(shipButtons[playerIndex], selectedShips[playerIndex], newVal));

        card.getChildren().addAll(label, shipButtons[playerIndex], nameFields[playerIndex], colorPickers[playerIndex]);
        return card;
    }

    private TextField createNameField(int playerIndex) {
        TextField nameField = new TextField("Space Pilot " + (playerIndex + 1));
        nameField.getStyleClass().add("space-text-field");
        nameField.setMaxWidth(Double.MAX_VALUE);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > MAX_NAME_LENGTH) {
                nameField.setText(oldVal);
            }
        });

        return nameField;
    }

    private ColorPicker createColorPicker(int playerIndex) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("space-color-picker");
        colorPicker.setStyle("-fx-background-radius: 0; -fx-border-radius: 0; -fx-color-rect-width: 96; -fx-color-rect-heigth: 48;");
        colorPicker.setTooltip(css.createTooltip("Select your ship's color"));

        Color[] defaultColors = ShipUtils.getDefaultColors();
        colorPicker.setValue(defaultColors[playerIndex % defaultColors.length]);

        return colorPicker;
    }

    private Button createShipSelectorButton(int[] selectedShips, int playerIndex, ColorPicker[] colorPickers) {
        Button button = new Button();
        button.setPrefSize(SHIP_BUTTON_SIZE, SHIP_BUTTON_SIZE);
        button.getStyleClass().add("ship-selector-button");
        button.setTooltip(css.createTooltip("Click to select a different ship model"));

        updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());

        button.setOnAction(e -> {
            selectedShips[playerIndex] = (selectedShips[playerIndex] % TOTAL_SHIP_TYPES) + 1;

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
            scaleOut.setToX(0.8);
            scaleOut.setToY(0.8);
            scaleOut.play();

            scaleOut.setOnFinished(event -> {
                updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
                scaleIn.setToX(1.0);
                scaleIn.setToY(1.0);
                scaleIn.play();
            });
        });

        return button;
    }

    private VBox createInstructionsPanel() {
        javafx.scene.text.Text instructions = new javafx.scene.text.Text(
                "• Click on a ship to cycle through available models\n" +
                "• Choose a color that's easy to identify on the board\n" +
                "• Names should be unique for easier identification\n" +
                "• Your selections will affect your appearance in-game"
        );
        instructions.setFill(Color.LIGHTGRAY);

        VBox instructionsBox = new VBox(instructions);
        instructionsBox.setPadding(new Insets(6, 0, 0, 0));
        instructionsBox.setAlignment(Pos.CENTER);
        instructionsBox.getStyleClass().add("instructions-panel");

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(1000), instructionsBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        return instructionsBox;
    }

    private void setupDialogButtons(Dialog<List<PlayerData>> dialog) {
        ButtonType cancelButtonType = new ButtonType("ABORT LAUNCH", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButtonType = new ButtonType("INITIATE LAUNCH", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, confirmButtonType);

        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);

        confirmButton.setTooltip(css.createTooltip("Begin your space adventure with these travelers"));
        cancelButton.setTooltip(css.createTooltip("Return to previous screen"));
    }

    private void setupResultConverter(Dialog<List<PlayerData>> dialog, int numPlayers,
                                      TextField[] nameFields, ColorPicker[] colorPickers,
                                      int[] selectedShips, Button[] shipButtons) {
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                List<PlayerData> result = new ArrayList<>();
                for (int i = 0; i < numPlayers; i++) {
                    int shipType = (shipButtons[i] != null && shipButtons[i].getUserData() != null)
                            ? (int) shipButtons[i].getUserData()
                            : (i % TOTAL_SHIP_TYPES) + 1;
                    String name = nameFields[i] != null ? nameFields[i].getText().trim() : "Player " + (i + 1);
                    Color color = colorPickers[i] != null ? colorPickers[i].getValue() : Color.WHITE;
                    result.add(new PlayerData(name, color, shipType));
                }
                return result;
            }
            return null;
        });
    }

    private void updateShipButtonImage(Button button, int shipType, Color color) {
        try {
            button.setUserData(shipType);

            Image baseImage = ShipUtils.loadShipSprite(shipType);
            ImageView shipView = ShipUtils.createColoredShipImage(color, baseImage);

            DropShadow glow = new DropShadow();
            glow.setColor(color);
            glow.setRadius(15);
            shipView.setEffect(glow);

            ImageView upscaledView = PixelArtUpscaler.resizeImage(shipView.getImage(), SHIP_BUTTON_SIZE, SHIP_BUTTON_SIZE);
            upscaledView.setEffect(shipView.getEffect());

            button.setGraphic(upscaledView);
            button.setText("");
        } catch (Exception ex) {
            button.setText("Ship " + shipType);
            button.setGraphic(null);
        }
    }
}
