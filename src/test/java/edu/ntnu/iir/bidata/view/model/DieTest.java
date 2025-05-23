// File: src/test/java/edu/ntnu/iir/bidata/laddergame/model/DieTest.java
package edu.ntnu.iir.bidata.view.model;

import edu.ntnu.iir.bidata.laddergame.model.Die;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DieTest {

    @Test
    void testInitialLastRollIsZero() {
        Die die = new Die();
        assertEquals(0, die.getLastRoll());
    }

    @Test
    void testRollSetsValueInRange() {
        Die die = new Die();
        for (int i = 0; i < 100; i++) {
            die.roll();
            int value = die.getLastRoll();
            assertTrue(value >= 1 && value <= 6, "Roll out of range: " + value);
        }
    }

    @Test
    void testSetLastRollValid() {
        Die die = new Die();
        for (int i = 1; i <= 6; i++) {
            die.setLastRoll(i);
            assertEquals(i, die.getLastRoll());
        }
    }

    @Test
    void testSetLastRollInvalidThrows() {
        Die die = new Die();
        assertThrows(IllegalArgumentException.class, () -> die.setLastRoll(0));
        assertThrows(IllegalArgumentException.class, () -> die.setLastRoll(7));
    }

    @Test
    void testObserverNotifiedOnSetLastRoll() {
        Die die = new Die();
        final boolean[] notified = {false};
        die.addObserver((d, event) -> {
            if ("VALUE_CHANGED".equals(event)) notified[0] = true;
        });
        die.setLastRoll(3);
        assertTrue(notified[0]);
    }

    @Test
    void testObserverNotifiedOnRoll() {
        Die die = new Die();
        final boolean[] notified = {false};
        die.addObserver((d, event) -> {
            if ("ROLL".equals(event)) notified[0] = true;
        });
        die.roll();
        assertTrue(notified[0]);
    }

    @Test
    void testToString() {
        Die die = new Die();
        die.setLastRoll(5);
        assertTrue(die.toString().contains("lastRoll=5"));
    }
}