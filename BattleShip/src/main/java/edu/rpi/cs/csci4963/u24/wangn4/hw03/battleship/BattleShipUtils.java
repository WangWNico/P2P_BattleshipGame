package edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/**
 * Utility class for the Battleship game. Provides methods to show alert and dialogue boxes.
 */
public class BattleShipUtils {
    private static BattleShipController gameController;

    // Constructor to set the controller
    public BattleShipUtils(BattleShipController gameController) {
        this.gameController = gameController;
    }

    /**
     * Displays an error alert with the specified title and content.
     *
     * @param title   the title of the alert dialog
     * @param content the content message of the alert dialog
     */
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content); // Set the content text
        alert.showAndWait(); // Show the alert and wait for user interaction
    }

    /**
     * Displays an information dialogue with the specified content.
     *
     * @param content the content message of the information dialogue
     */
    public static void showDialogue(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null); // No title
        alert.setHeaderText(null); // No header text
        alert.setContentText(content); // Set the content text
        alert.showAndWait(); // Show the alert and wait for user interaction
    }

    /**
     * Displays the win dialogue with a button that performs new game action
     */
    public static void showWinDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY); // Use utility style for a simple dialog
            alert.setTitle(null); // No title
            alert.setHeaderText(null); // No header text
            alert.setContentText("You Won!"); // Set the content text

            // Custom button with action
            ButtonType customButtonType = new ButtonType("New Game");
            alert.getButtonTypes().setAll(customButtonType, ButtonType.CLOSE);

            alert.showAndWait().ifPresent(response -> {
                if (response == customButtonType) {
                    gameController.newGame(); // Call the new game method on the controller
                }
            });
        });
    }

    /**
     * Displays the loss dialogue with a button that performs new game action
     */
    public static void showLossDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY); // Use utility style for a simple dialog
            alert.setTitle(null); // No title
            alert.setHeaderText(null); // No header text
            alert.setContentText("You Lost :("); // Set the content text

            // Custom button with action
            ButtonType customButtonType = new ButtonType("New Game");
            alert.getButtonTypes().setAll(customButtonType, ButtonType.CLOSE);

            alert.showAndWait().ifPresent(response -> {
                if (response == customButtonType) {
                    gameController.newGame(); // Call the new game method on the controller
                }
            });
        });
    }
}
