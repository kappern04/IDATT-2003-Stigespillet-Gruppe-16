package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardHasTiles() {
        assertNotNull(board.getTiles(), "Board should have a list of tiles");
        assertTrue(board.getTiles().size() > 0, "Board should have at least one tile");
    }

    @Test
    public void testGetTileByIndex() {
        Tile tile = board.getTiles().get(0);
        assertNotNull(tile, "First tile should not be null");
        assertEquals(0, tile.getIndex(), "First tile index should be 0");
    }

    @Test
    public void testLastTileIndex() {
        int lastIndex = board.getLastTile();
        assertEquals(board.getTiles().size() - 1, lastIndex, "Last tile index should match tiles size - 1");
    }
}
