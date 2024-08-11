package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network;

import java.io.IOException;

/**
 * The Connectable interface defines the necessary methods for network communication
 * in the Battleship game.
 */
public interface Connectable {

    /**
     * Establishes a network connection.
     *
     * @throws IOException if an I/O error occurs when attempting to connect.
     */
    void connect() throws IOException;

    /**
     * Sends a message over the network.
     *
     * @param message the message to be sent.
     */
    void send(String message);

    /**
     * Receives a message from the network.
     *
     * @return the message received.
     */
    String receive();

    /**
     * Gets the port number used for the network connection.
     *
     * @return the port number.
     */
    int getPort();

    /**
     * Closes the network connection.
     *
     * @throws IOException if an I/O error occurs when attempting to close the connection.
     */
    void close() throws IOException;

    /**
     * Checks if there are any messages available to be read from the network.
     *
     * @return true if there are messages available, false otherwise.
     */
    boolean hasMessage();


    // Todo fix this parameter stuff
    /**
     * Determines if the player goes first in the game.
     *
     * @return true if the player goes first, false otherwise.
     */
    boolean isTurn();

    void switchTurn();


    /**
     * The default port number used for the network connection.
     */
    static Integer DEFAULT_PORT = 1732;
}
