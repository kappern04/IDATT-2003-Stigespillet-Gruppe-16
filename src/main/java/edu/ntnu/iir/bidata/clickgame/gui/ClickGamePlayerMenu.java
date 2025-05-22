package edu.ntnu.iir.bidata.clickgame.gui;

import edu.ntnu.iir.bidata.laddergame.view.menu.PlayerMenu;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import edu.ntnu.iir.bidata.laddergame.util.PlayerData;
import java.util.List;

public class ClickGamePlayerMenu extends PlayerMenu {
    private final GameMenu gameMenu;
    private Runnable onPlayersSelected;

    public ClickGamePlayerMenu() {
        super(new CSS());
        this.gameMenu = GameMenu.getInstance();

        // Get player details immediately using parent's method
        List<PlayerData> playerDetails = collectPlayerDetails(gameMenu.getPlayerCount());

        if (playerDetails != null) {
            gameMenu.setPlayerData(playerDetails);
            playersSelected();
        }
    }

    public void setOnPlayersSelected(Runnable onPlayersSelected) {
        this.onPlayersSelected = onPlayersSelected;
    }

    protected void playersSelected() {
        if (onPlayersSelected != null) {
            onPlayersSelected.run();
        }
    }
}