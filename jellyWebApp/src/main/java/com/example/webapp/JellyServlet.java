package com.example.webapp;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

/**
 * Main servlet that processes Jelly templates
 */
public class JellyServlet extends HttpServlet {
    private static final boolean DEBUG = true;
    private static final String PAGES_DIR = "/pages/";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String pathInfo = requestURI;
        
        // Default to index if root
        if (pathInfo == null || pathInfo.equals("/")) {
            pathInfo = "index.jelly";
        } else if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1); // Remove leading slash
        }
        
        // Add .jelly extension if not present
        if (!pathInfo.endsWith(".jelly")) {
            pathInfo = pathInfo + ".jelly";
        }
        
        if (DEBUG) {
            System.out.println("Processing request: " + requestURI + " -> " + pathInfo);
        }
        
        try {
            // Get Jelly template file
            String jellyPath = PAGES_DIR + pathInfo;
            URL jellyUrl = getServletContext().getResource(jellyPath);
            
            if (jellyUrl == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "Jelly template not found: " + jellyPath);
                return;
            }
            
            // Create Jelly context
            JellyContext context = new JellyContext();
            
            // Register custom tag library
            context.registerTagLibrary("app", new com.example.webapp.tags.AppTagLibrary());
            
            // Add request attributes to context
            context.setVariable("request", request);
            context.setVariable("response", response);
            context.setVariable("session", request.getSession());
            
            // Add request parameters
            Map<String, String[]> paramMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values.length == 1) {
                    context.setVariable(key, values[0]);
                } else {
                    context.setVariable(key, values);
                }
            }
            
            // Compile and run Jelly script
            Script script = context.compileScript(jellyUrl);
            
            // Create output
            StringWriter stringWriter = new StringWriter();
            XMLOutput output = XMLOutput.createXMLOutput(stringWriter);
            
            // Execute script
            script.run(context, output);
            output.flush();
            
            // Send response
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(stringWriter.toString());
            out.flush();
            
            if (DEBUG) {
                System.out.println("Successfully processed: " + pathInfo);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing Jelly template: " + e.getMessage());
            e.printStackTrace();
            
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Error Processing Jelly Template</h1>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
            out.println("</body></html>");
        }
    }
}
