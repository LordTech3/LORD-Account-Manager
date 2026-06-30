package com.mycompany.AccountManager;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AccountCategoriesController implements Initializable {

    @FXML
    private TextArea AccountCategoryDescription_AddCategory_TxtArea;

    @FXML
    private TextField AccountCategoryName_AddCategory_TxtField;

    @FXML
    private TextArea AccountCategoryDescription_UpdateCategory_TxtArea;

    @FXML
    private TextField AccountCategoryName_UpdateCategory_TxtField;

    @FXML
    private ComboBox<AccountCategories> accountCategory_ComboBox;

    public static int userID;

    private ObservableList<AccountCategories> accountCategoriesList
            = FXCollections.observableArrayList();

    private SortedList<AccountCategories> sortedAccountCategories;

    private final String[] fieldNames = {
        "AccountCategoryID",
        "UserID",
        "AccountCategoryName",
        "AccountCategoryDescription"
    };

    private Object[] items;

    private Optional<ButtonType> buttonPressed;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (PassManagerDB.connect()) {

            accountCategoriesList
                    = PassManagerDB.getAccountCategories(userID);

            sortedAccountCategories
                    = new SortedList<>(
                            accountCategoriesList,
                            Comparator.comparing(
                                    AccountCategories::getAccountCategoryName));

            initializeAccountCategoriesComboBox();
        }
    }

    @FXML
    private void back_Btn_call(ActionEvent event) throws IOException {
        App.setRoot("UserDashboard");
    }

    @FXML
    private void clear_AddCategory_call(ActionEvent event) {

        AccountCategoryName_AddCategory_TxtField.clear();
        AccountCategoryDescription_AddCategory_TxtArea.clear();
    }

    @FXML
    private void save_Btn_call(ActionEvent event) {

        if (checkCategoryAddCategory()) {

            AccountCategories category
                    = new AccountCategories(
                            0, userID,
                            AccountCategoryName_AddCategory_TxtField
                                    .getText()
                                    .trim(),
                            AccountCategoryDescription_AddCategory_TxtArea
                                    .getText()
                                    .trim());

            items = new Object[]{
                category.getAccountCategoryID(),
                category.getUserID(),
                category.getAccountCategoryName(),
                category.getAccountCategoryDescription()
            };

            category.setAccountCategoryID(
                    PassManagerDB.insertRecord(
                            "AccountCategories",
                            fieldNames,
                            items));

            accountCategoriesList.add(category);

            Alerts.Alert(
                    Alert.AlertType.INFORMATION,
                    "Account Category has been added");

            clear_AddCategory_call(event);
        }
    }

    @FXML
    private void clear_UpdateCategory_call(ActionEvent event) {

        accountCategory_ComboBox.setValue(null);

        AccountCategoryName_UpdateCategory_TxtField.clear();

        AccountCategoryDescription_UpdateCategory_TxtArea.clear();
    }

    @FXML
    private void deleteAccount_Btn_call(ActionEvent event) {

        AccountCategories category
                = accountCategory_ComboBox.getValue();

        if (category != null) {

            buttonPressed = Alerts.Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this category? ALL ACCOUNTS BELONGING TO IT WILL BE DELETED\n");

            if (buttonPressed.isPresent()
                    && buttonPressed.get() == ButtonType.OK) {

                String[] columns = {
                    "AccountCategoryID"
                };

                Object[] values = {
                    category.getAccountCategoryID()
                };

                PassManagerDB.deleteRecord(
                        "AccountCategories",
                        columns,
                        values);

                accountCategoriesList.remove(category);

                Alerts.Alert(
                        Alert.AlertType.INFORMATION,
                        "Account Category has been deleted");

                clear_UpdateCategory_call(event);
            }

        } else {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "You have not selected a category");
        }
    }

    @FXML
    private void saveChanges_Btn_call(ActionEvent event) {

        if (checkCategoryUpdateCategory()) {

            AccountCategories updatedCategory
                    = new AccountCategories(
                            accountCategory_ComboBox
                                    .getValue()
                                    .getAccountCategoryID(),
                            userID,
                            AccountCategoryName_UpdateCategory_TxtField
                                    .getText()
                                    .trim(),
                            AccountCategoryDescription_UpdateCategory_TxtArea
                                    .getText()
                                    .trim());

            items = new Object[]{
                updatedCategory.getAccountCategoryID(),
                updatedCategory.getUserID(),
                updatedCategory.getAccountCategoryName(),
                updatedCategory.getAccountCategoryDescription()
            };

            PassManagerDB.updateRecord(
                    "AccountCategories",
                    fieldNames,
                    items);

            accountCategoriesList.add(updatedCategory);

            Alerts.Alert(
                    Alert.AlertType.INFORMATION,
                    "Account Category has been updated");
            accountCategoriesList.remove(
                    accountCategory_ComboBox.getValue());

            clear_UpdateCategory_call(event);
        }
    }

    private void initializeAccountCategoriesComboBox() {

        accountCategory_ComboBox.setItems(
                sortedAccountCategories);

        accountCategory_ComboBox.setCellFactory(
                lv -> new ListCell<>() {

            @Override
            protected void updateItem(
                    AccountCategories category,
                    boolean empty) {

                super.updateItem(category, empty);

                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(
                            category.getAccountCategoryName());
                }
            }
        });

        accountCategory_ComboBox.setButtonCell(
                accountCategory_ComboBox
                        .getCellFactory()
                        .call(null));

        accountCategory_ComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldCategory, newCategory) -> {

                            if (newCategory != null) {

                                AccountCategoryName_UpdateCategory_TxtField
                                        .setText(
                                                newCategory.getAccountCategoryName());

                                AccountCategoryDescription_UpdateCategory_TxtArea
                                        .setText(
                                                newCategory.getAccountCategoryDescription());
                            }
                        });
    }

    private boolean checkCategoryAddCategory() {

        String categoryName
                = AccountCategoryName_AddCategory_TxtField
                        .getText()
                        .trim();

        String categoryDescription
                = AccountCategoryDescription_AddCategory_TxtArea
                        .getText()
                        .trim();

        if (!categoryName.matches("[a-zA-Z0-9\\s]{2,30}")) {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "Category name is empty or invalid");

            return false;
        }

        if (categoryDescription.isBlank()) {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "Category description is empty");

            return false;
        }

        for (AccountCategories category
                : accountCategoriesList) {

            if (category.getAccountCategoryName()
                    .equalsIgnoreCase(categoryName)) {

                Alerts.Alert(
                        Alert.AlertType.ERROR,
                        "This category name already exists");

                return false;
            }
        }

        return true;
    }

    private boolean checkCategoryUpdateCategory() {

        AccountCategories selectedCategory
                = accountCategory_ComboBox.getValue();

        String categoryName
                = AccountCategoryName_UpdateCategory_TxtField
                        .getText()
                        .trim();

        String categoryDescription
                = AccountCategoryDescription_UpdateCategory_TxtArea
                        .getText()
                        .trim();

        if (selectedCategory == null) {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "You have not selected a category");

            return false;

        } else if (!categoryName.matches("[a-zA-Z0-9\\s]{2,30}")) {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "Category name is empty or invalid");

            return false;

        } else if (categoryDescription.isBlank()) {

            Alerts.Alert(
                    Alert.AlertType.ERROR,
                    "Category description is empty");

            return false;
        }

        for (AccountCategories category
                : accountCategoriesList) {

            if (category.getAccountCategoryName()
                    .equalsIgnoreCase(categoryName)
                    && category.getAccountCategoryID()
                    != selectedCategory.getAccountCategoryID()) {

                Alerts.Alert(
                        Alert.AlertType.ERROR,
                        "This category name already exists");

                return false;
            }
        }

        return true;
    }
}
