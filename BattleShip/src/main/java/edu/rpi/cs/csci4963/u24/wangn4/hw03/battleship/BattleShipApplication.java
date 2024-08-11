package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Battleship game.
 * Extends the JavaFX Application class to set up and launch the game interface.
 */
public class BattleShipApplication extends Application {

    /**
     * Starts the JavaFX application.
     * Loads the FXML layout, sets up the scene, and displays the primary stage.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if there is an error during loading the FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML layout for the network selection screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/rpi/cs/csci4963/u24/wangn4/hw03/battleship/BattleShip-netselect.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Apply ocean theme stylesheet
        scene.getStylesheets().add(getClass().getResource("/ocean-theme.css").toExternalForm());

        // Get controller and set the scene
        BattleShipNetSelectController controller = loader.getController();
        controller.setStage(primaryStage);

        // Set the title and scene for the primary stage, then show it
        primaryStage.setTitle("Battleship Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method for launching the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
