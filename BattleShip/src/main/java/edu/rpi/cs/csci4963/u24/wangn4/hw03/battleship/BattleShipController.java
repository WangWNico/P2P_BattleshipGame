package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;

import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.BattleShipProtocol;
import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.Connectable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller class for the BattleShip game.
 * Handles user interactions and updates the game model and view accordingly.
 */
public class BattleShipController {

    @FXML
    private VBox configurationPanel;

    @FXML
    public TextField timerTextField;

    @FXML
    private GridPane ownShipsGrid;

    @FXML
    private GridPane enemyShipsGrid;

    @FXML
    private CheckBox darkModeCheckBox;

    @FXML
    private Label timerLabel;

    private Timeline timeline;
    private int timeSeconds;

    private Scene scene;
    private Connectable me;
    private BattleShipModel selfModel;
    private BattleShipProtocol protocol;

    // For placing ships
    private String[] ships;
    private Integer shipNumber = 0;

    boolean timer = false;

    /**
     * Initializes the controller.
     * Sets up the game grids and the initial game model.
     */
    @FXML
    private void initialize() {
        int initialGridSize = 10; // Default grid size
        selfModel = new BattleShipModel(initialGridSize);
        setupGrid(ownShipsGrid, selfModel);
        setupGrid(enemyShipsGrid, selfModel);
        ships = BattleShipModel.SHIPS.keySet().toArray(new String[0]);

        // Initialize the timer with the starting time (e.g., 30 seconds)
        timeSeconds = 30;
    }

    /**
     * Toggles the visibility of the configuration panel.
     */
    @FXML
    private void toggleConfiguration() {
        boolean isVisible = configurationPanel.isVisible();
        configurationPanel.setVisible(!isVisible);
        configurationPanel.setManaged(!isVisible);
    }

