package edu.ntnu.iir.bidata.view.model;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testDefaultBoardProperties() {
        assertEquals("Stigespillet 90", board.getBoardName());
        assertEquals("Standard snakes and ladders with 90 (10x9) tiles", board.getDescription());
        assertEquals(10, board.getX_dimension());
        assertEquals(9, board.getY_dimension());
    }

    @Test
    void testTilesCreatedCorrectly() {
        List<Tile> tiles = board.getTiles();
        assertNotNull(tiles);
        assertEquals(91, tiles.size()); // 0-90 inclusive
        assertEquals(0, tiles.get(0).getIndex());
        assertEquals(90, tiles.get(90).getIndex());
    }

    @Test
    void testGetTileByIndex() {
        Tile tile = board.getTile(1);
        assertNotNull(tile);
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testGetLastTile() {
        int lastIndex = board.getLastTile();
        assertEquals(90, lastIndex);
    }

    @Test
    void testSetAndGetTileNodeMap() {
        Map<Integer, Node> map = new HashMap<>();
        Node dummyNode = new Node() {};
        map.put(1, dummyNode);
        board.setTileNodeMap(map);
        Tile tile = board.getTile(1);
        assertEquals(dummyNode, board.getTileNode(tile));
    }

    @Test
    void testLadderActionsPresent() {
        // Check a few known ladder tiles
        assertTrue(board.getTile(1).getTileAction() instanceof LadderAction);
        assertTrue(board.getTile(8).getTileAction() instanceof LadderAction);
        assertTrue(board.getTile(87).getTileAction() instanceof LadderAction);
    }
}