# Jelly Web Application - Interactive Tutorial

A hands-on, chapter-based tutorial for learning Apache Jelly in a web application context. Learn by doing with progressive examples, exercises, and real database integration.

## 🎓 Learning Approach

This isn't just a demo - it's a **complete interactive tutorial** with:
- **10 structured chapters** from beginner to advanced
- **Hands-on exercises** with solutions
- **Progressive difficulty** building on previous concepts
- **Real database operations** with H2
- **Custom tag libraries** similar to ServiceNow's implementation

## Overview

This project demonstrates how to build a web framework similar to what ServiceNow did with Jelly:
- **Servlet-based architecture** - Java servlets process requests
- **Jelly templating** - Dynamic page generation
- **Custom tag library** - Application-specific tags (SQL tags)
- **Database integration** - H2 in-memory database
- **Customizable pages** - Jelly templates can be modified

## Architecture

```
Browser Request
    ↓
JellyServlet (processes .jelly files)
    ↓
Jelly Engine + Custom Tags
    ↓
├── SQL Tags (query database)
├── App Tags (app-specific)
└── Standard Jelly Tags
    ↓
HTML Response → Browser
```

## Project Structure

```
jellyWebApp/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/webapp/
│       │       ├── EmbeddedServer.java       # Jetty embedded server
│       │       ├── JellyServlet.java         # Main servlet
│       │       ├── DatabaseManager.java      # Database setup
│       │       └── tags/
│       │           ├── SqlQueryTag.java      # Custom SQL query tag
│       │           ├── SqlUpdateTag.java     # Custom SQL update tag
│       │           └── AppTagLibrary.java    # Tag library definition
│       ├── resources/
│       │   └── init.sql                      # Database initialization
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml                   # Servlet configuration
│           └── pages/
│               ├── index.jelly               # Home page
│               ├── users.jelly               # User list
│               ├── products.jelly            # Product catalog
│               ├── dashboard.jelly           # Dashboard with stats
│               └── admin.jelly               # Admin page
└── run.sh                                     # Quick start script
```

## Features

### Custom SQL Tags

This app provides custom tags for database operations:

```xml
<!-- Query database -->
<app:sqlQuery var="users" table="users">
  SELECT * FROM users WHERE active = true
</app:sqlQuery>

<!-- Insert record -->
<app:sqlUpdate>
  INSERT INTO users (name, email) VALUES ('John', 'john@example.com')
</app:sqlUpdate>

<!-- Update record -->
<app:sqlUpdate>
  UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ${userId}
</app:sqlUpdate>
```

### Standard Jelly Tags

All standard Jelly tags work:

```xml
<j:forEach var="user" items="${users}">
  <tr>
    <td>${user.name}</td>
    <td>${user.email}</td>
  </tr>
</j:forEach>
```

### Database

Uses H2 in-memory database with sample tables:
- **users** - User accounts
- **products** - Product catalog
- **orders** - Order history
- **categories** - Product categories

## Quick Start

### Option 1: Docker (Recommended) 🐳

```bash
cd jellyWebApp

# Using Docker Compose
docker-compose up -d

# Or using Docker directly
docker build -t jelly-webapp .
docker run -d -p 8080:8080 jelly-webapp
```

**See [DOCKER.md](DOCKER.md) for complete Docker documentation.**

### Option 2: Local Maven Build

#### 1. Build the project

```bash
cd jellyWebApp
mvn clean package
```

#### 2. Run the embedded server

```bash
mvn exec:java
```

Or use the convenience script:

```bash
./run.sh
```

### 3. Start Learning!

Open browser to: **http://localhost:8080/**

## 📚 Tutorial Chapters

Follow the chapters in order for the best learning experience:

