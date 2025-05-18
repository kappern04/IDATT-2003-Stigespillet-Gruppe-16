package edu.ntnu.iir.bidata.controller.board;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.view.board.LadderView;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;

public class LadderController {
    private final Board board;
    private final LadderView ladderView;

    public LadderController(Board board) {
        this.board = board;
        this.ladderView = new LadderView();
    }

    /**
     * Adds all ladder visuals to the given pane, using the board's tile node map.
     * @param boardPane The pane to add ladder visuals to.
     * @param tileNodeMap Map of tile indices to their UI nodes.
     */
    public void addLaddersToBoard(Pane boardPane, Map<Integer, Node> tileNodeMap) {
        List<Tile> tiles = board.getTiles();
        for (Tile tile : tiles) {
            if (tile.getIndex() == 0) continue;
            if (tile.hasLadderAction()) {
                Tile destinationTile = tile.getLadderDestination(board);
                Node fromTileNode = tileNodeMap.get(tile.getIndex());
                Node toTileNode = tileNodeMap.get(destinationTile.getIndex());
                boolean isLadderUp = tile.getIndex() < destinationTile.getIndex();
                Node ladderVisual = ladderView.createLadderVisual(fromTileNode, toTileNode, isLadderUp);
                boardPane.getChildren().add(ladderVisual);
            }
        }
    }
}