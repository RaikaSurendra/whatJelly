package com.example.webapp.tags;

import org.apache.commons.jelly.TagLibrary;

/**
 * Custom tag library for application-specific tags
 */
public class AppTagLibrary extends TagLibrary {
    
    public AppTagLibrary() {
        // Register custom SQL tags
        registerTag("sqlQuery", SqlQueryTag.class);
        registerTag("sqlUpdate", SqlUpdateTag.class);
        registerTag("sqlExecute", SqlExecuteTag.class);
        
        // Register utility tags
        registerTag("formatDate", FormatDateTag.class);
        registerTag("formatNumber", FormatNumberTag.class);
    }
}
