package edu.ntnu.iir.bidata.view.menu;

import edu.ntnu.iir.bidata.view.util.PlayerData;
import edu.ntnu.iir.bidata.view.util.CSS;
import edu.ntnu.iir.bidata.view.util.PixelArtUpscaler;
import edu.ntnu.iir.bidata.view.util.ShipUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerMenu {
    private final CSS css;
    private static final Color[] DEFAULT_COLORS = {Color.RED, Color.BLUE, Color.PURPLE, Color.ORANGE};

    public PlayerMenu(CSS css) {
        this.css = css;
    }

    public List<PlayerData> collectPlayerDetails(int numPlayers) {
        Dialog<List<PlayerData>> dialog = new Dialog<>();
        dialog.setTitle("Space Traveler Identification");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("space-dialog-pane");

        Label headerLabel = css.createStyledLabel(
                "ENTER DETAILS FOR YOUR " + numPlayers + " SPACE TRAVELERS",
                FontWeight.BOLD, 16, css.getSpaceBlue());
        headerLabel.setEffect(new Glow(0.5));
        VBox headerBox = new VBox(10, headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        dialogPane.setHeader(headerBox);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("space-dialog-grid");

        // Add column headers
        Label nameHeader = css.createStyledLabel("NAME:", FontWeight.NORMAL, 14, Color.WHITE);
        Label colorHeader = css.createStyledLabel("COLOR:", FontWeight.NORMAL, 14, Color.WHITE);
        Label shipHeader = css.createStyledLabel("SHIP:", FontWeight.NORMAL, 14, Color.WHITE);
        grid.add(nameHeader, 1, 0);
        grid.add(colorHeader, 2, 0);
        grid.add(shipHeader, 3, 0);

        TextField[] nameFields = new TextField[numPlayers];
        ColorPicker[] colorPickers = new ColorPicker[numPlayers];
        Button[] shipButtons = new Button[numPlayers];
        int[] selectedShips = new int[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            Label label = css.createStyledLabel("TRAVELER " + (i + 1) + ":", FontWeight.NORMAL, 14, Color.WHITE);

            nameFields[i] = new TextField("Traveler " + (i + 1));
            nameFields[i].getStyleClass().add("space-text-field");
            nameFields[i].setPrefWidth(200);

            colorPickers[i] = new ColorPicker(DEFAULT_COLORS[i % DEFAULT_COLORS.length]);
            colorPickers[i].getStyleClass().add("space-color-picker");

            selectedShips[i] = 1;
            int playerIndex = i;

            shipButtons[i] = createShipSelectorButton(selectedShips, playerIndex, colorPickers);

            // Update ship preview when color changes
            colorPickers[i].valueProperty().addListener((obs, oldVal, newVal) ->
                    updateShipButtonImage(shipButtons[playerIndex], selectedShips[playerIndex], newVal));

            grid.add(label, 0, i + 1);
            grid.add(nameFields[i], 1, i + 1);
            grid.add(colorPickers[i], 2, i + 1);
            grid.add(shipButtons[i], 3, i + 1);
        }

        dialog.getDialogPane().setContent(grid);

        ButtonType cancelButtonType = new ButtonType("ABORT", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmButtonType = new ButtonType("LAUNCH MISSION", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, confirmButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                List<PlayerData> result = new ArrayList<>();
                for (int i = 0; i < numPlayers; i++) {
                    String name = nameFields[i].getText().trim();
                    if (name.isEmpty()) name = "Anonymous Traveler";
                    Color color = colorPickers[i].getValue();
                    result.add(new PlayerData(name, color, selectedShips[i]));
                }
                return result;
            }
            return null;
        });

        Optional<List<PlayerData>> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private Button createShipSelectorButton(int[] selectedShips, int playerIndex, ColorPicker[] colorPickers) {
        Button button = new Button();
        button.setPrefSize(48, 48);
        button.getStyleClass().add("ship-selector-button");
        updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());
        button.setOnAction(e -> {
            selectedShips[playerIndex] = (selectedShips[playerIndex] % 5) + 1;
            updateShipButtonImage(button, selectedShips[playerIndex], colorPickers[playerIndex].getValue());
        });
        return button;
    }

    private void updateShipButtonImage(Button button, int shipType, Color color) {
        try {
            Image baseImage = ShipUtils.loadShipSprite(shipType);
            ImageView shipView = ShipUtils.createColoredShipImage(color, baseImage);
            int targetSize = 48;
            ImageView upscaledView = PixelArtUpscaler.resizeImage(
                    shipView.getImage(), targetSize, targetSize
            );
            upscaledView.setEffect(shipView.getEffect());
            button.setGraphic(upscaledView);
            button.setText("");
        } catch (Exception ex) {
            button.setText("Ship " + shipType);
        }
    }
}