1. **[Chapter 1: Hello Jelly Web](http://localhost:8080/chapter1.jelly)** - Basics of Jelly in web context
2. **[Chapter 2: Database Integration](http://localhost:8080/chapter2.jelly)** - Custom SQL tags
3. **[Chapter 3: Dynamic Lists](http://localhost:8080/chapter3.jelly)** - Coming soon!
4. **[Chapter 4: Forms & Data](http://localhost:8080/chapter4.jelly)** - Coming soon!
5. **[Chapter 5: Advanced Queries](http://localhost:8080/chapter5.jelly)** - Coming soon!
6. **[Chapter 6: Custom Tags](http://localhost:8080/chapter6.jelly)** - Coming soon!
7. **[Chapter 7: Building Dashboards](http://localhost:8080/chapter7.jelly)** - Coming soon!
8. **[Chapter 8: Authentication](http://localhost:8080/chapter8.jelly)** - Coming soon!
9. **[Chapter 9: Real-World App](http://localhost:8080/chapter9.jelly)** - Coming soon!
10. **[Chapter 10: Best Practices](http://localhost:8080/chapter10.jelly)** - Coming soon!

**See [CHAPTERS.md](CHAPTERS.md) for detailed learning objectives and exercises.**

## 🎯 Demo Pages

Explore complete examples:
- http://localhost:8080/ - Home page
- http://localhost:8080/users.jelly - User list
- http://localhost:8080/products.jelly - Product catalog
- http://localhost:8080/dashboard.jelly - Analytics dashboard
- http://localhost:8080/admin.jelly - Admin interface

## How It Works

### 1. Request Flow

1. Browser requests `users.jelly`
2. `JellyServlet` intercepts `.jelly` extension
3. Servlet loads Jelly template from `webapp/pages/`
4. Jelly engine processes template with custom tags
5. Custom SQL tags query the database
6. Results are rendered as HTML
7. HTML sent back to browser

### 2. Custom Tag Processing

```java
// In SqlQueryTag.java
public void doTag(XMLOutput output) throws Exception {
    // Execute SQL query
    ResultSet rs = statement.executeQuery(sql);
    
    // Store results in context
    context.setVariable(var, resultList);
    
    // Process child tags
    invokeBody(output);
}
```

### 3. Database Connection

```java
// DatabaseManager provides connection pooling
Connection conn = DatabaseManager.getConnection();
// Use connection
conn.close(); // Returns to pool
```

## Customizing Pages

### Edit Jelly Templates

Templates are in `src/main/webapp/pages/`. Edit and refresh browser (no restart needed in dev mode).

### Create New Page

1. Create `mypage.jelly` in `webapp/pages/`
2. Use custom tags and standard Jelly
3. Access at `http://localhost:8080/mypage.jelly`

Example:

```xml
<?xml version="1.0"?>
<j:jelly xmlns:j="jelly:core" xmlns:app="app">
  <html>
    <head><title>My Page</title></head>
    <body>
      <h1>My Custom Page</h1>
      
      <app:sqlQuery var="data" table="products">
        SELECT * FROM products WHERE price &lt; 100
      </app:sqlQuery>
      
      <ul>
        <j:forEach var="item" items="${data}">
          <li>${item.name} - $${item.price}</li>
        </j:forEach>
      </ul>
    </body>
  </html>
</j:jelly>
```

## Creating Custom Tags

### 1. Create Tag Class

```java
package com.example.webapp.tags;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

public class MyCustomTag extends TagSupport {
    private String param;
    
    public void setParam(String param) {
        this.param = param;
    }
    
    @Override
    public void doTag(XMLOutput output) throws Exception {
        // Tag logic here
        output.write("Result: " + param);
    }
}
```

### 2. Register in TagLibrary

```java
public class AppTagLibrary extends TagLibrary {
    public AppTagLibrary() {
        registerTag("myTag", MyCustomTag.class);
    }
}
```

### 3. Use in Jelly

```xml
<app:myTag param="value"/>
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100),
    active BOOLEAN,
    created_at TIMESTAMP
);
```

### Products Table

```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2),
    category_id INT,
    stock INT
);
```

## Configuration

### Database

Edit `DatabaseManager.java` to change database settings:

```java
private static final String DB_URL = "jdbc:h2:mem:testdb";
private static final String DB_USER = "sa";
private static final String DB_PASSWORD = "";
```

### Server Port

Edit `EmbeddedServer.java`:

```java
Server server = new Server(8080); // Change port here
```

### Jelly Pages Location

Edit `JellyServlet.java`:

```java
private static final String PAGES_DIR = "/pages/";
```

## Development Tips

### Hot Reload

In development mode, Jelly templates are recompiled on each request. Just edit and refresh.

### Debugging

Enable debug logging in `JellyServlet`:

```java
private static final boolean DEBUG = true;
```

### Database Console

Access H2 console at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (empty)

## Security Considerations

**Note**: This is a demonstration project. For production:

1. **SQL Injection** - Use prepared statements
2. **XSS Prevention** - Escape all user input
3. **Authentication** - Add user authentication
4. **Authorization** - Implement role-based access
5. **HTTPS** - Use SSL/TLS
6. **Input Validation** - Validate all inputs
7. **Connection Pooling** - Already implemented
8. **Error Handling** - Don't expose stack traces

## Advantages of This Approach

### ✅ Pros
- **Dynamic templating** - No compilation needed
- **Customizable** - Templates can be modified at runtime
- **Extensible** - Easy to add custom tags
- **Familiar syntax** - XML-based, easy to learn
- **Database integration** - Direct SQL in templates

### ❌ Cons
- **Performance** - Slower than compiled templates
- **Security** - SQL in templates can be risky
- **Debugging** - Harder to debug than Java code
- **Modern alternatives** - Better options exist today

## Modern Alternatives

For production applications, consider:
- **Thymeleaf** - Modern template engine
- **FreeMarker** - Mature and fast
- **Spring MVC + JSP** - Traditional Java web
- **React/Vue/Angular** - Modern SPAs with REST APIs

## Learning Objectives

This project demonstrates:
1. How to integrate Jelly into a web application
2. Creating custom tag libraries
3. Database-driven dynamic content
4. Servlet-based request processing
5. Template engine architecture
6. Similar patterns to what ServiceNow uses

## Troubleshooting

### Port Already in Use

```bash
# Change port in EmbeddedServer.java or kill process
lsof -ti:8080 | xargs kill -9
```

### Database Connection Errors

Check `DatabaseManager` initialization and H2 dependency.

### Jelly Parse Errors

Check XML well-formedness and tag syntax.

### Custom Tags Not Found

Verify namespace declaration and tag library registration.

## Next Steps

1. Add authentication/authorization
2. Implement CRUD operations
3. Add form handling
4. Create more custom tags
5. Add CSS/JavaScript assets
6. Implement sessions
7. Add error pages

## Resources

- [Apache Jelly Documentation](https://commons.apache.org/proper/commons-jelly/)
- [Servlet API Documentation](https://javaee.github.io/servlet-spec/)
- [H2 Database Documentation](https://h2database.com/)
- [Jetty Documentation](https://www.eclipse.org/jetty/documentation/)

---

**Built with**: Java 11, Apache Jelly, Jetty, H2 Database
