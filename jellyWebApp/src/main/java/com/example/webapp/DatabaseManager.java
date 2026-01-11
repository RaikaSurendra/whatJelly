package com.example.webapp;

import org.apache.commons.dbcp.BasicDataSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Manages database connections and initialization
 */
public class DatabaseManager {
    private static BasicDataSource dataSource;
    
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    static {
        initializeDataSource();
        initializeDatabase();
    }
    
    private static void initializeDataSource() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        
        // Connection pool settings
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);
        
        System.out.println("Database connection pool initialized");
    }
    
    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Read and execute init.sql
            InputStream is = DatabaseManager.class.getResourceAsStream("/init.sql");
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sql = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    
                    if (line.endsWith(";")) {
                        String statement = sql.toString();
                        stmt.execute(statement);
                        sql = new StringBuilder();
                    }
                }
                
                reader.close();
                System.out.println("Database schema and sample data initialized");
            } else {
                System.err.println("Warning: init.sql not found");
            }
            
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }
    
    public static void shutdown() {
        try {
            if (dataSource != null) {
                dataSource.close();
                System.out.println("Database connection pool closed");
            }
        } catch (Exception e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
