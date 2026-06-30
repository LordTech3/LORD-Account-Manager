/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * FXML Controller class
 *
 * @author amirhossein
 */
public class UserDashboardController implements Initializable {
    
    public static int userID;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void PasswordVault_Btn_call(ActionEvent event) throws IOException {
        AccountVaultController.userID = userID;
        App.setRoot("AccountVault");
        
    }
    
    @FXML
    private void AccountCategories_Btn_call(ActionEvent event) throws IOException {
        AccountCategoriesController.userID = userID;
        App.setRoot("AccountCategories");
        
    }
    
    @FXML
    private void ChangeUserDetails_Btn_call(ActionEvent event) throws IOException {
        LoginOrSignUpWindowController.setState("UpdateUser");
        App.setRoot("LoginOrSignUpWindow");
    }
    
    @FXML
    private void Logout_Btn_call(ActionEvent event) throws IOException {
        KeyManager.clearCurrentKey();
        
        App.setRoot("MainWindow");
    }
    
    @FXML
    private void deleteUser_Btn_call(ActionEvent event) throws IOException {
        Optional buttonPressed = Alerts.Alert(Alert.AlertType.CONFIRMATION,
                "You are about to permanently delete this user and all accounts linked to it.\n\nThis action cannot be undone. Do you want to continue?");
        if (buttonPressed.isPresent() && buttonPressed.get() == ButtonType.OK) {
            buttonPressed = Alerts.Alert(Alert.AlertType.CONFIRMATION,
                    "Are you absolutely sure you want to delete this user?");
            if (buttonPressed.isPresent() && buttonPressed.get() == ButtonType.OK) {
                String[] deletingColumnNames = new String[]{"UserID"};
                Object[] items = new Object[]{userID};
                PassManagerDB.deleteRecord("Users", deletingColumnNames, items);
                KeyManager.clearCurrentKey();
                App.setRoot("MainWindow");
            }
            
        }
    }
    
}
