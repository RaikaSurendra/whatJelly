package com.learning.jelly;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import java.io.File;

public class JellyExamplesRunner {
    
    public static void main(String[] args) {
        String[] examples = {
            "examples/01-hello-world.jelly",
            "examples/02-variables.jelly",
            "examples/03-conditionals.jelly",
            "examples/04-loops.jelly",
            "examples/05-xml-generation.jelly",
            "examples/06-custom-tags.jelly",
            "examples/07-file-operations.jelly",
            "examples/08-script-invocation.jelly",
            "examples/09-expressions.jelly",
            "examples/10-java-integration.jelly"
        };
        
        for (String example : examples) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("Running: " + example);
                System.out.println("=".repeat(60));
                
                JellyContext context = new JellyContext();
                XMLOutput output = XMLOutput.createXMLOutput(System.out);
                
                File scriptFile = new File(example);
                if (scriptFile.exists()) {
                    context.runScript(scriptFile, output);
                    output.flush();
                } else {
                    System.out.println("Script not found: " + example);
                }
                
                System.out.println("\n");
                
            } catch (Exception e) {
                System.err.println("Error running " + example + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("All examples completed!");
        System.out.println("=".repeat(60));
    }
}
