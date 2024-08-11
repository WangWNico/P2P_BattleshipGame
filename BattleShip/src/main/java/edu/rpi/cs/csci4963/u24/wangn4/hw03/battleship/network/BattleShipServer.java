package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * The BattleShipServer class implements the Connectable interface and provides
 * the server-side logic for the Battleship game network communication.
 */
public class BattleShipServer implements Connectable {

    /**
     * Constructs a BattleShipServer with the default port.
     *
     * @throws IOException if an I/O error occurs when creating the server socket.
     */
    public BattleShipServer() throws IOException {
        this(DEFAULT_PORT);
    }

    /**
     * Constructs a BattleShipServer with the specified port.
     *
     * @param port the port number to bind the server socket.
     * @throws IOException if an I/O error occurs when creating the server socket.
     */
    public BattleShipServer(int port) throws IOException {
        this.log = Logger.getLogger("global");

        this.port = port;
        this.servSocket = new ServerSocket(this.port);
        log.info(String.format("Server socket was created on port %d.\n", port));
    }

    /**
     * Accepts an incoming connection from a client and initializes input and output streams.
     *
     * @throws IOException if an I/O error occurs when accepting a connection or initializing streams.
     */
    @Override
    public void connect() throws IOException {
        this.socket = this.servSocket.accept();
        log.info(String.format("Incoming connection from a client at %s accepted.\n", this.socket.getRemoteSocketAddress().toString()));
        this.inStream = this.socket.getInputStream();
        this.outStream = this.socket.getOutputStream();
        this.in = new Scanner(this.inStream);
        this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /* autoFlush */);
    }

    /**
     * Sends a message to the connected client.
     *
     * @param message the message to be sent.
     */
    @Override
    public void send(String message) {
        this.out.println(message);
        log.info(String.format("Message %s sent.\n", message));
    }

    /**
     * Receives a message from the connected client.
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
     * Gets the port number on which the server socket is bound.
     *
     * @return the port number.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Closes the server socket and all associated resources.
     *
     * @throws IOException if an I/O error occurs when closing the connections.
     */
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        servSocket.close();
    }

    /**
     * Checks if there are any messages available to be read from the client.
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

    public boolean isTurn() {
        return turn;
    }

    public void switchTurn() {
        turn = !turn;
    }

    /**
     * The default port number used for the server socket.
     */
    public static final int DEFAULT_PORT = 8189;

    private int port;
    private Socket socket;
    private ServerSocket servSocket;
    private InputStream inStream;
    private OutputStream outStream;
    private Scanner in;
    private PrintWriter out;
    private Logger log;

    public boolean turn = true;

}
