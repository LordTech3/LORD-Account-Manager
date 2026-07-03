# LORD Account Manager

A local desktop application for securely managing account credentials and recovery information.

The application supports multiple users, with all user data stored in a single SQLite database. User passwords are hashed using Argon2, while sensitive account information is encrypted with AES-256 using encryption keys derived from the user's master password via Argon2. All data is stored locally, and the application does not rely on any cloud or external servers.

## ✨ Features

* Multi-user support
* Local SQLite database
* Argon2 password hashing
* Argon2-based key derivation
* AES-256 encryption for sensitive account data
* Stores account details such as:
  * Website or application names
  * Account usernames
  * Passwords
  * Recovery phrases or backup codes
  * Descriptions and notes
* Fully offline

## 🛠️ Technologies

* Java
* JavaFX
* Maven
* SQLite
* Argon2
* AES-256

## 📦 Building

### 📋 Prerequisites

* JDK 11 or later
* Maven

### 📥 Clone the Repository

```bash
git clone https://github.com/LordTech3/LORD-Account-Manager.git
cd LORD-Account-Manager
```

### ⚙️ Build

```bash
mvn clean package
```

This creates a JAR in the `target` directory, which can be run by the JVM.

## 🛡️ Security

This application is designed with local security in mind.

* User passwords are never stored in plaintext. They are hashed using Argon2.
* Encryption keys are derived from the user's master password using Argon2.
* Sensitive account information is encrypted with AES-256 before being written to the database.
* All data remains on the local machine.
* The entire database is stored in a single SQLite file, making it easy to create backups or transfer your data to another system when needed.

## ⚠️ Disclaimer

This application was developed as a personal project to explore cryptography and desktop application development in Java. While care has been taken to follow good security practices, it has not been professionally audited. As with any software, undiscovered bugs or security vulnerabilities may still exist.
