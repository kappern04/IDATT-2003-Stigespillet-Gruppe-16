package edu.ntnu.iir.bidata.laddergame.controller.board;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.view.board.LadderView;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LadderController {
    private final Board board;
    private final LadderView ladderView;
    private final Map<String, Node> ladderVisuals = new HashMap<>();
    private static final String SOUND_UP = "/audio/portal.wav";
    private static final String SOUND_DOWN = "/audio/portal2.wav";

    public LadderController(Board board) {
        this.board = board;
        this.ladderView = new LadderView();
    }

    /**
     * Adds all ladder visuals to the given pane, using the board's tile node map.
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
                ladderVisuals.put(ladderKey(tile.getIndex(), destinationTile.getIndex()), ladderVisual);
            }
        }
    }


    /**
     * Plays the ladder sound effect.
     */
    public void playLadderSound(int fromIndex, int toIndex) {
        String soundFile = (toIndex > fromIndex) ? SOUND_UP : SOUND_DOWN;
        try {
            Media sound = new Media(getClass().getResource(soundFile).toExternalForm());
            MediaPlayer player = new MediaPlayer(sound);
            player.setOnEndOfMedia(player::dispose);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String ladderKey(int from, int to) {
        return from + "-" + to;
    }
}