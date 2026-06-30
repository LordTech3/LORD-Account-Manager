package com.mycompany.AccountManager;

public class AccountsOfUsers {

    private int UserID;
    private int accountID;
    private AccountCategories acccountCategory;
    private String siteOrAppName;
    private String encryptedUserName;
    private String encryptedPassword;
    private byte[] passwordNonce;
    private byte [] userNameNonce;
    private String encryptedRecoveryPhrases;
    private byte [] recoveryPhrasesNonce;
    private String description;

    
    public AccountsOfUsers(int accountID, int userID,  AccountCategories acccountCategory,String siteOrAppName,String description, String encryptedUserName,
                           String encryptedPassword,String encryptedRecoveryPhrases,byte [] userNameNonce, byte[]passwordNonce, byte [] recoveryPhrasesNonce ) {
        this.accountID = accountID;
        this.description = description;
        this.UserID = userID;
        this.siteOrAppName = siteOrAppName;
        this.acccountCategory = acccountCategory;
        this.encryptedUserName = encryptedUserName;
        this.encryptedPassword = encryptedPassword;
        this.userNameNonce = userNameNonce;
        this.passwordNonce= passwordNonce;
        this.recoveryPhrasesNonce = recoveryPhrasesNonce;
        this.encryptedRecoveryPhrases = encryptedRecoveryPhrases;
    }

    // Getters
    public int getUserID() {
        return UserID;
    }
    public byte [] getRecoveryPhrasesNonce (){
        return recoveryPhrasesNonce;
    }
    public String getEncryptedRecoveryPhrases(){
       return  encryptedRecoveryPhrases;
    }
    public void setEncryptedRecoveryPhrases(String EncryptedRecoveryPhrases){
        this.encryptedRecoveryPhrases = EncryptedRecoveryPhrases;
    }
    public void setRecoveryPhrasesNonce (byte [] nonce){
         this.recoveryPhrasesNonce = nonce;
    }

    public  AccountCategories getAccountCategory() {
        return acccountCategory;
    }

    public String getEncryptedUserName() {
        return encryptedUserName;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public byte[] getUserNameNonce() {
        return userNameNonce;
    }
     public byte[] getPasswordNonce() {
        return passwordNonce;
    }

    public String getDescription() {
        return description;
    }
 public String getSiteOrAppName() {
        return siteOrAppName;
    }
    // Setters
    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public void setAccountCategory( AccountCategories acccountCategory) {
        this.acccountCategory = acccountCategory;
    }

    public void setEncryptedUserName(String encryptedUserName) {
        this.encryptedUserName = encryptedUserName;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setUserNameNonce(byte[] userNameNonce ) {
        this.userNameNonce = userNameNonce ;
    }
     public void setPasswordNonce(byte[] passwordNonce ) {
        this.passwordNonce = passwordNonce ;
    }

    public void setDescription(String description) {
        this.description = description;
    }
     public void setSiteOrAppName(String siteOrAppName) {
        this.siteOrAppName  =  siteOrAppName;
    }
     public int getAccountID(){
         return this.accountID;
     }
     public void setAccountID (int accountID){
         this.accountID = accountID;
     }
}
