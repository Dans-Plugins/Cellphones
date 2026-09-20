package dansplugins.cellphones.towers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverageTest {

    @Test
    void pointInsideRadiusIsCovered() {
        assertTrue(Coverage.covers(0, 64, 0, 30, 64, 40, 50, false));
    }

    @Test
    void pointOnTheBoundaryIsCovered() {
        assertTrue(Coverage.covers(0, 64, 0, 30, 64, 40, 50, true));
    }

    @Test
    void pointOutsideRadiusIsNotCovered() {
        assertFalse(Coverage.covers(0, 64, 0, 30, 64, 41, 50, false));
    }

    @Test
    void heightIsIgnoredWhenAsked() {
        assertTrue(Coverage.covers(0, 200, 0, 10, 0, 10, 50, true));
        assertFalse(Coverage.covers(0, 200, 0, 10, 0, 10, 50, false));
    }

    @Test
    void negativeRadiusCoversNothing() {
        assertFalse(Coverage.covers(0, 0, 0, 0, 0, 0, -1, true));
    }
}
