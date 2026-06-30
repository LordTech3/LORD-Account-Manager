package com.mycompany.AccountManager;

/**
 * This class is used for generating and displaying Alerts in order to provide
 * feedback to the user, such as error, information and confirmation messages,
 * improving user interaction within the application.
 */
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 *
 * @author mgg610
 */
public class Alerts {

    /**
     * Private constructor is used to ensure the user cannot make an object of
     * this class.
     */
    private Alerts() {

    }

    /**
     * Displays an alert of the specified type with the given message.
     *
     * @param alertType the type of the alert that is going to be displayed
     * @param message the text that is shown in the alert
     * @return an Optional that contains the ButtonType selected by the user
     */
    public static Optional<ButtonType> Alert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Alerts.class.getResource("/styles/AlertsStyling.css").toExternalForm());
        alert.getDialogPane().setPrefSize(450, 250);
        return alert.showAndWait();
        

    }
}
