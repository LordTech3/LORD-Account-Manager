package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AccountVaultController implements Initializable {

    @FXML
    private TableView<AccountsOfUsers> passwordTable;
    @FXML
    private TableColumn<AccountsOfUsers, String> categoryCol;
    @FXML
    private TableColumn<AccountsOfUsers, String> usernameCol;
    @FXML
    private TableColumn<AccountsOfUsers, String> passwordCol;
    @FXML
    private TableColumn<AccountsOfUsers, String> descriptionCol;
    @FXML
    private TableColumn<AccountsOfUsers, String> websiteNameCol;
    public static int userID;

    private String[] accountVaultTableDeletingColumns = {"AccountID"};
    private ObservableList <AccountsOfUsers> accountsList;
    @FXML
    private ComboBox<AccountCategories> selectAccountCategory_ComboBox;
    private ObservableList accountCategoriesList;
    private ObservableList accountsOfACategory = FXCollections.observableArrayList();

    public AccountVaultController() {
        this.accountCategoriesList = PassManagerDB.getAccountCategories(userID);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Disable user resizing interference
        passwordTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Bind columns to table width (percentages)
        categoryCol.prefWidthProperty().bind(passwordTable.widthProperty().multiply(0.20));

        usernameCol.prefWidthProperty().bind(passwordTable.widthProperty().multiply(0.20));

        passwordCol.prefWidthProperty().bind(passwordTable.widthProperty().multiply(0.15));

        descriptionCol.prefWidthProperty().bind(passwordTable.widthProperty().multiply(0.25));

        websiteNameCol.prefWidthProperty().bind(passwordTable.widthProperty().multiply(0.20));
        accountsList = PassManagerDB.getAccountsOfUsers(userID);
        accountCategoriesList = PassManagerDB.getAccountCategories(userID);
        initializeSelectAccountCategoryCombo();
        passwordTable.setItems(accountsList);
        initializePasswordTable();
        AddOrModifyAccountController.userID = userID;
    }

    @FXML
    public void AddPass_Btn(ActionEvent event) throws IOException {
        App.setRoot("AddOrModifyAccount");
    }

    @FXML
    public void EditPass_Btn(ActionEvent event) throws IOException {
        AccountsOfUsers account = passwordTable.selectionModelProperty().get().getSelectedItem();
        if (account != null) {
            AddOrModifyAccountController.account = account;
            App.setRoot("AddOrModifyAccount");

        } else {
            Alerts.Alert(Alert.AlertType.ERROR, "You haven't selected an account!");
        }
    }

    @FXML
    private void back_Btn_call(ActionEvent event) throws IOException {
        App.setRoot("UserDashboard");
        
    }

    private void initializePasswordTable() {
        categoryCol.setCellValueFactory((cellValue) -> {
            return new ReadOnlyObjectWrapper<>(cellValue.getValue().getAccountCategory().getAccountCategoryName());
        });
        websiteNameCol.setCellValueFactory(new PropertyValueFactory<>("siteOrAppName"));
        usernameCol.setCellValueFactory(value -> {
            return new ReadOnlyObjectWrapper<>("*******");
        });
        passwordCol.setCellValueFactory(value -> {
            return new ReadOnlyObjectWrapper<>("*******");
        });
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

    }

    @FXML
    private void deleteAccount_Btn_call(ActionEvent event) {
        AccountsOfUsers account = passwordTable.getSelectionModel().selectedItemProperty().get();
        if (account != null) {
            Optional buttonPressed = Alerts.Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this account?");
            if (buttonPressed.isPresent() && buttonPressed.get() == ButtonType.OK) {
                Object[] items = new Object[]{account.getAccountID()};
                PassManagerDB.deleteRecord("AccountVault", accountVaultTableDeletingColumns, items);
                Alerts.Alert(Alert.AlertType.INFORMATION, "Account has been deleted");
                accountsList.remove(account);
                reset_Btn_call(event);
            }

        } else {
            Alerts.Alert(Alert.AlertType.ERROR, "You have not selected an account!");
        }
    }

    private void initializeSelectAccountCategoryCombo() {
        selectAccountCategory_ComboBox.setCellFactory(lv -> new ListCell<>() {
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

        selectAccountCategory_ComboBox.setButtonCell(
                selectAccountCategory_ComboBox.getCellFactory().call(null));
        selectAccountCategory_ComboBox.setItems(accountCategoriesList);
        selectAccountCategory_ComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldcategory, newCategory) -> {
            if (newCategory != null) {
                accountsOfACategory.removeAll(accountsOfACategory);
                for(AccountsOfUsers acc: accountsList){
                      if(acc.getAccountCategory().getAccountCategoryID()== newCategory.getAccountCategoryID()){
                          accountsOfACategory.add(acc);
                      } 
                }
          passwordTable.setItems(accountsOfACategory);
            }
        });
    }

    @FXML
    private void reset_Btn_call(ActionEvent event) {
        selectAccountCategory_ComboBox.setValue(null);{
        passwordTable.setItems(accountsList);
    }
    }

    @FXML
    private void RecoveryPhrase_Btn_call(ActionEvent event) throws IOException {
         AccountsOfUsers account = passwordTable.getSelectionModel().selectedItemProperty().get();
        if(account != null){
            RecoveryPhraseWindowController.account = account;
            App.setRoot("RecoveryPhraseWindow");
        }
        else{
            Alerts.Alert(Alert.AlertType.ERROR, "You must select an account before adding or modifying its recovery phrases");
        }
    }
}
        
    


    
