package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network;

import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.BattleShipController;
import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.BattleShipModel;
import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.BattleShipUtils;
import javafx.application.Platform;

/**
 * The BattleShipProtocol class handles the communication protocol
 * between the game server and clients for Battleship game sessions.
 */
public class BattleShipProtocol {
    private BattleShipModel game; // The Battleship game model instance
    private Connectable me;
    private BattleShipController controller;
    private BattleShipUtils utils;

    /**
     * Constructs a BattleShipProtocol with a specified BattleShipModel.
     *
     * @param newGame the initialized BattleShipModel instance for the protocol
     * @param me connectable storing itself
     * @throws IllegalStateException if the game is not properly initialized
     */
    public BattleShipProtocol(BattleShipModel newGame, Connectable me, BattleShipController controller) {
        if (!newGame.placed()) {
            throw new IllegalStateException("Game must have all ships placed and no shots fired!");
        }
        game = newGame;
        this.me = me;
        this.controller = controller;
    }

    /**
     * Parses and processes commands received from clients.
     *
     * @param command the command received from the client
     * @return the response to the client after processing the command
     * @throws IllegalArgumentException if the command format is invalid
     */
    public String process(String command) {
        String[] args = parseCommand(command);
        String response = "";
        int row, col;

        switch (args[0]) {
            case "shot":
                if (args.length != 3) {
                    throw new IllegalArgumentException("Malformed packet! Should be \"shot <row> <col>\"");
                }
                row = Integer.parseInt(args[1]);
                col = Integer.parseInt(args[2]);
                BattleShipModel.ShotResult shotResult = game.getShot(row, col);
                Platform.runLater(controller::updateEnemyShots);
                response = "result " + row + " " + col + (shotResult.shotHit() ? " T " : " F ") + shotResult.shipSunk();
                // detect loss, if loss then switch out to loss dialog
                if (game.lose()) {
                    utils = new BattleShipUtils(controller);
                    utils.showLossDialog();
                    game = new BattleShipModel(game.getGridSize());
                    System.out.println(game.placed());
                }
                this.me.send(response);
                this.me.switchTurn();
                break;
            case "result":
                if (args.length != 5) {
                    throw new IllegalArgumentException("Malformed packet! Should be \"result <row> <col> <hit> <sunk>\"");
                }
                row = Integer.parseInt(args[1]);
                col = Integer.parseInt(args[2]);
                boolean hit = (args[3].equals("T"));

                game.takeShot(row, col, hit);
                String sunk = args[4];
                if (!sunk.equals("none")) {
                    if (BattleShipModel.SHIPS.containsKey(sunk)) {
                        game.sinkShip(sunk);
                        BattleShipUtils.showDialogue("You sunk the opponent's " + sunk);
                        // if win
                        if(game.win()) {
                            utils = new BattleShipUtils(controller);
                            utils.showWinDialog();
                            game = new BattleShipModel(game.getGridSize());
                            System.out.println(game.placed());
                        }
                    } else {
                        throw new IllegalArgumentException("Malformed packet! Illegal ship value!");
                    }
                }
                break;
            default:
        }
        return response;
    }

    /**
     * Parses a command into an array of strings.
     *
     * @param command the command string to parse
     * @return an array of strings representing the parsed command
     */
    private String[] parseCommand(String command) {
        return command.split("\\s");
    }
}
