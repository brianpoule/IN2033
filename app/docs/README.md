
# Getting Started with marketing app for Lancaster music hall

## Introduction
This is the marketing app for the Lancaster Music hall. Project for the IN2033 Module for year 2024/2025. This project is a Java-based desktop application with a graphical user interface (GUI) and database connectivity using JDBC.

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java Development Kit (JDK 17+)**
  [Download JDK](https://adoptium.net/)
  ```sh
  java -version
  ```
  Ensure it prints something like `openjdk version "17.0.x"`

- **Maven (for dependency management and builds)**
  [Download Maven](https://maven.apache.org/download.cgi)
  ```sh
  mvn -version
  ```
  Ensure it prints the Maven version.

- **Database (SQLite/MySQL/PostgreSQL)**
We will be using MYsql

## Cloning the Repository
To get started, clone the project:
```sh
git clone https://github.com/yourusername/mkApp.git
cd mkApp
```

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

## Running the Application
1. **Build the project**
   ```sh
   mvn clean packag
   ```

2. **Run the application**
   ```sh
   mvn exec:java -Dexec.mainClass="com.lancaster.App"
   ```

## Configuring the Database
TODO

## Contributing
We welcome contributions! Follow these steps to contribute:

1. **Fork the repository** on GitHub.
2. **Create a new branch** for your feature or fix:
   ```sh
   git checkout -b feature-branch-name
   ```
3. **Make your changes** and commit them:
   ```sh
   git commit -m "Added new feature X"
   ```
4. **Push to your fork**:
   ```sh
   git push origin feature-branch-name
   ```
5. **Open a pull request** (PR) on GitHub.

### Code Style Guidelines
- Use meaningful variable and method names.
- Follow Java conventions (`camelCase` for variables, `PascalCase` for classes).
- Ensure the code is formatted properly before submitting a PR.

## Reporting Issues
If you find a bug or have a feature request, please open an issue on GitHub.
