# LORD Account Manager

A local desktop application for securely storing account credentials and recovery information.

The application supports multiple users, with all data stored in a single local SQLite database. User passwords are protected using Argon2 password hashing, while sensitive account information is encrypted using AES-256 with encryption keys derived from the user's master password using Argon2. Users can securely store usernames, passwords, descriptions, and recovery phrases, and all data remains on the local device.

## Features

* Multi-user support using a single local SQLite database
* Argon2 password hashing for user authentication
* Argon2-based key derivation for AES-256 encryption
* Secure storage of usernames, passwords, recovery phrases, and account notes
* Fully offline — no cloud services or external servers

## Tech Stack

* Java
* JavaFX
* SQLite
* Maven
* Argon2
* AES-256
