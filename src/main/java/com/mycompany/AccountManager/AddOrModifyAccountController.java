package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddOrModifyAccountController implements Initializable {

    @FXML
    private CheckBox AddCategory_CheckBox;
    @FXML
    private TextField password_TxtField;
    @FXML
    private TextArea Description_TxtArea;

    @FXML
    private TextField NewCategory_TxtField;

    @FXML
    private CheckBox ShowPassword_checkBox;

    @FXML
    private ComboBox<AccountCategories> accountCategory_Combo;

    @FXML
    private PasswordField password_PassField;

    @FXML
    private TextField userName_TxtField;

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
    private String accountCategoriesTableColumnNames[] = {
        "AccountCategoryID", "UserID", "AccountCategoryName", "AccountCategoryDescription"
    };
    private Object[] items;

    @FXML
    private TextField SiteName_TxtField;

    @FXML
    private ProgressBar passQuality_ProgressBar;
    @FXML
    private Label passQuality_Label;
    public static int userID;
    public static AccountsOfUsers account;
    private String userName;
    private String password;
    private ObservableList<AccountCategories> categoriesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        password_PassField.textProperty().addListener((obs, oldPass, newPass) -> {
            if (!password_PassField.isDisabled()) {
                int score = PasswordSecurityUtils.passStrength(newPass);
                updateProgressBar(score, passQuality_ProgressBar, passQuality_Label);
            }
        });
        password_TxtField.textProperty().addListener((obs, oldPass, newPass) -> {
            int score = PasswordSecurityUtils.passStrength(newPass);
            updateProgressBar(score, passQuality_ProgressBar, passQuality_Label);

        });
        categoriesList = PassManagerDB.getAccountCategories(userID);
        initializeAccountCategoriesComboBox();

        if (account != null) {

            userName = PasswordSecurityUtils.decryptToString(account.getEncryptedUserName(), account.getUserNameNonce(), KeyManager.getCurrentKey());

            userName_TxtField.setText(userName);
            userName = null;
            password = PasswordSecurityUtils.decryptToString(account.getEncryptedPassword(), account.getPasswordNonce(), KeyManager.getCurrentKey());

            password_PassField.setText(password);
            password = null;

            SiteName_TxtField.setText(account.getSiteOrAppName());

            Description_TxtArea.setText(account.getDescription());
        }

    }

    @FXML
    private void Back_call(ActionEvent event) throws IOException {
        account = null;
        App.setRoot("AccountVault");

    }

    @FXML
    private void Clear_Btn(ActionEvent event) {
        Description_TxtArea.setText("");

        NewCategory_TxtField.setText("");

        accountCategory_Combo.setValue(null);
        SiteName_TxtField.setText("");
        password_PassField.setText("");
        password_TxtField.setText("");
        userName_TxtField.setText("");
        AddCategory_CheckBox.setSelected(false);
        ShowPassword_checkBox.setSelected(false);
    }

    @FXML
    private void Save_Btn(ActionEvent event) throws IOException {
        if (checkInput()) {
            double passQuality = passQuality_ProgressBar.getProgress();
            if (passQuality <= 0.2) {
                Alerts.Alert(Alert.AlertType.ERROR, "Password is too weak");

            } else if (passQuality <= 0.4) {

                Optional buttonPressed = Alerts.Alert(Alert.AlertType.CONFIRMATION, "Your password is not very high quality, are you sure you want to continue?");
                if (buttonPressed.isPresent() && buttonPressed.get() == ButtonType.OK) {
                    addOrUpdate();
                    account = null;

                }

            } else {
                addOrUpdate();
                account = null;

            }

        }
    }

    private void addOrUpdate() throws IOException {
        String encryptedPassword;
        String encryptedUserName;
        String writtenDescription;
        AccountCategories category = null;
        String siteOrAppName;
        byte[] userNameNonce;
        byte[] passwordNonce;
        boolean found = false;
        if (ShowPassword_checkBox.isSelected()) {
            password = password_TxtField.getText();
        } else {
            password = password_PassField.getText();
        }
        if (NewCategory_TxtField.isDisabled()) {
            category = accountCategory_Combo.getValue();

        } else {
            String categoryName = NewCategory_TxtField.getText();

            for (AccountCategories account : categoriesList) {
                if (account.getAccountCategoryName().equalsIgnoreCase(categoryName)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                Alerts.Alert(Alert.AlertType.ERROR, "A category with the same name already exists in the program");
            } else {

                category = new AccountCategories(0, userID, categoryName, "");
                insertCategory(category);

            }
        }

        userName = userName_TxtField.getText();
        siteOrAppName = SiteName_TxtField.getText();
        writtenDescription = Description_TxtArea.getText();
        passwordNonce = PasswordSecurityUtils.generateNonce();
        userNameNonce = PasswordSecurityUtils.generateNonce();
        encryptedPassword = PasswordSecurityUtils.encrypt(password, passwordNonce, KeyManager.getCurrentKey());
        encryptedUserName = PasswordSecurityUtils.encrypt(userName, userNameNonce, KeyManager.getCurrentKey());
        if (account != null) {
            account.setSiteOrAppName(SiteName_TxtField.getText());
            account.setDescription(writtenDescription);
            account.setAccountCategory(category);
            account.setEncryptedPassword(encryptedPassword);
            account.setPasswordNonce(passwordNonce);
            account.setEncryptedUserName(encryptedUserName);
            account.setUserNameNonce(userNameNonce);
            account.setUserID(userID);

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
            PassManagerDB.updateRecord("AccountVault", accountVaultTableColumns, items);
            Alerts.Alert(Alert.AlertType.INFORMATION, "The account is updated");

        } else {

            account = new AccountsOfUsers(
                    0, userID, category, siteOrAppName, writtenDescription, encryptedUserName, encryptedPassword, null, userNameNonce, passwordNonce, null);
            items = new Object[]{
                0,
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
            PassManagerDB.insertRecord("AccountVault", accountVaultTableColumns, items);
            Alerts.Alert(Alert.AlertType.INFORMATION, "The account is created");
        }
        App.setRoot("AccountVault");

    }

    @FXML
    private void checkBox_AddCategory_call(ActionEvent event) {
        if (AddCategory_CheckBox.isSelected()) {
            accountCategory_Combo.setDisable(true);
            accountCategory_Combo.setValue(null);
            NewCategory_TxtField.setVisible(true);
            NewCategory_TxtField.setDisable(false);
        } else {
            accountCategory_Combo.setDisable(false);
            NewCategory_TxtField.setVisible(false);
            NewCategory_TxtField.setDisable(true);
        }
    }

    @FXML
    private void randomPass_Btn_call(ActionEvent event) {
        String character_set = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{};:'\"<>,./|\\";
        StringBuilder randomPass = new StringBuilder();

        final int PASSWORD_LENGTH = 32;

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int character_index = random.nextInt(character_set.length());
            randomPass.append(character_set.charAt(character_index));
        }
        password_PassField.setText(randomPass.toString());
        password_TxtField.setText(randomPass.toString());

    }

    @FXML
    private void showPassword_checkBox_call(ActionEvent event) {

        if (ShowPassword_checkBox.isSelected()) {
            String password = password_PassField.getText();
            password_PassField.setDisable(true);
            password_PassField.setVisible(false);
            password_TxtField.setDisable(false);
            password_TxtField.setVisible(true);
            password_TxtField.setText(password);
        } else {
            String password = password_TxtField.getText();
            password_TxtField.setVisible(false);
            password_TxtField.setDisable(true);
            password_PassField.setDisable(false);
            password_PassField.setVisible(true);
            password_PassField.setText(password);
        }

    }

    private void insertCategory(AccountCategories newCategory) {
        try {
            items = new Object[]{
                0, userID, newCategory.getAccountCategoryName(), ""
            };
            int ID = PassManagerDB.insertRecord("AccountCategories", accountCategoriesTableColumnNames, items);
            newCategory.setAccountCategoryID(ID);
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage() + "nigg");
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

    private void initializeAccountCategoriesComboBox() {
        accountCategory_Combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AccountCategories category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getAccountCategoryName());
                }
            }
        });

        accountCategory_Combo.setButtonCell(
                accountCategory_Combo.getCellFactory().call(null));
        accountCategory_Combo.setItems(categoriesList);
        if (account != null) {
            int categoryID = account.getAccountCategory().getAccountCategoryID();
            for (AccountCategories category : categoriesList) {
                if (category.getAccountCategoryID() == categoryID) {
                    accountCategory_Combo.setValue(category);
                    break;
                }
            }
        }
    }

    private boolean checkInput() {

        String password;

        if (ShowPassword_checkBox.isSelected()) {
            password = password_TxtField.getText().trim();
        } else {
            password = password_PassField.getText().trim();
        }

        String userName = userName_TxtField.getText().trim();
        String siteName = SiteName_TxtField.getText().trim();
        String description = Description_TxtArea.getText().trim();

        /*
        Site/App Name
        - letters
        - numbers
        - spaces
        - common symbols
         */
        if (!siteName.matches("^[A-Za-z0-9 ._\\-@]{1,100}$")) {
            Alerts.Alert(Alert.AlertType.ERROR, "Invalid site/application name");
            return false;
        }

        /*
        Username / Email
         */
        if (!userName.matches("^[\\w.@+-]{3,200}$")) {
            Alerts.Alert(Alert.AlertType.ERROR, "Invalid username.\n");

            return false;
        }

        /*
        Category validation
         */
        if (AddCategory_CheckBox.isSelected()) {

            String category = NewCategory_TxtField.getText().trim();

            if (!category.matches("^[A-Za-z0-9 _\\-]{1,50}$")) {

                Alerts.Alert(Alert.AlertType.ERROR, "Invalid category name");

                return false;
            }

        } else if (accountCategory_Combo.getValue() == null) {

            Alerts.Alert(Alert.AlertType.ERROR,
                    "Please select a category.");

            return false;
        }

        /*
        Description
        Allows almost anything printable/newlines
         */
        if (!description.matches("^[\\p{Print}\\p{Space}]{0,1000}$")) {

            Alerts.Alert(Alert.AlertType.ERROR,
                    "Description contains invalid characters.");

            return false;
        }

        return true;
    }


}
