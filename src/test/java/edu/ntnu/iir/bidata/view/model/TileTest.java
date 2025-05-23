// File: src/test/java/edu/ntnu/iir/bidata/laddergame/model/TileTest.java
package edu.ntnu.iir.bidata.view.model;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import edu.ntnu.iir.bidata.laddergame.model.TileAction;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

    @Test
    void testConstructorAndGetters() {
        TileAction action = new LadderAction(5);
        Tile tile = new Tile(1, 2, 3, action);
        assertEquals(1, tile.getIndex());
        assertEquals(2, tile.getX());
        assertEquals(3, tile.getY());
        assertEquals(action, tile.getTileAction());
    }

    @Test
    void testSetTileAction() {
        Tile tile = new Tile(1, 0, 0, null);
        TileAction action = new LadderAction(7);
        tile.setTileAction(action);
        assertEquals(action, tile.getTileAction());
    }

    @Test
    void testHasLadderAction() {
        Tile tile = new Tile(1, 0, 0, new LadderAction(2));
        assertTrue(tile.hasLadderAction());
        tile.setTileAction(null);
        assertFalse(tile.hasLadderAction());
    }

    @Test
    void testIsDestinationOfLadder() {
        Board board = new Board();
        Tile ladderStart = new Tile(0, 0, 0, new LadderAction(2));
        Tile normal = new Tile(1, 1, 1, null);
        Tile ladderEnd = new Tile(2, 2, 2, null);
        board.getTiles().add(ladderStart);
        board.getTiles().add(normal);
        board.getTiles().add(ladderEnd);

        assertTrue(ladderEnd.isDestinationOfLadder(board));
        assertFalse(normal.isDestinationOfLadder(board));
    }

    @Test
    void testGetLadderDestination() {
        Board board = new Board();
        Tile ladderStart = new Tile(0, 0, 0, new LadderAction(2));
        Tile normal = new Tile(1, 1, 1, null);
        Tile ladderEnd = new Tile(2, 2, 2, null);
        board.getTiles().add(ladderStart);
        board.getTiles().add(normal);
        board.getTiles().add(ladderEnd);

        assertEquals(ladderEnd, ladderStart.getLadderDestination(board));
        assertNull(normal.getLadderDestination(board));
    }
}