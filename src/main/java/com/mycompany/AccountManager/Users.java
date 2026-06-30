package com.mycompany.AccountManager;

/**
 * Represents a user in the app.
 */
public class Users {

    private int userID;
    private String userName;
    private String hashedPassword;
    private byte[] encryptionSalt;

    /**
     * Constructs a new Users object.
     *
     * @param userID The unique identifier for the user.
     * @param userName The username of the user.
     * @param hashedPassword The hashed password of the user.
     * @param encryptionSalt The salt used for encryption.
     */
    public Users(int userID, String userName, String hashedPassword, byte[] encryptionSalt) {
        this.userID = userID;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.encryptionSalt = encryptionSalt;
    }

    // --- Getters ---

    /**
     * Gets the user ID.
     * @return The user's ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the username.
     * @return The user's username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the hashed password.
     * @return The user's hashed password.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Gets the encryption salt.
     * @return The salt used for encryption.
     */
    public byte[] getEncryptionSalt() {
        return encryptionSalt;
    }

    // --- Setters ---

    /**
     * Sets the user ID.
     * @param userID The new user ID.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Sets the username.
     * @param userName The new username.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the hashed password.
     * @param hashedPassword The new hashed password.
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Sets the encryption salt.
     * @param encryptionSalt The new encryption salt.
     */
    public void setEncryptionSalt(byte[] encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }
}
