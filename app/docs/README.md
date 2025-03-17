
# Lancaster music hall

## Introduction
This is the marketing app for the Lancaster Music hall. Project for the IN2033 Module for year 2024/2025. This project is a Java-based desktop application with a graphical user interface (GUI) and database connectivity using JDBC the database that will be used will be `MYSQL`. There is a possibility to run the mysql DB both locally or with university servers, you can go in more details on the functionality and how to set up the Database in the Database.md file

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java JDK**
  [Download JDK](https://adoptium.net/)
  ```sh
  java -version
  ```
_Not necessary if using IntelliJ or other editors but needed for CLI__
- **Maven (for dependency management and builds)**
  [Download Maven](https://maven.apache.org/download.cgi)
  ```sh
  mvn -version
  ```
  Ensure it prints the Maven version.

- **Database (MYSQL)**
We will be using MYSQL as a database, we will need to interact with other development teams. For example we are the marketing deparment and there are also the operation and box office deparments, each of which have their own databases, we need to provide ways to access these databases and interact with the system.
More information on the database can be found in the Database.md file

## Project Structure
```
mkApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lancaster/
│   │   │       ├── App.java
│   │   │       ├── gui/MainWindow.java
│   │   │       ├── database/DBManager.java
│   │   ├── resources/
│   ├── test/
├── pom.xml (Maven configuration)
```

## Running the Application on CLI
1. **Build the project**
   ```sh
   mvn clean packag
   ```

2. **Run the application**
   ```sh
   mvn exec:java -Dexec.mainClass="com.lancaster.App"
   ```

## Configuring the Database
Look at the Database.md file for all the instructions

You also need to setup a `.env` file which contains secret access keys the format of the file will be in this way

```
ADMIN_USER=""
ADMIN_PASSWORD=""

DATA_USER=""
DATA_PASSWORD=""
 ```
