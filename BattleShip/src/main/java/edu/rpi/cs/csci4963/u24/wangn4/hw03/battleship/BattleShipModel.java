package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * BattleShipModel represents the model for a Battleship game.
 * It manages the placement of ships, tracking hits and misses, and determining the state of the game.
 */
public class BattleShipModel {

    /**
     * States represent the possible states of a cell in the grid.
     */
    public enum States { HIT, MISS, EMPTY };

    /**
     * Ship represents a ship with its name, position, and orientation.
     */
    public record Ship(String name, int row, int col, boolean isVertical) {};

    /**
     * ShotResult represents the result of a shot, indicating whether it hit and if a ship was sunk.
     */
    public record ShotResult(Boolean shotHit, String shipSunk) {};

    private int gridSize;
    private String[][] shipsGrid;
    private Map<String, Ship> shipLookups;
    private States[][] enemyHitsGrid;
    private Map<String,Boolean> enemyShipsSunk;
    private States[][] yourHitsGrid;

    /**
     * A constant map of ship types and their corresponding sizes.
     */
    public static Map<String, Integer> SHIPS;

    static {
        SHIPS = new LinkedHashMap<>();
        SHIPS.put("Carrier", 5);
        SHIPS.put("Battleship", 4);
        SHIPS.put("Submarine", 3);
        SHIPS.put("Destroyer", 2);
        SHIPS.put("Buoy", 1);
    }

    /**
     * Constructor for BattleShipModel.
     *
     * @param gridSize the size of the grid for the game
     */
    public BattleShipModel(int gridSize) {
        this.gridSize = gridSize;
        this.yourHitsGrid = new States[gridSize][gridSize];
        this.enemyHitsGrid = new States[gridSize][gridSize];
        // initialize grids
        for (int i = 0; i < gridSize; i++){
            for (int j = 0; j < gridSize; j++) {
                yourHitsGrid[i][j] = States.EMPTY;
                enemyHitsGrid[i][j] = States.EMPTY;
            }
        }
        this.shipsGrid = new String[gridSize][gridSize];
        this.shipLookups = new HashMap<>();
        this.enemyShipsSunk = new HashMap<>();
        // initialize the all ships to not sunk
        for (String ship : SHIPS.keySet()) {
            enemyShipsSunk.put(ship, false);
        }
    }

    /**
     * Places a ship on the grid if the placement is valid and the ship has not been placed yet.
     *
     * @param row the starting row for the ship
     * @param col the starting column for the ship
     * @param name the name of the ship
     * @param isVertical whether the ship is placed vertically
     * @return true if the ship was placed successfully, false otherwise
     */
    public boolean placeShip(int row, int col, String name, boolean isVertical) {
        if (isPlacementValid(row, col, name, isVertical) && !shipLookups.containsKey(name)) {
            Integer shipSize = SHIPS.get(name);
            // place the ship at the coordinates based on is vertical
            for (int i = 0; i < shipSize; i++) {
                int placeRow = isVertical ? row + i : row;
                int placeCol = isVertical ? col : col + i;
                shipsGrid[placeRow][placeCol] = name;
            }
            Ship a = new Ship(name, row, col, isVertical);
            shipLookups.put(name, a);
            return true;
        }
        return false;
    }

    /**
     * Checks if the placement of a ship is valid.
     *
     * @param row the starting row for the ship
     * @param col the starting column for the ship
     * @param name the name of the ship
     * @param isVertical whether the ship is placed vertically
     * @return true if the placement is valid, false otherwise
     */
    private boolean isPlacementValid(int row, int col, String name, boolean isVertical) {
        // Check if the ship placement exceeds grid boundaries
        Integer shipSize = SHIPS.get(name);

        if (isVertical && row + shipSize > gridSize) {
            System.out.println("Vertically out of bounds. Ship Size: " + shipSize);
            return false;
        } else if (!isVertical && col + shipSize > gridSize) {
            System.out.println("Horizontally out of bounds. Ship Size: " + shipSize);
            return false;
        }

        // Check if any cell in the ship's path is already occupied
        for (int i = 0; i < shipSize; i++) {
            int checkRow = isVertical ? row + i : row;
            int checkCol = isVertical ? col : col + i;

            if (shipsGrid[checkRow][checkCol] != null) {
                System.out.println(shipsGrid[checkRow][checkCol]);
                return false;
            }
        }
        System.out.println(name + " placed in valid spot.");
        return true;
    }

