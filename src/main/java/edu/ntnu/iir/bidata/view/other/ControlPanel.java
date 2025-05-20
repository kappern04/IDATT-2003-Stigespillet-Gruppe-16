package edu.ntnu.iir.bidata.view.other;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.controller.menu.InGameMenuController;
import edu.ntnu.iir.bidata.controller.other.MusicController;
import edu.ntnu.iir.bidata.view.menu.InGameMenu;
import edu.ntnu.iir.bidata.view.util.CSS;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Provides a control panel with music controls and a menu button.
 */
public class ControlPanel {
    private final BoardGameController boardGameController;
    private final MusicController musicController;
    private final MusicControlPanel musicControlPanel;
    private final CSS css;

    /**
     * Constructs a ControlPanel.
     *
     * @param boardGameController the board game controller
     * @param musicController the music controller
     */
    public ControlPanel(BoardGameController boardGameController, MusicController musicController) {
        this.boardGameController = boardGameController;
        this.musicController = musicController;
        this.musicControlPanel = new MusicControlPanel(musicController);
        this.css = new CSS();
    }

    /**
     * Creates the control panel UI.
     *
     * @return the HBox containing the control panel
     */
    public HBox createControlPanel() {
        HBox controlPanel = new HBox(30);
        controlPanel.setAlignment(Pos.CENTER_RIGHT);

        Button menuButton = css.createSpaceButton("Menu");
        menuButton.getStyleClass().add("space-button-small");
        menuButton.setOnAction(e -> showInGameMenu());

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