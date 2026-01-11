package com.example.webapp.tags;

import com.example.webapp.DatabaseManager;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Custom tag to execute SQL updates (INSERT, UPDATE, DELETE)
 * 
 * Usage:
 * <app:sqlUpdate var="rowsAffected">
 *   UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = 1
 * </app:sqlUpdate>
 */
public class SqlUpdateTag extends TagSupport {
    private String var;
    
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            // Get SQL from tag body
            String sql = getBodyText();
            
            if (sql == null || sql.trim().isEmpty()) {
                throw new JellyTagException("SQL statement is required in tag body");
            }
            
            sql = sql.trim();
            
            // Execute update
            int rowsAffected = executeUpdate(sql);
            
            // Store result in context if var is specified
            if (var != null && !var.trim().isEmpty()) {
                context.setVariable(var, rowsAffected);
            }
            
        } catch (Exception e) {
            throw new JellyTagException("Error executing SQL update: " + e.getMessage(), e);
        }
    }
    
    private int executeUpdate(String sql) throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(sql);
        }
    }
}
