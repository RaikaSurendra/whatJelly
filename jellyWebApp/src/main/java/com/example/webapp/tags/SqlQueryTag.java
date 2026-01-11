package com.example.webapp.tags;

import com.example.webapp.DatabaseManager;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom tag to execute SQL queries and store results in context
 * 
 * Usage:
 * <app:sqlQuery var="users" table="users">
 *   SELECT * FROM users WHERE active = true
 * </app:sqlQuery>
 */
public class SqlQueryTag extends TagSupport {
    private String var;
    private String table;
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setTable(String table) {
        this.table = table;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        if (var == null || var.trim().isEmpty()) {
            throw new JellyTagException("'var' attribute is required");
        }
        
        try {
            // Get SQL from tag body
            String sql = getBodyText();
            
            if (sql == null || sql.trim().isEmpty()) {
                throw new JellyTagException("SQL query is required in tag body");
            }
            
            sql = sql.trim();
            
            // Execute query
            List<Map<String, Object>> results = executeQuery(sql);
            
            // Store results in context
            context.setVariable(var, results);
            
            // Also make row count available
            context.setVariable(var + "_count", results.size());
            
        } catch (Exception e) {
            throw new JellyTagException("Error executing SQL query: " + e.getMessage(), e);
        }
    }
    
    private List<Map<String, Object>> executeQuery(String sql) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i).toLowerCase();
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                
                results.add(row);
            }
        }
        
        return results;
    }
}
