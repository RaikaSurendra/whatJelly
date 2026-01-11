package com.example.webapp.tags;

import com.example.webapp.DatabaseManager;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Custom tag to execute arbitrary SQL statements
 * 
 * Usage:
 * <app:sqlExecute>
 *   CREATE TABLE IF NOT EXISTS temp (id INT, name VARCHAR(100))
 * </app:sqlExecute>
 */
public class SqlExecuteTag extends TagSupport {
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            // Get SQL from tag body
            String sql = getBodyText();
            
            if (sql == null || sql.trim().isEmpty()) {
                throw new JellyTagException("SQL statement is required in tag body");
            }
            
            sql = sql.trim();
            
            // Execute SQL
            execute(sql);
            
        } catch (Exception e) {
            throw new JellyTagException("Error executing SQL: " + e.getMessage(), e);
        }
    }
    
    private void execute(String sql) throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
        }
    }
}
