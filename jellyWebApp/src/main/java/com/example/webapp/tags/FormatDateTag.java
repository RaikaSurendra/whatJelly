package com.example.webapp.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom tag to format dates
 * 
 * Usage:
 * <app:formatDate value="${timestamp}" pattern="yyyy-MM-dd HH:mm:ss"/>
 */
public class FormatDateTag extends TagSupport {
    private Object value;
    private String pattern = "yyyy-MM-dd HH:mm:ss";
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        if (value == null) {
            return;
        }
        
        try {
            Date date;
            if (value instanceof Date) {
                date = (Date) value;
            } else if (value instanceof Long) {
                date = new Date((Long) value);
            } else {
                date = new Date(Long.parseLong(value.toString()));
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            String formatted = sdf.format(date);
            
            output.write(formatted);
            
        } catch (Exception e) {
            throw new JellyTagException("Error formatting date: " + e.getMessage(), e);
        }
    }
}
