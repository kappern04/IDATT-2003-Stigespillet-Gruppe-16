package edu.ntnu.iir.bidata.view.model;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Player;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderActionTest {

    @Test
    void testGetAndSetDestinationTileIndex() {
        LadderAction action = new LadderAction(10);
        assertEquals(10, action.getDestinationTileIndex());
        action.setDestinationTileIndex(20);
        assertEquals(20, action.getDestinationTileIndex());
    }

    @Test
    void testPerformActionSetsPlayerPosition() {
        LadderAction action = new LadderAction(15);
        Player player = new Player("Test", Color.RED);
        action.performAction(player);
        assertEquals(15, player.getPositionIndex());
    }

    @Test
    void testToStringContainsDestination() {
        LadderAction action = new LadderAction(5);
        assertTrue(action.toString().contains("destinationTile=5"));
    }

    @Test
    void testLeadsToPosition() {
        LadderAction action = new LadderAction(42);
        Board board = new Board();
        assertTrue(action.leadsToPosition(board, 42));
        assertFalse(action.leadsToPosition(board, 41));
    }
}