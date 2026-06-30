
package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author amirhossein
 */
public class MainWindowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PassManagerDB.connect();
        PassManagerDB.createTables();
    }

    @FXML
    private void CreateAccount_Btn_call(ActionEvent event) throws IOException {
          LoginOrSignUpWindowController.setState("SignUp");
        App.setRoot("LoginOrSignUpWindow");
    }

    @FXML
    private void Exit_Btn_call(ActionEvent event) {
        PassManagerDB.disconnect();
        Platform.exit();
    }

    @FXML
    private void Login_Btn_call(ActionEvent event) throws IOException {
        LoginOrSignUpWindowController.setState("Login");
        App.setRoot("LoginOrSignUpWindow");
    }

}