    /**
     * Returns the name of the ship at the specified location, if any.
     *
     * @param row the row to check
     * @param col the column to check
     * @return the name of the ship at the specified location, or null if no ship is present
     */
    @Nullable
    public String shipAt(int row, int col) {
        return shipsGrid[row][col];
    }

    /**
     * Records a shot at the specified location on the grid.
     *
     * @param row the row of the shot
     * @param col the column of the shot
     * @param hit whether the shot was a hit
     */
    public void takeShot(int row, int col, boolean hit) {
        if (hit) { // set to hit
            yourHitsGrid[row][col] = States.HIT;
        } else { // set to miss
            yourHitsGrid[row][col] = States.MISS;
        }
    }

    /**
     * Marks a ship as sunk.
     *
     * @param name the name of the ship to mark as sunk
     */
    public void sinkShip(String name) {
        enemyShipsSunk.put(name, true);
    }

    /**
     * Returns the result of a shot at the specified location on the grid.
     *
     * @param row the row of the shot
     * @param col the column of the shot
     * @return a ShotResult indicating whether the shot was a hit and if a ship was sunk
     */
    public ShotResult getShot(int row, int col) {
        String val = shipAt(row, col);
        // hit
        if (val != null) {
            enemyHitsGrid[row][col] = States.HIT;
            System.out.println("Setting row " + row + " col " + col + " to hit");
            return new ShotResult(true, isSunk(val) ? val : "none");
        } else { // miss
            System.out.println("Setting row " + row + " col " + col + " to miss");
            enemyHitsGrid[row][col] = States.MISS;
            return new ShotResult(false, "none");
        }
    }

    /**
     * Checks if a ship has been sunk.
     *
     * @param name the name of the ship to check
     * @return true if the ship is sunk, false otherwise
     */
    public boolean isSunk(String name) {
        Ship ship = shipLookups.get(name);
        Integer shipSize = SHIPS.get(name);
        boolean isVertical = ship.isVertical();
        int row = ship.row();
        int col = ship.col();
        // check if a ship is sunk by looping through the coordinates
        for (int i = 0; i < shipSize; i++) {
            int checkRow = isVertical ? row + i : row;
            int checkCol = isVertical ? col : col + i;
            if (name.equals("Buoy")) System.out.println("Checking row " + checkRow + " col " + checkCol);
            if (enemyHitsGrid[checkRow][checkCol] != States.HIT) {
                System.out.println("Returning false!");
                return false;
            }
        }
        System.out.println("Returning true!");
        return true;
    }

    /**
     * Checks if all ships have been placed.
     *
     * @return true if all ships are placed, false otherwise
     */
    public boolean placed() {
        return shipLookups.size() == SHIPS.size();
    }

    /**
     * Returns the size of the grid.
     *
     * @return the grid size
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Returns the state of the enemy's hits at the specified location.
     *
     * @param row the row to check
     * @param col the column to check
     * @return the state of the enemy's hits at the specified location
     */
    public States getEnemyHitsAt(int row, int col) {
        return enemyHitsGrid[row][col];
    }

    /**
     * Returns the state of your hits at the specified location.
     *
     * @param row the row to check
     * @param col the column to check
     * @return the state of your hits at the specified location
     */
    public States getYourHitsAt(int row, int col) {
        return yourHitsGrid[row][col];
    }

    /**
     * Returns if the player has won (all enemy ships are sunk)
     *
     * @return true if all ships sunk
     */
    public boolean win() {
        for (String ship : SHIPS.keySet()) {
            // if all ships are sunk return true
            if (enemyShipsSunk.get(ship) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the player has lost (all player ship cells are hit)
     *
     * @return true if all cells are hit
     */
    public boolean lose() {
        int count = 0;
        for (int i = 0; i < gridSize; i++){
            for (int j = 0; j < gridSize; j++) {
                if (enemyHitsGrid[i][j] == States.HIT) count++;
                if (count == (5+4+3+2+1)) return true;
            }
        }
        return false;
    }

}
