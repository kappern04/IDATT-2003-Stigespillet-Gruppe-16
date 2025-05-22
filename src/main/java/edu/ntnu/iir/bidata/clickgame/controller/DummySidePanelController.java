package edu.ntnu.iir.bidata.clickgame.controller;

import edu.ntnu.iir.bidata.laddergame.controller.board.SidePanelController;
import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.view.util.ShipUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class DummySidePanelController extends SidePanelController {
    public DummySidePanelController(BoardGameController boardGameController, PlayerController playerController) {
        super(boardGameController, playerController);
    }

    @Override
    public Image getPlayerImage(Player player) {
        int shipType = player.getShipType();
        Color color = player.getColor();
        Image baseImage = ShipUtils.loadShipSprite(shipType);
        ImageView coloredView = ShipUtils.createColoredShipImage(color, baseImage);
        return coloredView.getImage();
    }
}