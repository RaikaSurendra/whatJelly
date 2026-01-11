package com.learning.jelly;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class JellyRunner {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java JellyRunner <jelly-script-path> [output-file]");
            System.out.println("Example: java JellyRunner examples/01-hello-world.jelly");
            System.exit(1);
        }
        
        String scriptPath = args[0];
        String outputPath = args.length > 1 ? args[1] : null;
        
        try {
            runJellyScript(scriptPath, outputPath);
        } catch (Exception e) {
            System.err.println("Error executing Jelly script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void runJellyScript(String scriptPath, String outputPath) throws Exception {
        JellyContext context = new JellyContext();
        
        XMLOutput output;
        if (outputPath != null) {
            FileOutputStream fos = new FileOutputStream(outputPath);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
            output = XMLOutput.createXMLOutput(writer);
            System.out.println("Output will be written to: " + outputPath);
        } else {
            output = XMLOutput.createXMLOutput(System.out);
        }
        
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            throw new IllegalArgumentException("Script file not found: " + scriptPath);
        }
        
        System.out.println("Executing Jelly script: " + scriptPath);
        System.out.println("----------------------------------------");
        
        context.runScript(scriptFile, output);
        output.flush();
        
        if (outputPath != null) {
            output.close();
            System.out.println("\n----------------------------------------");
            System.out.println("Output written successfully!");
        }
    }
}
