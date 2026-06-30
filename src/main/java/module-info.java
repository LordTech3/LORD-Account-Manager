module com.mycompany.AccountManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    opens com.mycompany.AccountManager to javafx.fxml;
    exports com.mycompany.AccountManager;
    requires com.nulabinc.zxcvbn;
   requires de.mkammerer.argon2.nolibs;
}
