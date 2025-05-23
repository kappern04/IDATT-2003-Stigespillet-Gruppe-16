package edu.ntnu.iir.bidata.view.model;

import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.Observer;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testDefaultConstructor() {
        Player player = new Player("Alice");
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getPositionIndex());
        assertNull(player.getColor());
        assertEquals(1, player.getShipType());
    }

    @Test
    void testConstructorWithColor() {
        Player player = new Player("Bob", Color.BLUE);
        assertEquals("Bob", player.getName());
        assertEquals(Color.BLUE, player.getColor());
        assertEquals(1, player.getShipType());
    }

    @Test
    void testConstructorWithColorAndShipType() {
        Player player = new Player("Carol", Color.GREEN, 2);
        assertEquals("Carol", player.getName());
        assertEquals(Color.GREEN, player.getColor());
        assertEquals(2, player.getShipType());
    }

    @Test
    void testSettersAndGetters() {
        Player player = new Player("Test");
        player.setName("NewName");
        assertEquals("NewName", player.getName());

        player.setColor(Color.YELLOW);
        assertEquals(Color.YELLOW, player.getColor());

        player.setShipType(3);
        assertEquals(3, player.getShipType());
    }

    @Test
    void testSetPositionIndex() {
        Player player = new Player("Test");
        player.setPositionIndex(5);
        assertEquals(5, player.getPositionIndex());
    }

    @Test
    void testMoveAndFinishMove() {
        Player player = new Player("Test");
        player.move(4);
        assertTrue(player.isMoving());
        assertEquals(4, player.getPositionIndex());
        player.finishMove();
        assertFalse(player.isMoving());
    }

    @Test
    void testToString() {
        Player player = new Player("Test");
        assertTrue(player.toString().contains("Test"));
    }

    @Test
    void testObserverNotification() {
        Player player = new Player("Test");
        final boolean[] notified = {false};
        Observer<Player> observer = (p, event) -> {
            if ("NAME_CHANGED".equals(event)) notified[0] = true;
        };
        player.addObserver(observer);
        player.setName("Changed");
        assertTrue(notified[0]);
        player.removeObserver(observer);
    }
}