package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.view.util.PlayerData;
import edu.ntnu.iir.bidata.laddergame.view.util.CSS;
import edu.ntnu.iir.bidata.laddergame.view.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.laddergame.view.util.ShipUtils;
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
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerMenu {
    private static final int SHIP_BUTTON_SIZE = 64;
    private static final int GRID_SPACING = 15;
    private static final int PADDING = 20;
    private static final int MAX_NAME_LENGTH = 15;
    private static final int TOTAL_SHIP_TYPES = 5;

    private final CSS css;

    public PlayerMenu(CSS css) {
        this.css = css;
    }

    public List<PlayerData> collectPlayerDetails(int numPlayers) {
        Dialog<List<PlayerData>> dialog = new Dialog<>();
        dialog.setTitle("Space Traveler Configuration");

        DialogPane dialogPane = setupDialogPane(dialog);
        createDialogHeader(numPlayers, dialogPane);

        GridPane grid = createPlayerInputGrid(numPlayers);

        BorderPane contentPane = new BorderPane();
        contentPane.setCenter(grid);
        contentPane.setBottom(createInstructionsPanel());

        dialog.getDialogPane().setContent(contentPane);

        setupDialogButtons(dialog);
        setupResultConverter(dialog, numPlayers, grid);

        Optional<List<PlayerData>> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private DialogPane setupDialogPane(Dialog<List<PlayerData>> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("space-dialog-pane");
        dialogPane.setPrefWidth(700);
        dialogPane.setPrefHeight(500);
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

        VBox headerBox = new VBox(10, headerLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10, 0, 20, 0));
        dialogPane.setHeader(headerBox);
    }

    private GridPane createPlayerInputGrid(int numPlayers) {
        GridPane grid = new GridPane();
        grid.setHgap(GRID_SPACING);
        grid.setVgap(GRID_SPACING);
        grid.setPadding(new Insets(PADDING));
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("space-dialog-grid");

        addColumnHeaders(grid);

        TextField[] nameFields = new TextField[numPlayers];
        ColorPicker[] colorPickers = new ColorPicker[numPlayers];
        Button[] shipButtons = new Button[numPlayers];
        int[] selectedShips = new int[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            createPlayerRow(grid, i, nameFields, colorPickers, shipButtons, selectedShips);
        }

        return grid;
    }

    private void addColumnHeaders(GridPane grid) {
        Label playerHeader = css.createStyledLabel("TRAVELER:", FontWeight.NORMAL, 14, Color.LIGHTGRAY);
        Label nameHeader = css.createStyledLabel("NAME:", FontWeight.NORMAL, 14, Color.LIGHTGRAY);
        Label colorHeader = css.createStyledLabel("COLOR:", FontWeight.NORMAL, 14, Color.LIGHTGRAY);
        Label shipHeader = css.createStyledLabel("SHIP:", FontWeight.NORMAL, 14, Color.LIGHTGRAY);

        Tooltip.install(nameHeader, css.createTooltip("Enter a name for this traveler (max 15 chars)"));
        Tooltip.install(colorHeader, css.createTooltip("Select a color for this traveler's ship"));
        Tooltip.install(shipHeader, css.createTooltip("Click to cycle through available ship models"));

        grid.add(playerHeader, 0, 0);
        grid.add(nameHeader, 1, 0);
        grid.add(colorHeader, 2, 0);
        grid.add(shipHeader, 3, 0);
    }

    private void createPlayerRow(GridPane grid, int playerIndex, TextField[] nameFields,
                                 ColorPicker[] colorPickers, Button[] shipButtons, int[] selectedShips) {

        Label label = css.createStyledLabel("PILOT " + (playerIndex + 1), FontWeight.BOLD, 14, Color.WHITE);
        label.getStyleClass().add("player-label");

        nameFields[playerIndex] = createNameField(playerIndex);
        colorPickers[playerIndex] = createColorPicker(playerIndex);

        selectedShips[playerIndex] = playerIndex % TOTAL_SHIP_TYPES + 1;
        shipButtons[playerIndex] = createShipSelectorButton(selectedShips, playerIndex, colorPickers);

        // Update ship button image when color changes
        colorPickers[playerIndex].valueProperty().addListener((obs, oldVal, newVal) ->
                updateShipButtonImage(shipButtons[playerIndex], selectedShips[playerIndex], newVal));

        grid.add(label, 0, playerIndex + 1);
        grid.add(nameFields[playerIndex], 1, playerIndex + 1);
        grid.add(colorPickers[playerIndex], 2, playerIndex + 1);
        grid.add(shipButtons[playerIndex], 3, playerIndex + 1);
    }

    private TextField createNameField(int playerIndex) {
        TextField nameField = new TextField("Space Pilot " + (playerIndex + 1));
        nameField.getStyleClass().add("space-text-field");
        nameField.setPrefWidth(200);

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
        colorPicker.setTooltip(css.createTooltip("Select your ship's color"));

        // Set default color from ShipUtils
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
        Text instructions = new Text(
                "• Click on a ship to cycle through available models\n" +
                        "• Choose a color that's easy to identify on the board\n" +
                        "• Names should be unique for easier identification\n" +
                        "• Your selections will affect your appearance in-game"
        );
        instructions.setFill(Color.LIGHTGRAY);

        VBox instructionsBox = new VBox(instructions);
        instructionsBox.setPadding(new Insets(PADDING, 0, 0, 0));
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

    private void setupResultConverter(Dialog<List<PlayerData>> dialog, int numPlayers, GridPane grid) {
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                List<PlayerData> result = new ArrayList<>();
                for (int index = 0; index < numPlayers; index++) {
                    final int i = index; // Create effectively final variable
                    TextField nameField = (TextField) grid.getChildren().stream()
                            .filter(node -> GridPane.getRowIndex(node) == i + 1 && GridPane.getColumnIndex(node) == 1)
                            .findFirst().orElse(null);

                    ColorPicker colorPicker = (ColorPicker) grid.getChildren().stream()
                            .filter(node -> GridPane.getRowIndex(node) == i + 1 && GridPane.getColumnIndex(node) == 2)
                            .findFirst().orElse(null);

                    Button shipButton = (Button) grid.getChildren().stream()
                            .filter(node -> GridPane.getRowIndex(node) == i + 1 && GridPane.getColumnIndex(node) == 3)
                            .findFirst().orElse(null);

                    int shipType = (shipButton != null && shipButton.getUserData() != null) ?
                            (int) shipButton.getUserData() : (i % TOTAL_SHIP_TYPES) + 1;

                    String name = nameField != null ? nameField.getText().trim() : "Player " + (i + 1);
                    Color color = colorPicker != null ? colorPicker.getValue() : Color.WHITE;

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

            int targetSize = SHIP_BUTTON_SIZE;
            ImageView upscaledView = PixelArtUpscaler.resizeImage(
                    shipView.getImage(), targetSize, targetSize
            );
            upscaledView.setEffect(shipView.getEffect());

            button.setGraphic(upscaledView);
            button.setText("");
        } catch (Exception ex) {
            button.setText("Ship " + shipType);
            button.setGraphic(null);
        }
    }


}