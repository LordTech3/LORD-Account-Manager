package com.mycompany.AccountManager;

public class AccountCategories {

    private int userID;
    private int accountCategoryID;
    private String accountCategoryName;
    private String accountCategoryDescription;

    // Constructor
    public AccountCategories( int accountCategoryID,int userID, String accountCategoryName) {
        this.accountCategoryID = accountCategoryID;
        this.accountCategoryName = accountCategoryName;
        this.userID = userID;
        this.accountCategoryDescription = "";
    }
      public AccountCategories(int accountCategoryID,int userID, String accountCategoryName, String categoryDescription) {
        this.accountCategoryID = accountCategoryID;
        this.accountCategoryName = accountCategoryName;
        this.userID = userID;
        this.accountCategoryDescription = categoryDescription;
    }

    // Getters
    public int getAccountCategoryID() {
        return accountCategoryID;
    }

    public String getAccountCategoryName() {
        return accountCategoryName;
    }
      public String getAccountCategoryDescription() {
        return accountCategoryDescription;
    }
      public void setAccountCategoryDescription(String description){
          this.accountCategoryDescription = description;
      }

    // Setters
    public void setAccountCategoryID(int accountCategoryID) {
        this.accountCategoryID = accountCategoryID;
    }

    public void setAccountCategoryName(String accountCategoryName) {
        this.accountCategoryName = accountCategoryName;
    }
    public int getUserID() {
    return userID;
}

public void setUserID(int userID) {
    this.userID = userID;
}
}
