import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.BattleShipModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleShipModelTests {

    private BattleShipModel model;

    @BeforeEach
    public void setUp() {
        model = new BattleShipModel(10); // Initialize with a grid size of 10
    }

    @Test
    public void testPlaceShipValid() {
        assertTrue(model.placeShip(0, 0, "Carrier", true), "Should place Carrier vertically at (0,0)");
        assertNotNull(model.shipAt(0, 0), "Carrier should be at (0,0)");
    }

    @Test
    public void testPlaceShipInvalidOutOfBounds() {
        assertFalse(model.placeShip(6, 0, "Carrier", true), "Should not place Carrier out of bounds vertically");
        assertFalse(model.placeShip(0, 6, "Carrier", false), "Should not place Carrier out of bounds horizontally");
    }

    @Test
    public void testPlaceShipInvalidOverlap() {
        assertTrue(model.placeShip(0, 0, "Carrier", true), "Should place Carrier vertically at (0,0)");
        assertFalse(model.placeShip(0, 0, "Battleship", true), "Should not place Battleship overlapping Carrier");
    }

    @Test
    public void testTakeShotHit() {
        model.placeShip(0, 0, "Carrier", true);
        model.takeShot(0, 0, true);
        assertEquals(BattleShipModel.States.HIT, model.getYourHitsAt(0, 0), "Shot at (0,0) should be a hit");
    }

    @Test
    public void testTakeShotMiss() {
        model.takeShot(0, 0, false);
        assertEquals(BattleShipModel.States.MISS, model.getYourHitsAt(0, 0), "Shot at (0,0) should be a miss");
    }

    @Test
    public void testGetShotHit() {
        model.placeShip(0, 0, "Carrier", true);
        BattleShipModel.ShotResult result = model.getShot(0, 0);
        assertTrue(result.shotHit(), "Shot at (0,0) should hit Carrier");
    }

    @Test
    public void testGetShotMiss() {
        BattleShipModel.ShotResult result = model.getShot(0, 0);
        assertFalse(result.shotHit(), "Shot at (0,0) should miss");
    }

    @Test
    public void testIsSunk() {
        model.placeShip(0, 0, "Buoy", true);
        model.getShot(0, 0);
        assertTrue(model.isSunk("Buoy"), "Buoy should be sunk after being hit");
    }

    @Test
    public void testGetGridSize() {
        assertEquals(10, model.getGridSize(), "Grid size should be 10");
    }

    @Test
    public void testGetEnemyHitsAt() {
        model.getShot(0, 0);
        assertEquals(BattleShipModel.States.MISS, model.getEnemyHitsAt(0, 0), "Enemy shot at (0,0) should be a miss");
    }

    @Test
    public void testGetYourHitsAt() {
        model.takeShot(0, 0, true);
        assertEquals(BattleShipModel.States.HIT, model.getYourHitsAt(0, 0), "Your shot at (0,0) should be a hit");
    }
}