    /**
     * Sets the game timer based on user input.
     * Validates the input and displays alerts for invalid values.
     */
    @FXML
    private void setTimer() {
        try {
            String timerValue = timerTextField.getText().trim();
            if (!timerValue.isEmpty()) {
                int timer = Integer.parseInt(timerValue);
                if (timer >= 10 && timer <= 60) {
                    timeSeconds = timer;
                    timerLabel.setText(timerValue);
                    startTimer();
                } else {
                    BattleShipUtils.showAlert("Invalid Timer", "Timer must be between 10 and 60.");
                }
            } else {
                BattleShipUtils.showAlert("Invalid Input", "Timer value cannot be empty.");
            }
        } catch (NumberFormatException e) {
            BattleShipUtils.showAlert("Invalid Input", "Timer must be a valid integer.");
        }
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Starts the timer, updating the label as it counts down
     */
    public void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        // Ensure timeSeconds is reset to the desired initial value
        AtomicInteger time = new AtomicInteger(timeSeconds);
        timerLabel.setText(Integer.toString(time.get()));

        if (timer) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), event -> {
                        time.getAndDecrement();
                        // Update the timer label
                        timerLabel.setText(Integer.toString(time.get()));

                        if (time.get() <= 0) {
                            timeline.stop();
                            // Note Timer doesnt actually have game win feature ig.
                        }
                    })
            );
            timeline.playFromStart();
        }
    }


    /**
     * Sets up the game grid based on the model's grid size.
     * Clears existing grid elements and initializes new ones.
     *
     * @param gridPane the grid to set up
     * @param model the model containing the grid size
     */
    private void setupGrid(GridPane gridPane, BattleShipModel model) {
        // clear previous grid
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();
        int gridSize = model.getGridSize();
        // create grid cells
        for (int i = 0; i < gridSize; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(40);
            gridPane.getRowConstraints().add(rowConstraints);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(40);
            gridPane.getColumnConstraints().add(columnConstraints);
        }
        // add grid cells
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Label cell = createCell(gridPane, row, col);
                gridPane.add(cell, col, row);
            }
        }
    }

    /**
     * Creates a cell for the grid.
     * Sets up event handlers for placing ships or shots.
     *
     * @param gridPane the grid to which the cell belongs
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return the created cell
     */
    private Label createCell(GridPane gridPane, int row, int col) {
        Label cell = new Label();
        cell.setStyle("-fx-border-color: black; -fx-background-color: blue;");
        cell.setPrefSize(40, 40);
        // create cell on own grid
        if (gridPane == ownShipsGrid) {
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    placeShip(gridPane, row, col, true);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    placeShip(gridPane, row, col, false);
                }
            });
        }
        // create cell on enemy grid
        if (gridPane == enemyShipsGrid) {
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    try {
                        placeShot(gridPane, row, col);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        return cell;
    }

    /**
     * Places a ship on the grid.
     * Validates the placement and updates the model and view.
     *
     * @param gridPane the grid on which to place the ship
     * @param row the row index of the starting position
     * @param col the column index of the starting position
     * @param isVertical true if the ship is placed vertically, false if horizontally
     */
    private void placeShip(GridPane gridPane, int row, int col, boolean isVertical) {
        // if not done placing and on own grid, ready to place
        if (!selfModel.placed() && gridPane == ownShipsGrid) {
            int shipSize = BattleShipModel.SHIPS.get(ships[shipNumber]);
            String shipName = ships[shipNumber];
            // if placement valid, place the ship
            if (selfModel.placeShip(row, col, shipName, isVertical)) {
                updateShipUI(gridPane, row, col, shipSize, isVertical);
                // iterate through to next ship
                shipNumber++;
                // if done placing ships
                if (selfModel.placed()) {
                    protocol = new BattleShipProtocol(selfModel, me, this);
                    me.send("ready");
                    if (!me.hasMessage()) { BattleShipUtils.showDialogue("Waiting for other player..."); }
                    String response = me.receive();
                    if (!response.equals("ready")) { BattleShipUtils.showAlert("Invalid packet!", "Expected \"ready\", received " + response); }
                    if (!me.isTurn()) {
                        // open turn wait window
                        BattleShipUtils.showDialogue("Waiting for peer to shoot.");
                        response = me.receive();
                        protocol.process(response);
                    } else {
                        BattleShipUtils.showDialogue("All ships placed. You can start shooting.");
                        timerLabel.setText(Integer.toString(timeSeconds));
                        startTimer();
                    }
                }
            } else {
                BattleShipUtils.showAlert("Invalid Placement", "Ship cannot be placed here.");
            }
        } else if (!selfModel.placed()) {
            BattleShipUtils.showAlert("Invalid Placement", "Ship cannot be placed here.");
        }
    }

    /**
     * Places a shot on the enemy's grid.
     * Validates the action and updates the model and view.
     *
     * @param gridpane the grid on which to place the shot
     * @param row the row index of the shot
     * @param col the column index of the shot
     */
    private void placeShot(GridPane gridpane, int row, int col) throws InterruptedException {
        // if own turn and all ships are placed, ready to shoot
        if (selfModel.placed() && me.isTurn()) {
            // if shooting at enemy update
            if (gridpane == enemyShipsGrid) {
                Label shotCell = new Label();
                shotCell.setPrefSize(40, 40);
                me.send("shot " + row + " " + col);
                shotCell.setStyle("-fx-border-color: black; -fx-background-color: pink;");
                gridpane.add(shotCell, col, row);
                // get result
                String response = me.receive();
                protocol.process(response);
                updateShotUI(enemyShipsGrid);
                // switch turn
                me.switchTurn();
                // stop timer
                timer = false;
                // thread to receive opponents next shot
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = me.receive();
                        protocol.process(response);
                    }
                }).start();
            }
        } else if (!me.isTurn()) {
            BattleShipUtils.showAlert("Can't Shoot Yet","It's your opponent's turn.");
        }
        else {
            BattleShipUtils.showAlert("Can't Shoot Yet", "Place all your ships first.");
        }
    }

    /**
     * Updates the UI to reflect the placement of a ship.
     *
     * @param gridPane the grid on which the ship is placed
     * @param row the row index of the starting position
     * @param col the column index of the starting position
     * @param shipSize the size of the ship
     * @param isVertical true if the ship is placed vertically, false if horizontally
     */
    private void updateShipUI(GridPane gridPane, int row, int col, int shipSize, boolean isVertical) {
        // loop through and place each cell
        for (int i = 0; i < shipSize; i++) {
            int placeRow = isVertical ? row + i : row;
            int placeCol = isVertical ? col : col + i;
            Label shipCell = new Label();
            shipCell.setStyle("-fx-border-color: black; -fx-background-color: gray;");
            shipCell.setPrefSize(40, 40);
            gridPane.add(shipCell, placeCol, placeRow);
        }
    }

    /**
     * Updates the UI to reflect the results of shots.
     *
     * @param gridpane the grid to update
     */
    private void updateShotUI(GridPane gridpane) {
        for (int i = 0; i < selfModel.getGridSize(); i++) {
            for (int j = 0; j < selfModel.getGridSize(); j++) {
                Label shotCell = new Label();
                shotCell.setPrefSize(40, 40);
                // if own grid
                if (gridpane == ownShipsGrid) {
                    // start your own timer after getting shot
                    timer = true;
                    startTimer();
                    // update if hit
                    if (selfModel.getEnemyHitsAt(i, j) == BattleShipModel.States.HIT) {
                        shotCell.setStyle("-fx-border-color: black; -fx-background-color: gray; -fx-text-fill: red; -fx-font-size: 20;");
                        shotCell.setText("X");
                        shotCell.setTextAlignment(TextAlignment.CENTER);
                        shotCell.setAlignment(Pos.CENTER); // Center the text horizontally and vertically
                        gridpane.add(shotCell, j, i);
                    } else if (selfModel.getEnemyHitsAt(i, j) == BattleShipModel.States.MISS) { // update if miss
                        shotCell.setStyle("-fx-border-color: black; -fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 20;");
                        shotCell.setText("O");
                        shotCell.setTextAlignment(TextAlignment.CENTER);
                        shotCell.setAlignment(Pos.CENTER); // Center the text horizontally and vertically
                        gridpane.add(shotCell, j, i);
                    }
                } else if (gridpane == enemyShipsGrid) { // if enemy grid
                    if (selfModel.getYourHitsAt(i, j) == BattleShipModel.States.HIT) { // update if hit
                        shotCell.setStyle("-fx-border-color: black; -fx-background-color: red;");
                        gridpane.add(shotCell, j, i);
                    } else if (selfModel.getYourHitsAt(i, j) == BattleShipModel.States.MISS) { // update if miss
                        shotCell.setStyle("-fx-border-color: black; -fx-background-color: white;");
                        gridpane.add(shotCell, j, i);
                    }
                }

            }
        }
    }

    /**
     * Helper for updating enemy shots.
     */
    public void updateEnemyShots() {
        updateShotUI(ownShipsGrid);
    }

    /**
     * Toggles the dark mode theme.
     */
    @FXML
    private void toggleDarkMode() {
        if (darkModeCheckBox.isSelected()) {
            scene.getStylesheets().add(getClass().getResource("/dark-mode.css").toExternalForm());
        } else {
            scene.getStylesheets().remove(getClass().getResource("/dark-mode.css").toExternalForm());
        }
    }

    /**
     * Exits the Application
     */
    @FXML
    private void exitApplication() {
        System.exit(0);
    }

    /**
     * Starts new game
     */
    @FXML
    public void newGame() {
        this.initialize();
    }

    /**
     * Sets the scene for the controller.
     *
     * @param scene the scene to set
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Sets the network peer for the controller.
     *
     * @param newPeer       the network peer to set
     */
    public void setNetwork(Connectable newPeer) {
        me = newPeer;
    }
}
