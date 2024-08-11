package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;

import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.BattleShipClient;
import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.BattleShipServer;
import edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.Connectable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the network selection screen in the Battleship game.
 * Handles user input for hosting or joining a game and manages the transition to the game screen.
 */
public class BattleShipNetSelectController {

    @FXML
    private TextField portInput;

    @FXML
    private TextField serverInput;

    @FXML
    private Button launchButton;

    @FXML
    private Text portText;

    @FXML
    private Text serverText;

    /**
     * Enum to represent the current mode of the controller.
     * INIT - Initial state
     * HOST - Host game state
     * JOIN - Join game state
     */
    enum Mode {INIT, HOST, JOIN}

    private Stage stage;
    private String server;
    private Integer port;
    private Mode mode;

    /**
     * Initializes the controller.
     * Sets default values and hides input fields initially.
     */
    @FXML
    private void initialize() {
        server = null;
        port = Connectable.DEFAULT_PORT;
        portInput.setVisible(false);
        portInput.setText("" + Connectable.DEFAULT_PORT);
        portText.setVisible(false);
        serverInput.setVisible(false);
        serverInput.setText("localhost");
        serverText.setVisible(false);
        launchButton.setVisible(false);
        mode = Mode.INIT;
    }

    /**
     * Handles the "Host" button click.
     * Makes the port input field visible and sets the mode to HOST.
     */
    @FXML
    private void host() {
        portInput.setVisible(true);
        portText.setVisible(true);
        serverInput.setVisible(false);
        serverText.setVisible(false);
        mode = Mode.HOST;
        launchButton.setVisible(port != null);
    }

    /**
     * Handles the "Join" button click.
     * Makes the port and server input fields visible and sets the mode to JOIN.
     */
    @FXML
    private void join() {
        portInput.setVisible(true);
        portText.setVisible(true);
        serverInput.setVisible(true);
        serverText.setVisible(true);
        mode = Mode.JOIN;
        launchButton.setVisible(true);
    }

    /**
     * Sets the port number based on user input.
     * Shows an alert if the input is not a valid integer.
     */
    @FXML
    private void setPort() {
        try {
            port = Integer.parseInt(portInput.getText());
        } catch (NumberFormatException e) {
            BattleShipUtils.showAlert("Invalid Number", "Port must be an integer");
            port = null;
            launchButton.setVisible(false);
            return;
        }
        if (mode == Mode.HOST) {
            launchButton.setVisible(true);
        } else {
            launchButton.setVisible(server != null);
        }
    }

    /**
     * Sets the server address based on user input.
     */
    @FXML
    private void setServer() {
        server = serverInput.getText();
        launchButton.setVisible(port != null);
    }

    /**
     * Launches the game by connecting to the server or starting a server.
     * Transitions to the game view if successful.
     */
    @FXML
    private void launchGame() {
        Connectable network;
        try {
            if (mode == Mode.HOST) {
                network = new BattleShipServer(port);
                BattleShipUtils.showDialogue("Waiting for a partner...");
            } else {
                network = new BattleShipClient(server, port);
            }
            network.connect();
        } catch (IOException e) {
            BattleShipUtils.showAlert("Connection Failure", "Invalid port or server: " + e.getMessage());
            return;
        }
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/edu/rpi/cs/csci4963/u24/wangn4/hw03/battleship/BattleShip-view.fxml"));
            root = loader.load();
        } catch (IOException e) {
            return;
        }
        Scene scene = new Scene(root);

        // Apply ocean theme stylesheet
        scene.getStylesheets().add(getClass().getResource("/ocean-theme.css").toExternalForm());

        // Get controller and set the scene
        BattleShipController controller = loader.getController();
        controller.setScene(scene);
        controller.setNetwork(network);

        stage.setTitle("Battleship Game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the stage for this controller.
     *
     * @param newStage the new stage to set
     */
    public void setStage(Stage newStage) {
        stage = newStage;
    }
}
