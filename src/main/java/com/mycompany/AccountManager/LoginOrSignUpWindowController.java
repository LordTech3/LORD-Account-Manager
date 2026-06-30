package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author amirhossein
 */
public class LoginOrSignUpWindowController implements Initializable {

    @FXML
    private Button Save_Btn;
    @FXML
    private Label Title_Label;
    @FXML
    private GridPane AccountDetails_GridPane;
    @FXML
    private RowConstraints Row3;
    private static String state;
    @FXML
    private PasswordField repeatPass_PassField;
    @FXML
    private TextField repeatPass_TxtField;
    @FXML
    private PasswordField password_PassField;
    @FXML
    private TextField password_TxtField;
    @FXML
    private TextField UserName_TxtField;
    @FXML
    private Label repeatPassword_Label;
    @FXML
    private CheckBox showPassword_CheckBox;
    @FXML
    private CheckBox showRepeatedPassword_CheckBox;
    @FXML
    private VBox ShowPassword_Vbox;
    private Map<String, Users> hashMap;
    private static int numberOfTimes;
    @FXML
    private ProgressBar passwordStrength_ProgressBar;
    @FXML
    private Label passwordStrength_Label;
    private String[] usersTableColumnNames = {
        "UserID", "UserName", "HashedPassword", "EncryptionSalt"};
    private Object[] items;
    private static int userID;
    private Users user;
    private String userName;
    private byte[] encryptionSalt;

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
        if (state.equalsIgnoreCase("Login")) {
            password_TxtField.setDisable(true);
            Title_Label.setText("Login Window");
            Row3.setMinHeight(0);
            Row3.setPrefHeight(0);
            Row3.setMaxHeight(0);
            Save_Btn.setText("Login");
            showPassword_CheckBox.setDisable(false);
            passwordStrength_ProgressBar.setVisible(false);

        } else {
            if (state.equalsIgnoreCase("signUp")) {
                Title_Label.setText("SignUp Window");
                Save_Btn.setText("SignUp");
            } else {
                Title_Label.setText("Change Username Password");
                Save_Btn.setText("Save changes");
            }
            repeatPassword_Label.setVisible(true);
            repeatPass_PassField.setDisable(false);
            repeatPass_PassField.setVisible(true);
            repeatPass_TxtField.setDisable(true);
            repeatPass_TxtField.setVisible(false);

            showRepeatedPassword_CheckBox.setDisable(false);
            showRepeatedPassword_CheckBox.setVisible(true);
            passwordStrength_ProgressBar.setVisible(true);
            password_PassField.textProperty().addListener((obs, oldPass, newPass) -> {
                if (!password_PassField.isDisabled()) {
                    int score = PasswordSecurityUtils.passStrength(newPass);
                    updateProgressBar(score, passwordStrength_ProgressBar, passwordStrength_Label);
                }
            });
            password_TxtField.textProperty().addListener((obs, oldPass, newPass) -> {
                if (!password_TxtField.isDisabled()) {
                    int score = PasswordSecurityUtils.passStrength(newPass);
                    updateProgressBar(score, passwordStrength_ProgressBar, passwordStrength_Label);
                }

            });

        }
    }

    @FXML
    private void Back_Btn_call(ActionEvent event) throws IOException {
        if (state.equalsIgnoreCase("UpdateUser")) {
            App.setRoot("UserDashboard");
        } else {
            App.setRoot("MainWindow");
        }
        numberOfTimes = 0;
    }

    @FXML
    private void Clear_Btn_Call(ActionEvent event) {
        repeatPass_PassField.setText("");
        repeatPass_TxtField.setText("");
        password_TxtField.setText("");
        password_PassField.setText("");
        UserName_TxtField.setText("");
    }

    @FXML
    private void Save_Btn_call(ActionEvent event) throws IOException {

        userName = UserName_TxtField.getText();

        if (userName.matches("[A-Za-z0-9_\\-@.]{3,20}")) {

            String password;

            if (showPassword_CheckBox.isSelected()) {
                password = password_TxtField.getText();
            } else {
                password = password_PassField.getText();
            }

            if (password.isEmpty()) {
                Alerts.Alert(Alert.AlertType.ERROR, "Password cannot be empty");
                return;
            }

            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            char[] passwordChars;
            passwordChars = password.toCharArray();
            try {

                password = null;
                hashMap = PassManagerDB.getUsersMap();
                user = hashMap.get(userName);
                if (state.equalsIgnoreCase("Login")) {

                    if (user == null) {
                        Alerts.Alert(Alert.AlertType.ERROR,
                                "Username is incorrect or does not exist");
                        return;
                    }

                    encryptionSalt = user.getEncryptionSalt();

                    login(passwordBytes, passwordChars);

                } else {

                    if (user == null || user.getUserID() == userID) {
                        encryptionSalt = PasswordSecurityUtils.generateSalt();

                        signUpOrUpdate(passwordChars);
                    } else {
                        Alerts.Alert(Alert.AlertType.ERROR,
                                "UserName already exists in the program");
                    }
                }

            } catch (Exception e) {
                Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            } finally {
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(passwordBytes, (byte) 0);
            }
        } else {
            Alerts.Alert(Alert.AlertType.ERROR, "UserName is invalid");
        }
    }

    private void login(byte[] passwordBytes, char[] passwordChars) throws IOException {

        if (user != null) {

            if (PasswordSecurityUtils.verifyHash(user.getHashedPassword(), passwordBytes)) {

                userID = user.getUserID();
                UserDashboardController.userID = userID;
                KeyManager.setCurrentKey(PasswordSecurityUtils.generateEncryptionKey(passwordChars, encryptionSalt));
                App.setRoot("UserDashboard");

            } else {
                Alerts.Alert(Alert.AlertType.ERROR, "Password is incorrect");
            }

        } else {
            Alerts.Alert(Alert.AlertType.ERROR,
                    "Username is incorrect or does not exist in the database");
        }

    }

    private void signUpOrUpdate(char[] passwordChars) throws IOException {

        String repeatedPassword;
        char[] repeatedPasswordChars;
        if (showRepeatedPassword_CheckBox.isSelected()) {
            repeatedPassword = repeatPass_TxtField.getText();
        } else {
            repeatedPassword = repeatPass_PassField.getText();
        }
        repeatedPasswordChars = repeatedPassword.toCharArray();
        try {
            repeatedPassword = null;
            if (Arrays.equals(passwordChars, repeatedPasswordChars)) {
                double strength = passwordStrength_ProgressBar.getProgress();

                if (strength <= 0.2) {

                    Alerts.Alert(Alert.AlertType.ERROR, "Password is too weak");

                } else if (strength <= 0.4) {

                    Optional buttonPressed = Alerts.Alert(Alert.AlertType.CONFIRMATION, "Your password is not very high quality, are you sure you want to continue?");

                    if (buttonPressed.isPresent() && buttonPressed.get() == ButtonType.OK) {

                        if (state.equalsIgnoreCase("SignUp")) {
                            signUp(passwordChars);
                        } else {
                            updateUser(passwordChars);
                        }

                    }
                } else {

                    if (state.equalsIgnoreCase("SignUp")) {
                        signUp(passwordChars);
                    } else {
                        updateUser(passwordChars);
                    }

                }
            } else {
                Alerts.Alert(Alert.AlertType.ERROR,
                        "Password and the repeated password do not match");
            }
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
        } finally {

            Arrays.fill(passwordChars, '\0');
            Arrays.fill(repeatedPasswordChars, '\0');
        }
    }

    private void signUp(char[] passwordChars) throws IOException {

        items = new Object[]{
            0,
            userName,
            PasswordSecurityUtils.createHash(passwordChars), encryptionSalt};

        userID = PassManagerDB.insertRecord(
                "Users",
                usersTableColumnNames,
                items);
        UserDashboardController.userID = userID;

        Alerts.Alert(Alert.AlertType.INFORMATION,
                "User has been added");

        KeyManager.setCurrentKey(
                PasswordSecurityUtils.generateEncryptionKey(
                        passwordChars,
                        encryptionSalt));
        password_PassField.clear();
password_TxtField.clear();
 repeatPass_PassField.clear();
repeatPass_TxtField.clear();
        App.setRoot("UserDashboard");

    }

    private void updateUser(char[] passwordChars) throws IOException, SQLException {
        Connection connect = PassManagerDB.getConnection();
        connect.setAutoCommit(false);
        items = new Object[]{userID, userName, PasswordSecurityUtils.createHash(passwordChars), encryptionSalt};
        try {
            boolean success = true;
         success =   PassManagerDB.updateRecord("Users", usersTableColumnNames, items);
                 if (!success) {
                    Exception e = new Exception("Update failed");
                    throw e;
                }
            
            ObservableList<AccountsOfUsers> accountsList = PassManagerDB.getAccountsOfUsers(userID);
            byte[] newKey = PasswordSecurityUtils.generateEncryptionKey(passwordChars, encryptionSalt);
            for (AccountsOfUsers account : accountsList) {
                String userName = PasswordSecurityUtils.decryptToString(account.getEncryptedUserName(), account.getUserNameNonce(), KeyManager.getCurrentKey());
                byte[] newUserNameNonce = PasswordSecurityUtils.generateNonce();
                account.setUserNameNonce(newUserNameNonce);
                account.setEncryptedUserName(PasswordSecurityUtils.encrypt(userName, newUserNameNonce, newKey));
                userName = null;
                char[] password = PasswordSecurityUtils.decryptToChars(account.getEncryptedPassword(), account.getPasswordNonce(), KeyManager.getCurrentKey());
                byte[] passwordNonce = PasswordSecurityUtils.generateNonce();

                account.setPasswordNonce(passwordNonce);
                account.setEncryptedPassword(PasswordSecurityUtils.encrypt(password, passwordNonce, newKey));
                if (account.getEncryptedRecoveryPhrases() != null) {
                    byte[] recoveryPhrasesNonce = PasswordSecurityUtils.generateNonce();
                    String recoveryPhrases = PasswordSecurityUtils.decryptToString(account.getEncryptedRecoveryPhrases(), account.getRecoveryPhrasesNonce(), KeyManager.getCurrentKey());
                    account.setRecoveryPhrasesNonce(recoveryPhrasesNonce);
                    String encryptedRecoveryPhrases = PasswordSecurityUtils.encrypt(recoveryPhrases, account.getRecoveryPhrasesNonce(), newKey);
                    account.setEncryptedRecoveryPhrases(encryptedRecoveryPhrases);
                }

                Arrays.fill(password, '\0');
                items = new Object[]{
                    account.getAccountID(),
                    userID,
                    account.getAccountCategory().getAccountCategoryID(),
                    account.getSiteOrAppName(),
                    account.getDescription(),
                    account.getEncryptedUserName(),
                    account.getEncryptedPassword(),
                    account.getEncryptedRecoveryPhrases(),
                    account.getUserNameNonce(),
                    account.getPasswordNonce(),
                    account.getRecoveryPhrasesNonce()
                };
                success = PassManagerDB.updateRecord("AccountVault", accountVaultTableColumns, items);
                if (!success) {
                    Exception e = new Exception("Updating one of the accounts of this user failed");
                    throw e;
                }
            }
                connect.commit();
                Alerts.Alert(Alert.AlertType.INFORMATION, "User information has been updated");
                KeyManager.clearCurrentKey();
                KeyManager.setCurrentKey(newKey);
                App.setRoot("MainWindow");
            
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            connect.rollback();
        } finally {
            connect.setAutoCommit(true);
        }

    }

    public static void setState(String state) {
        LoginOrSignUpWindowController.state = state;
    }

    @FXML
    private void showPassword_CheckBox_call(ActionEvent event) {

        if (showPassword_CheckBox.isSelected()) {

            String password = password_PassField.getText();

            password_PassField.setVisible(false);
            password_PassField.setDisable(true);

            password_TxtField.setVisible(true);
            password_TxtField.setDisable(false);

            password_TxtField.setText(password);

        } else {
            String password = password_TxtField.getText();
            password_TxtField.setVisible(false);
            password_TxtField.setDisable(true);

            password_PassField.setVisible(true);
            password_PassField.setDisable(false);

            password_PassField.setText(password);
        }
    }

    @FXML
    private void showRepeatedPassword_CheckBox_calll(ActionEvent event) {

        if (showRepeatedPassword_CheckBox.isSelected()) {

            String password = repeatPass_PassField.getText();

            repeatPass_PassField.setVisible(false);
            repeatPass_PassField.setDisable(true);

            repeatPass_TxtField.setVisible(true);

            repeatPass_TxtField.setDisable(false);

            repeatPass_TxtField.setText(password);

        } else {

            String password = repeatPass_TxtField.getText();

            repeatPass_TxtField.setVisible(false);
            repeatPass_TxtField.setDisable(true);

            repeatPass_PassField.setVisible(true);

            repeatPass_PassField.setDisable(false);

            repeatPass_PassField.setText(password);

        }
    }

    @FXML
    private void password_TxtField_clicked(MouseEvent event) {
        if (numberOfTimes == 0 && state.equalsIgnoreCase("SignUp")) {
            Alerts.Alert(Alert.AlertType.INFORMATION, "This password is the master password protecting all your passwords, ENSURE to choose something complex and remember it");
            numberOfTimes++;
        }
    }

    @FXML
    private void password_PassField_clicked(MouseEvent event) {
        if (numberOfTimes == 0 && state.equalsIgnoreCase("SignUp")) {
            Alerts.Alert(Alert.AlertType.INFORMATION, "This password is the master password protecting all your passwords, ENSURE to choose something complex and remember it");
            numberOfTimes++;
        }
    }

    public static void updateProgressBar(int score, ProgressBar passQuality_ProgressBar, Label passQuality_Label) {

        passQuality_ProgressBar.setProgress(score / 4.0);

        passQuality_ProgressBar.getStyleClass().removeAll(
                "strength-very-weak",
                "strength-weak",
                "strength-medium",
                "strength-strong",
                "strength-very-strong"
        );

        if (score == 0) {
            passQuality_ProgressBar.getStyleClass().add("strength-very-weak");
            passQuality_Label.setText("Password Strength: Very Weak");
            passQuality_ProgressBar.setProgress(0.2);
        } else if (score == 1) {
            passQuality_ProgressBar.getStyleClass().add("strength-weak");
            passQuality_Label.setText("Password Strength: Weak ");
            passQuality_ProgressBar.setProgress(0.4);
        } else if (score == 2) {
            passQuality_ProgressBar.getStyleClass().add("strength-medium");
            passQuality_Label.setText("Password Strength: Medium");
            passQuality_ProgressBar.setProgress(0.6);
        } else if (score == 3) {
            passQuality_ProgressBar.getStyleClass().add("strength-strong");
            passQuality_Label.setText("Password Strength: Strong");
            passQuality_ProgressBar.setProgress(0.8);
        } else {
            passQuality_ProgressBar.getStyleClass().add("strength-very-strong");
            passQuality_Label.setText("Password Strength: Very Strong");
            passQuality_ProgressBar.setProgress(1.0);
        }
    }

}
