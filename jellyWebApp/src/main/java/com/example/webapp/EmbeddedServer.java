package com.example.webapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Embedded Jetty server to run the application
 */
public class EmbeddedServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        Server server = new Server(PORT);
        
        try {
            // Create servlet context handler (simpler than WebAppContext)
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.setResourceBase("src/main/webapp");
            
            // Add Jelly servlet
            ServletHolder jellyServlet = new ServletHolder("jelly", new JellyServlet());
            context.addServlet(jellyServlet, "*.jelly");
            context.addServlet(jellyServlet, "/");
            
            server.setHandler(context);
            
            // Start server
            server.start();
            
            System.out.println("======================================");
            System.out.println("Jelly Web Application Started");
            System.out.println("======================================");
            System.out.println("Server running at: http://localhost:" + PORT);
            System.out.println("");
            System.out.println("Available pages:");
            System.out.println("  http://localhost:" + PORT + "/");
            System.out.println("  http://localhost:" + PORT + "/users.jelly");
            System.out.println("  http://localhost:" + PORT + "/products.jelly");
            System.out.println("  http://localhost:" + PORT + "/dashboard.jelly");
            System.out.println("  http://localhost:" + PORT + "/admin.jelly");
            System.out.println("");
            System.out.println("Press Ctrl+C to stop");
            System.out.println("======================================");
            
            // Wait for server to finish
            server.join();
            
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            // Shutdown database
            DatabaseManager.shutdown();
        }
    }
}
