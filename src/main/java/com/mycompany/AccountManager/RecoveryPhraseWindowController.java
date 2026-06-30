/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * FXML Controller class
 *
 * @author amirhossein
 */
public class RecoveryPhraseWindowController implements Initializable {

    @FXML
    private ComboBox<Integer> numberOfPhrases_ComboBox;
    @FXML
    private GridPane recoveryPhrases_GridPane;
    private ObservableList<Integer> numberOfPhrases = FXCollections.observableArrayList();
    private Map<Integer, TextField> txtFields = new HashMap<Integer, TextField>();
    private Map<Integer, HBox> hBoxes;
    public static AccountsOfUsers account;
    private String[] accountVaultTableColumns = {"AccountID",
        "UserID",
        "AccountCategoryID",
        "SiteAppName",
        "AccountDescription",
        "EncryptedSiteUserName",
        "EncryptedPassword",
        "EncryptedRecoveryPhrases",
        "UserNameNonce",
        "PasswordNonce",
        "EncryptedRecoveryPhrasesNonce"};

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hBoxes = new HashMap<Integer, HBox>();
        recoveryPhrases_GridPane.getChildren().clear();
        for (int i = 1; i <= 16; i++) {
            numberOfPhrases.add(i);
        }
        numberOfPhrases_ComboBox.setItems(numberOfPhrases);
        numberOfPhrases_ComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldNumber, newNumber) -> {
            if (newNumber != null) {
                if (oldNumber == null) {
                    oldNumber = 0;
                }
                initializeGridPane();
                initializeRecoveryPhrases(newNumber.intValue(), oldNumber.intValue());

            }
        });

        String encryptedCombinedRecoveryPhrases = account.getEncryptedRecoveryPhrases();

        if (encryptedCombinedRecoveryPhrases != null) {
            int start = 0;
            List<String> recoveryPhrases = new ArrayList<String>();
            String decryptedCombinedRecoveryPhrases;
            decryptedCombinedRecoveryPhrases = (PasswordSecurityUtils.decryptToString(encryptedCombinedRecoveryPhrases, account.getRecoveryPhrasesNonce(), KeyManager.getCurrentKey()));
            for (int i = 0; i < decryptedCombinedRecoveryPhrases.length(); i++) {
                if (decryptedCombinedRecoveryPhrases.charAt(i) == ' ') {
                    recoveryPhrases.add(decryptedCombinedRecoveryPhrases.substring(start, i));
                    start = i + 1;
                }
            }
            recoveryPhrases.add(decryptedCombinedRecoveryPhrases.substring(start, decryptedCombinedRecoveryPhrases.length()));
            numberOfPhrases_ComboBox.setValue(recoveryPhrases.size());
            for (int i = 0; i < txtFields.size(); i++) {
                txtFields.get(i + 1).setText(recoveryPhrases.get(i));
            }
        }
    }

    private void initializeGridPane() {
        int numberOfPhrases = numberOfPhrases_ComboBox.getValue().intValue();
        int numberOfRows = (numberOfPhrases / 2) + (numberOfPhrases % 2);

        recoveryPhrases_GridPane.getRowConstraints().clear();

        for (int i = 0; i < numberOfRows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            recoveryPhrases_GridPane.getRowConstraints().add(rc);
        }

    }

    private void initializeRecoveryPhrases(int newNumberOfPhrases, int oldNumberOfPhrases) {

        int rowNumber;
        int columnNumber;

        if (newNumberOfPhrases > oldNumberOfPhrases) {

            for (int j = oldNumberOfPhrases + 1; j <= newNumberOfPhrases; j++) {
                HBox row = new HBox();
                row.setSpacing(10);
                rowNumber = (j / 2) + (j % 2) - 1;
                columnNumber = ((j - 1) % 2);
                hBoxes.put(j, row);

                String labelText;
                if (j >= 10) {
                    labelText = " " + String.valueOf(j);
                } else {
                    labelText = " " + String.valueOf(j) + "  ";
                }

                Label label = new Label(labelText);

                TextField txtField = new TextField();
                HBox.setHgrow(txtField, Priority.ALWAYS);
                txtField.setMaxWidth(Double.MAX_VALUE);
                row.setMaxHeight(Double.MAX_VALUE);

                row.setMaxWidth(Double.MAX_VALUE);

                txtFields.put(j, txtField);

                row.getChildren().addAll(label, txtField);

                GridPane.setHgrow(row, Priority.ALWAYS);
                GridPane.setVgrow(row, Priority.ALWAYS);

                recoveryPhrases_GridPane.add(row, columnNumber, rowNumber);
            }

        } else {

            for (; oldNumberOfPhrases > newNumberOfPhrases; oldNumberOfPhrases--) {
                hBoxes.get(oldNumberOfPhrases).getChildren().clear();
                recoveryPhrases_GridPane.getChildren().remove(hBoxes.get(oldNumberOfPhrases));
                hBoxes.remove(oldNumberOfPhrases);
                txtFields.remove(oldNumberOfPhrases);
            }
        }
    }

    @FXML
    private void back_Btn_call(ActionEvent event) throws IOException {
        App.setRoot("AccountVault");
    }

    @FXML
    private void clear_Btn_call(ActionEvent event) {

        recoveryPhrases_GridPane.getChildren().clear();
        numberOfPhrases_ComboBox.setValue(null);
        txtFields.clear();
        hBoxes.clear();
    }

    @FXML
    private void save_Btn_call(ActionEvent event) throws IOException {
        if (txtFields.size() == 0) {
            account.setRecoveryPhrasesNonce(null);
            account.setEncryptedRecoveryPhrases(null);

            Object[] items = new Object[]{
                account.getAccountID(),
                account.getUserID(),
                account.getAccountCategory().getAccountCategoryID(),
                account.getSiteOrAppName(),
                account.getDescription(),
                account.getEncryptedUserName(),
                account.getEncryptedPassword(),
                account.getEncryptedRecoveryPhrases(),
                account.getUserNameNonce(),
                account.getPasswordNonce(),
                account.getRecoveryPhrasesNonce()};
            if (PassManagerDB.updateRecord("AccountVault", accountVaultTableColumns, items)) {
                Alerts.Alert(Alert.AlertType.INFORMATION, "Account has been updated");
                App.setRoot("AccountVault");
            }
        } else {
            if (checkRecoveryPhrases()) {
                StringBuilder combinedRecoveryPhrases = new StringBuilder();
                for (int i = 0; i < txtFields.size(); i++) {
                    combinedRecoveryPhrases.append(txtFields.get(i + 1).getText());
                    if (i < txtFields.size() - 1) {
                        combinedRecoveryPhrases.append(" ");
                    }
                }
                byte[] nonce = PasswordSecurityUtils.generateNonce();
                String encryptedCombinedRecoveryPhrases = PasswordSecurityUtils.encrypt(combinedRecoveryPhrases.toString(), nonce, KeyManager.getCurrentKey());
                for (int i = 0; i < combinedRecoveryPhrases.length(); i++) {
                    combinedRecoveryPhrases.setCharAt(i, '\0');
                }
                account.setRecoveryPhrasesNonce(nonce);
                account.setEncryptedRecoveryPhrases(encryptedCombinedRecoveryPhrases);
                Object[] items = new Object[]{
                    account.getAccountID(),
                    account.getUserID(),
                    account.getAccountCategory().getAccountCategoryID(),
                    account.getSiteOrAppName(),
                    account.getDescription(),
                    account.getEncryptedUserName(),
                    account.getEncryptedPassword(),
                    account.getEncryptedRecoveryPhrases(),
                    account.getUserNameNonce(),
                    account.getPasswordNonce(),
                    account.getRecoveryPhrasesNonce()};
                if (PassManagerDB.updateRecord("AccountVault", accountVaultTableColumns, items)) {
                    Alerts.Alert(Alert.AlertType.INFORMATION, "Account has been updated");
                    App.setRoot("AccountVault");
                }

            }
        }
    }

    private boolean checkRecoveryPhrases() {

        for (int i = 1; i <= txtFields.size(); i++) {

            String phrase = txtFields.get(i).getText().trim();

            if (phrase.isEmpty()) {
                Alerts.Alert(Alert.AlertType.ERROR, "Phrase number " + i + " is empty");
                return false;
            }

            if (phrase.contains(" ")) {
                Alerts.Alert(Alert.AlertType.ERROR, "Phrase number " + i + " has spaces between its characters");
                return false;
            }
        }

        return true;
    }

}
