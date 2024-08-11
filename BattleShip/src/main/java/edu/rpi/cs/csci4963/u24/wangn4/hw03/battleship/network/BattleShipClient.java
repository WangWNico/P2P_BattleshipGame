package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * The BattleShipClient class implements the Connectable interface and provides
 * the client-side logic for the Battleship game network communication.
 */
public class BattleShipClient implements Connectable {

    /**
     * Constructs a BattleShipClient with the default port and specified server.
     *
     * @param server the server hostname or IP address to connect to.
     */
    public BattleShipClient(String server) {
        this(server, DEFAULT_PORT);
    }

    /**
     * Constructs a BattleShipClient with the specified server and port.
     *
     * @param server the server hostname or IP address to connect to.
     * @param port   the port number on the server to connect to.
     */
    public BattleShipClient(String server, int port) {
        this.log = Logger.getLogger("global");
        this.server = server;
        this.port = port;
    }

    /**
     * Establishes a connection to the server using the specified server and port.
     *
     * @throws IOException if an I/O error occurs when establishing the connection.
     */
    @Override
    public void connect() throws IOException {
        this.socket = new Socket(this.server, this.port);

        log.info(String.format("Connection to server %s established at port %d.\n", server, port));
        this.inStream = this.socket.getInputStream();
        this.outStream = this.socket.getOutputStream();
        this.in = new Scanner(this.inStream);
        this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /* autoFlush */);
    }

    /**
     * Sends a message to the connected server.
     *
     * @param message the message to be sent.
     */
    @Override
    public void send(String message) {
        this.out.println(message);
        log.info(String.format("Message %s sent.\n", message));
    }

    /**
     * Receives a message from the connected server.
     *
     * @return the received message.
     */
    @Override
    public String receive() {
        String message = this.in.nextLine();
        log.info(String.format("Message %s received.\n", message));
        return message;
    }

    /**
     * Closes the client socket and all associated streams.
     *
     * @throws IOException if an I/O error occurs when closing the connections.
     */
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    /**
     * Checks if there are any messages available to be read from the server.
     *
     * @return true if there are messages available, false otherwise.
     */
    @Override
    public boolean hasMessage() {
        try {
            return inStream.available() != 0;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Gets the port number on which the client is connected.
     *
     * @return the port number.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Gets the server hostname or IP address that the client is connected to.
     *
     * @return the server hostname or IP address.
     */
    public String getServer() {
        return this.server;
    }

    /**
     * Checks if the connection to the server is closed.
     *
     * @return true if the connection is closed, false otherwise.
     */
    public boolean isConnectionClosed() {
        return this.socket.isClosed();
    }

    public boolean isTurn() {
        return turn;
    }

    public void switchTurn() {
        turn = !turn;
    }

    /**
     * The default port number used for the client socket.
     */
    public static final int DEFAULT_PORT = 8189;

    private Socket socket;
    private String server;
    private int port;
    private InputStream inStream;
    private OutputStream outStream;
    private Scanner in;
    private PrintWriter out;
    private Logger log;

    public boolean turn = false;
}
