/**
 * This class is used for inserting,updating or removing a record from the
 * database, and retrieving records from the database and returns them as
 * observable lists
 */
package com.mycompany.AccountManager;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class PassManagerDB {

    /**
     *
     * @author MLX
     */
    /**
     * Connection object used to connect to the database.
     */
    private static Connection connection;

    /**
     * private constructor is used to ensure the user does not create an
     * instance of this class.
     */
    private PassManagerDB() {

    }
    public static Connection getConnection(){
        return connection;
    }

    /**
     * connects to database.
     */
    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            /* Path to the SQLite database*/
            String URL = "jdbc:sqlite:LORD Account Manager.db";
            connection = DriverManager.getConnection(URL);
            Statement statement = connection.createStatement();
            /* turns on the foreign keys of the database */
            statement.execute("PRAGMA foreign_keys = ON;");
            return true;
        } catch (ClassNotFoundException e) {
            Alerts.Alert(Alert.AlertType.ERROR, "SQLite driver not found");
            return false;
        } catch (SQLException a) {
            Alerts.Alert(Alert.AlertType.ERROR, a.getMessage());
            return false;
        }
    }

    /**
     * Disconnects from the database
     */
    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException d) {
            Alerts.Alert(Alert.AlertType.ERROR, d.getMessage());
        }
    }

    /**
     * Creates the tables of the database.
     */
    public static void createTables() {

        if (connection == null) {
            Alerts.Alert(Alert.AlertType.ERROR, "There isn't a connection with the database");
            return;
        }

        try {
            Statement statement = connection.createStatement();

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Users ("
                    + "UserID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "UserName TEXT UNIQUE NOT NULL, "
                    + "HashedPassword TEXT NOT NULL, "
                    + "EncryptionSalt BLOB NOT NULL "
                    + ");"
            );

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS AccountCategories ("
                    + "AccountCategoryID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "UserID INTEGER NOT NULL, "
                    + "AccountCategoryName TEXT NOT NULL, "
                    + "AccountCategoryDescription TEXT, "
                    + "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE"
                    + ");"
            );

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS AccountVault ("
                    + "AccountID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "UserID INTEGER NOT NULL, "
                    + "AccountCategoryID INTEGER, "
                    + "SiteAppName TEXT NOT NULL, "
                    + "AccountDescription TEXT, "
                    + "EncryptedSiteUserName TEXT NOT NULL, "
                    + "EncryptedPassword TEXT NOT NULL, "
                    + "EncryptedRecoveryPhrases TEXT, "
                    + "UserNameNonce BLOB NOT NULL, "
                    + "PasswordNonce BLOB NOT NULL, "
                    + "EncryptedRecoveryPhrasesNonce BLOB, "
                    + "FOREIGN KEY (AccountCategoryID) REFERENCES AccountCategories(AccountCategoryID) ON DELETE CASCADE, "
                    + "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE"
                    + ");"
            );

        } catch (SQLException e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    /**
     * Inserts the given item into the specified table in the database
     *
     * @param tableName the name of the table that the record will be inserted
     * in
     * @param columnNames the column names of the table
     * @param items contains The items of the record that will be inserted into
     * each column of the table
     * @return the primary key
     */
    public static int insertRecord(String tableName, String[] columnNames, Object[] items) {
        try {
            /*uses a string builder in order to create a specific insert command statement for the given table*/
            StringBuilder command = new StringBuilder("INSERT INTO " + tableName + " (");
            /* Creates the first part of the SQL command string (column names) */
            for (int i = 0; i < columnNames.length; i++) {
                command.append(columnNames[i]);
                if (i < columnNames.length - 1) {
                    command.append(", ");
                } else {
                    command.append(") VALUES (");
                }
            }
            /*creates the second part of the command string by appending placeholders for prepared statement */
            for (int j = 0; j < items.length; j++) {
                command.append("?");
                if (j < items.length - 1) {
                    command.append(", ");
                } else {
                    command.append(")");
                }
            }
            /* An instance of PreparedStatement is used to prepare the SQL statement securely and retrieve the auto generated primary key of the inserted record. */
            PreparedStatement Pstatement = connection.prepareStatement(command.toString(), Statement.RETURN_GENERATED_KEYS);
            /* Iterates through the items array,Index 0 is skipped because it represents the primary key, which is generated automatically by the database. */
            for (int n = 1; n < items.length; n++) {
                Pstatement.setObject(n + 1, items[n]);
            }
            /* Executes the Insert command, adding the new record to the database */
            Pstatement.executeUpdate();
            /* Retrieves the auto-generated primary key from the database after insertion, so it can be used for establishing relationships with other tables */
            ResultSet rs = Pstatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException sq) {
            Alerts.Alert(Alert.AlertType.ERROR, sq.getMessage());
            return -1;
        }
        return -1;
    }

    /**
     * Updates a given record in the given table.
     *
     * @param tableName the name of the table that the record is in
     * @param columnNames the column names of the table
     * @param items the items of the record that will replace the old one
     */
    public static boolean updateRecord(String tableName, String[] columnNames, Object[] items)  {
        /*uses a string builder in order to create a specific update command statement for the given table*/
       
        StringBuilder command = new StringBuilder("Update " + tableName + " SET ");
        /* appends "column = ?" to the command string for each column of the table */
        for (int i = 1; i < columnNames.length; i++) {
            if (i < columnNames.length - 1) {
                command.append(columnNames[i] + " = ?, ");
            } else {
                /*This append is used to specify which record of the database will be updated*/
                command.append(columnNames[i] + " = ? where " + columnNames[0] + " = ? ");
            }
        }
        try {
            PreparedStatement pStatement = connection.prepareStatement(command.toString());
            /* Assigns each value to its corresponding placeholder in the command statement*/
            for (int i = 1; i < items.length; i++) {
                pStatement.setObject(i, items[i]);
            }
            /* Assigns the ID of the record in the WHERE = ? placeholder */
            pStatement.setObject(items.length, items[0]);
            /* Executes the update command,updating the record in the database */
            pStatement.executeUpdate();
        } catch (SQLException e) {
            Alerts.Alert(Alert.AlertType.ERROR,
                    "Failed to update " + tableName + " \n" + e.getMessage());
            return false;
        }
        return true;
        
    }

    /**
     * Deletes a record from a table using the given values.
     *
     * @param tableName
     * @param columnNames
     * @param items
     */
    public static void deleteRecord(String tableName,
            String[] deletingColumnNames,
            Object[] items) {
        /* creates the DELETE command statement */
        StringBuilder command = new StringBuilder("DELETE FROM " + tableName + " WHERE " + deletingColumnNames[0] + " = ? ");
        try {
            /* add additional columns to delete values from more than one column */
            for (int i = 1; i < deletingColumnNames.length; i++) {
                command.append("AND " + deletingColumnNames[i] + " = ? ");
            }
            PreparedStatement pStatement = connection.prepareStatement(command.toString());
            /* Assigns each value to its corresponding placeholder in the command statement */
            for (int i = 0; i < items.length; i++) {
                pStatement.setObject(i + 1, items[i]);
            }
            /* Executes the delete command, deleting the record from the database */
            pStatement.executeUpdate();
        } catch (SQLException e) {
            Alerts.Alert(Alert.AlertType.ERROR, "Failed to delete item \n" + e.getMessage());
        }
    }

    public static ObservableList<AccountCategories> getAccountCategories(int userID) {

        try {
            ObservableList<AccountCategories> accountCategoriesList = FXCollections.observableArrayList();
            String command = "select * FROM AccountCategories WHERE UserID = ?";
            PreparedStatement pStatement = connection.prepareStatement(command);
            pStatement.setInt(1, userID);
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                int categoryID = rs.getInt("AccountCategoryID");
                String categoryName = rs.getString("AccountCategoryName");
                String categoryDescription = rs.getString("AccountCategoryDescription");
                accountCategoriesList.add(new AccountCategories(categoryID, userID, categoryName, categoryDescription));

            }
            return accountCategoriesList;
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        }

    }

    public static ObservableList<Users> getUsers() {

        try {
            ObservableList<Users> usersList = FXCollections.observableArrayList();
            String command = "select * FROM Users";
            PreparedStatement pStatement = connection.prepareStatement(command);

            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String userName = rs.getString("UserName");
                String hashedPassword = rs.getString("HashedPassword");
                byte[] encryptionSalt = rs.getBytes("EncryptionSalt");
                usersList.add(new Users(userID, userName, hashedPassword, encryptionSalt));
            }
            return usersList;
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        }
    }

    public static Map<String, Users> getUsersMap() {

        try {
            Map<String, Users> usersMap = new HashMap<String, Users>();
            String command = "SELECT * FROM Users";
            PreparedStatement pStatement = connection.prepareStatement(command);
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String userName = rs.getString("UserName");
                String hashedPassword = rs.getString("HashedPassword");
                byte[] encryptionSalt = rs.getBytes("EncryptionSalt");
                usersMap.put(userName, new Users(userID, userName, hashedPassword, encryptionSalt));
            }
            return usersMap;
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param userID
     * @return
     */
    public static ObservableList<AccountsOfUsers> getAccountsOfUsers(int userID) {

        try {
            ObservableList<AccountsOfUsers> accountsOfUsersList = FXCollections.observableArrayList();
            ObservableList<AccountCategories> userAccountCategories = getAccountCategories(userID);
            String command = "select * FROM AccountVault WHERE UserID = ? ";
            PreparedStatement pStatement = connection.prepareStatement(command);
            pStatement.setInt(1, userID);
            ResultSet rs = pStatement.executeQuery();

            int i = 0;

            while (rs.next()) {
                int accountCategoryID = rs.getInt("AccountCategoryID");
                for (; accountCategoryID != userAccountCategories.get(i).getAccountCategoryID(); i++);
                int accountID = rs.getInt("AccountID");
                String siteName = rs.getString("SiteAppName");
                String encryptedUserName = rs.getString("EncryptedSiteUserName");
                String description = rs.getString("AccountDescription");
                String encryptedPassword = rs.getString("EncryptedPassword");

                byte[] userNameNonce = rs.getBytes("UserNameNonce");

                byte[] passwordNonce = rs.getBytes("PasswordNonce");
                byte [] encryptedRecoveryPhrasesNonce = rs.getBytes("EncryptedRecoveryPhrasesNonce");
                String EncryptedRecoveryPhrases = rs.getString("EncryptedRecoveryPhrases");
                accountsOfUsersList.add(new AccountsOfUsers(accountID, userID, userAccountCategories.get(i), siteName, description, encryptedUserName, encryptedPassword,EncryptedRecoveryPhrases, userNameNonce, passwordNonce,encryptedRecoveryPhrasesNonce));
                i = 0;
            }
            return accountsOfUsersList;
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        }

    }
}
