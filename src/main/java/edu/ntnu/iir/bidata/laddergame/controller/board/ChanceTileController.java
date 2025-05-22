package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.view.board.ChanceTileView;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChanceTileController {
    private final Board board;
    private final ChanceTileView chanceTileView;
    private final Map<Integer, Node> chanceVisuals = new HashMap<>();
    private final Random random = new Random();

    /**
     * Creates a new ChanceTileController for managing chance tiles.
     * @param board the game board
     */
    public ChanceTileController(Board board) {
        this.board = board;
        this.chanceTileView = new ChanceTileView();
    }

    /**
     * Adds visual indicators for chance tiles to the game board.
     * @param boardPane the pane containing the board
     * @param tileNodeMap map from tile index to its visual representation
     */
    public void addChanceTilesToBoard(Pane boardPane, Map<Integer, Node> tileNodeMap) {
        List<Tile> tiles = board.getTiles();
        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            if (tile.getType().equals("chance")) {
                Node tileNode = tileNodeMap.get(tile.getIndex());
                Node chanceVisual = chanceTileView.createChanceTileVisual(tileNode);
                boardPane.getChildren().add(chanceVisual);
                chanceVisuals.put(tile.getIndex(), chanceVisual);
            }
        }
    }



    /**
     * Gets the visual representation for a specific chance tile.
     * @param tileIndex the index of the tile
     * @return the visual Node for the chance tile indicator
     */
    public Node getChanceVisual(int tileIndex) {
        return chanceVisuals.get(tileIndex);
    }
}