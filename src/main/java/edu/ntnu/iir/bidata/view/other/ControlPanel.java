package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.other.InGameMenuController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlPanel {
    private BoardGameController boardGameController;
    private MusicController musicController;
    private MusicControlPanel musicControlPanel;
    private CSS css;

    public ControlPanel(BoardGameController boardGameController, MusicController musicController) {
        this.boardGameController = boardGameController;
        this.musicController = musicController;
        this.musicControlPanel = new MusicControlPanel(musicController);
        this.css = new CSS();
    }

    public HBox createControlPanel() {
        HBox controlPanel = new HBox(10);
        controlPanel.setAlignment(Pos.CENTER_LEFT);

        // Create menu button
        Button menuButton = css.createSpaceButton("Menu");
        menuButton.setOnAction(e -> showInGameMenu());

        // Add music controls and menu button
        HBox musicPanel = musicControlPanel.createControlPanel();

        controlPanel.getChildren().addAll(musicPanel, menuButton);

        return controlPanel;
    }

    private void showInGameMenu() {
        InGameMenuController controller = new InGameMenuController(boardGameController, musicController);
        InGameMenu menu = new InGameMenu(controller);
        menu.show();
    }

}