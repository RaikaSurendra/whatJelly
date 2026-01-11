package com.example.webapp.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import java.text.DecimalFormat;

/**
 * Custom tag to format numbers
 * 
 * Usage:
 * <app:formatNumber value="${price}" pattern="#,##0.00"/>
 */
public class FormatNumberTag extends TagSupport {
    private Object value;
    private String pattern = "#,##0.00";
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            if (value == null) {
                output.write("0");
                return;
            }
            
            Number number;
            if (value instanceof Number) {
                number = (Number) value;
            } else {
                number = Double.parseDouble(value.toString());
            }
            
            DecimalFormat df = new DecimalFormat(pattern);
            String formatted = df.format(number);
            
            output.write(formatted);
            
        } catch (Exception e) {
            throw new JellyTagException("Error formatting number: " + e.getMessage(), e);
        }
    }
}
