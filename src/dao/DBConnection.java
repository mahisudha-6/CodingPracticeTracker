package dao;

import exception.DatabaseException;
import util.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC connections to the MySQL database.
 * Includes automatic database and table initialization.
 */
public class DBConnection {
    private static final String SERVER_URL; // Base connection URL e.g. jdbc:mysql://localhost:3306/
    private static final String DB_NAME = "coding_tracker_db";
    private static final String USER;
    private static final String PASSWORD;

    static {
        // Load configurations with flexible key mapping
        String rawUrl = ConfigLoader.getProperty("db.url", ConfigLoader.getProperty("url", null));
        if (rawUrl == null) {
            SERVER_URL = "jdbc:mysql://localhost:3306/";
        } else {
            // Extract the base server URL (up to Port)
            int dbIndex = rawUrl.indexOf("3306/");
            if (dbIndex != -1) {
                SERVER_URL = rawUrl.substring(0, dbIndex + 5);
            } else {
                SERVER_URL = "jdbc:mysql://localhost:3306/";
            }
        }
        
        USER = ConfigLoader.getProperty("db.username", ConfigLoader.getProperty("username", "root"));
        PASSWORD = ConfigLoader.getProperty("db.password", ConfigLoader.getProperty("password", ""));

        try {
            // Register MySQL JDBC Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: MySQL JDBC Driver not found on classpath.");
        }
    }

    /**
     * Establishes a new database connection.
     * Before returning the connection, it triggers database auto-creation if not already present.
     * @return a Connection object
     * @throws DatabaseException if connection fails
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            // Auto initialize database schemas
            initializeDatabase();
            
            // Connect to specific database
            String dbUrl = SERVER_URL + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            return DriverManager.getConnection(dbUrl, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException(
                "Unable to establish database connection. " +
                "Verify that MySQL is running on localhost:3306 and the credentials in 'config.properties' are correct.", 
                e
            );
        }
    }

    /**
     * Automatically creates the database, tables, and inserts reference data if they don't exist.
     */
    private static synchronized void initializeDatabase() {
        String serverUrlWithParams = SERVER_URL + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(serverUrlWithParams, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // 1. Create coding_tracker_db database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("USE " + DB_NAME);
            
            // 2. Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(256) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createUsersTable);
            
            // 3. Create topics table
            String createTopicsTable = "CREATE TABLE IF NOT EXISTS topics (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(50) NOT NULL UNIQUE" +
                    ")";
            stmt.executeUpdate(createTopicsTable);
            
            // 4. Create coding_problems table
            String createProblemsTable = "CREATE TABLE IF NOT EXISTS coding_problems (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "title VARCHAR(255) NOT NULL," +
                    "platform VARCHAR(100) NOT NULL," +
                    "difficulty VARCHAR(20) NOT NULL," +
                    "topic_id INT NOT NULL," +
                    "status VARCHAR(20) NOT NULL," +
                    "date_solved DATE DEFAULT NULL," +
                    "notes TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE RESTRICT" +
                    ")";
            stmt.executeUpdate(createProblemsTable);
            
            // 5. Prepopulate topics lookup if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM topics")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO topics (id, name) VALUES " +
                            "(1, 'Arrays')," +
                            "(2, 'Strings')," +
                            "(3, 'Linked List')," +
                            "(4, 'Stack & Queue')," +
                            "(5, 'Trees & Graphs')," +
                            "(6, 'Sorting & Searching')," +
                            "(7, 'Dynamic Programming')," +
                            "(8, 'Greedy Algorithms')," +
                            "(9, 'Recursion & Backtracking')," +
                            "(10, 'Bit Manipulation')");
                }
            }
            
        } catch (SQLException e) {
            // Output a warning but do not throw, so if the user restricted permissions they can still run pre-created schema
            System.err.println("Warning: Database auto-initialization check completed with message: " + e.getMessage());
        }
    }
}
