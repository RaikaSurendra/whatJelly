package com.learning.jelly;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JellyContextDemo {
    
    public static void main(String[] args) throws Exception {
        demonstrateContext();
        demonstrateVariableSharing();
    }
    
    private static void demonstrateContext() throws Exception {
        System.out.println("=== JellyContext Demonstration ===\n");
        
        JellyContext context = new JellyContext();
        
        context.setVariable("greeting", "Hello from Java!");
        context.setVariable("user", "Developer");
        context.setVariable("count", 42);
        
        Map<String, Object> config = new HashMap<>();
        config.put("appName", "Jelly Learning App");
        config.put("version", "1.0.0");
        context.setVariable("config", config);
        
        System.out.println("Variables set in context:");
        System.out.println("  greeting: " + context.getVariable("greeting"));
        System.out.println("  user: " + context.getVariable("user"));
        System.out.println("  count: " + context.getVariable("count"));
        System.out.println("  config: " + context.getVariable("config"));
        System.out.println();
        
        String simpleScript = 
            "<?xml version='1.0'?>" +
            "<j:jelly xmlns:j='jelly:core'>" +
            "  <j:out value='${greeting}'/>" +
            "  <j:out value='${\"\\n\"}'/>" +
            "  <j:out value='User: ${user}'/>" +
            "  <j:out value='${\"\\n\"}'/>" +
            "  <j:out value='Count: ${count}'/>" +
            "  <j:out value='${\"\\n\"}'/>" +
            "  <j:out value='App: ${config.appName} v${config.version}'/>" +
            "</j:jelly>";
        
        StringWriter writer = new StringWriter();
        XMLOutput output = XMLOutput.createXMLOutput(writer);
        
        context.runScript(simpleScript, output);
        output.flush();
        
        System.out.println("Script output:");
        System.out.println(writer.toString());
        System.out.println();
    }
    
    private static void demonstrateVariableSharing() throws Exception {
        System.out.println("\n=== Variable Sharing Between Scripts ===\n");
        
        JellyContext context = new JellyContext();
        
        context.setVariable("sharedData", "This data is shared across scripts");
        
        String script1 = 
            "<?xml version='1.0'?>" +
            "<j:jelly xmlns:j='jelly:core'>" +
            "  <j:out value='Script 1: ${sharedData}'/>" +
            "  <j:set var='fromScript1' value='Set by Script 1'/>" +
            "</j:jelly>";
        
        String script2 = 
            "<?xml version='1.0'?>" +
            "<j:jelly xmlns:j='jelly:core'>" +
            "  <j:out value='Script 2: ${sharedData}'/>" +
            "  <j:out value='${\"\\n\"}'/>" +
            "  <j:out value='From Script 1: ${fromScript1}'/>" +
            "</j:jelly>";
        
        System.out.println("Running Script 1:");
        XMLOutput output1 = XMLOutput.createXMLOutput(System.out);
        context.runScript(script1, output1);
        output1.flush();
        
        System.out.println("\n\nRunning Script 2:");
        XMLOutput output2 = XMLOutput.createXMLOutput(System.out);
        context.runScript(script2, output2);
        output2.flush();
        
        System.out.println("\n\nVariables in context after both scripts:");
        System.out.println("  sharedData: " + context.getVariable("sharedData"));
        System.out.println("  fromScript1: " + context.getVariable("fromScript1"));
    }
}
