# Understanding TagSupport and Tag Libraries in Jelly

A deep dive into how custom tags work in Apache Jelly using the SqlUpdateTag example.

## 🏗️ The Architecture

### 1. **TagSupport Base Class**

```java
public class SqlUpdateTag extends TagSupport {
    // Your custom tag implementation
}
```

**TagSupport** is the foundation class from Apache Jelly that provides:
- Access to the **JellyContext** (via `context` field)
- Methods to read tag body content (`getBodyText()`)
- Lifecycle management
- Parent-child tag relationships
- Output writing capabilities

### 2. **Tag Lifecycle**

When Jelly processes `<app:sqlUpdate>`, here's what happens:

```
1. XML Parser encounters <app:sqlUpdate>
   ↓
2. Jelly looks up "sqlUpdate" in "app" namespace
   ↓
3. Creates new SqlUpdateTag() instance
   ↓
4. Calls setter methods for attributes (setVar())
   ↓
5. Sets the context (inherited from TagSupport)
   ↓
6. Calls doTag(XMLOutput output)
   ↓
7. Tag executes its logic
   ↓
8. Tag is discarded after execution
```

## 📝 How SqlUpdateTag Works

### Step-by-Step Breakdown

```java
public class SqlUpdateTag extends TagSupport {
    private String var;  // 1. Store attribute value
    
    // 2. Setter called automatically for 'var' attribute
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        // 3. Main execution happens here
        
        // Access tag body content (between opening and closing tags)
        String sql = getBodyText();
        
        // Execute your custom logic
        int rowsAffected = executeUpdate(sql);
        
        // Access JellyContext (provided by TagSupport)
        context.setVariable(var, rowsAffected);
    }
}
```

### What TagSupport Provides

```java
public abstract class TagSupport {
    protected JellyContext context;      // Execution context
    protected XMLOutput output;          // Output stream
    protected Tag parent;                // Parent tag (for nesting)
    
    // Methods you can use:
    protected String getBodyText();      // Get content between tags
    protected void invokeBody(XMLOutput output);  // Execute child tags
    public JellyContext getContext();    // Access context
    public Tag getParent();              // Access parent tag
}
```

## 🏷️ Tag Library Registration

### 1. **AppTagLibrary.java**

```java
public class AppTagLibrary extends TagLibrary {
    public AppTagLibrary() {
        // Register tag: XML name → Java class
        registerTag("sqlQuery", SqlQueryTag.class);
        registerTag("sqlUpdate", SqlUpdateTag.class);
        registerTag("sqlExecute", SqlExecuteTag.class);
        registerTag("formatDate", FormatDateTag.class);
        registerTag("formatNumber", FormatNumberTag.class);
    }
}
```

**This creates the mapping**:
- XML: `<app:sqlUpdate>` → Java: `SqlUpdateTag.class`
- XML: `<app:sqlQuery>` → Java: `SqlQueryTag.class`

### 2. **Tag Library Descriptor (jelly.tld)**

```xml
<taglib>
    <short-name>app</short-name>
    <uri>app</uri>
    
    <tag>
        <name>sqlUpdate</name>
        <tag-class>com.example.webapp.tags.SqlUpdateTag</tag-class>
        <attribute>
            <name>var</name>
            <required>false</required>
        </attribute>
    </tag>
</taglib>
```

This tells the servlet container about:
- Tag name
- Java class
- Attributes and their requirements

## 🔄 Complete Flow Example

### In Your Jelly Page:

```xml
<j:jelly xmlns:j="jelly:core" xmlns:app="app">
    <app:sqlUpdate var="rows">
        UPDATE users SET active = TRUE WHERE id = 1
    </app:sqlUpdate>
    
    <p>Rows affected: ${rows}</p>
</j:jelly>
```

### What Happens:

**1. Namespace Resolution**
```
xmlns:app="app" → Look for TagLibrary with URI "app"
                → Finds AppTagLibrary
```

**2. Tag Creation**
```
<app:sqlUpdate var="rows"> → registerTag("sqlUpdate", SqlUpdateTag.class)
                           → new SqlUpdateTag()
```

**3. Attribute Setting**
```
var="rows" → sqlUpdateTag.setVar("rows")
```

**4. Context Injection**
```
TagSupport provides: sqlUpdateTag.context = currentJellyContext
```

**5. Tag Body Capture**
```
UPDATE users... → Stored as body text
```

**6. Execution**
```java
doTag(output) is called:
  - getBodyText() returns "UPDATE users..."
  - executeUpdate(sql) runs the SQL
  - context.setVariable("rows", 1) stores result
```

**7. Variable Access**
```
${rows} → Looks up "rows" in context → Returns 1
```

## 🎯 Key Concepts

### 1. **Context is Shared**

All tags in the same execution share one JellyContext:

```java
// In SqlUpdateTag
context.setVariable("rows", 1);

// In another tag or expression
${rows}  // Accesses the same context
```

### 2. **Attribute Mapping is Automatic**

```xml
<app:sqlUpdate var="myVar" timeout="30">
```

Jelly automatically calls:
```java
tag.setVar("myVar");
tag.setTimeout(30);
```

(You'd need to add `setTimeout()` setter for this to work)

### 3. **Tag Composition**

Tags can be nested:

```java
@Override
public void doTag(XMLOutput output) {
    // Execute child tags
    invokeBody(output);
}
```

Example:
```xml
<app:customTag>
    <j:if test="${condition}">
        <app:sqlQuery var="data">
            SELECT * FROM table
        </app:sqlQuery>
    </j:if>
</app:customTag>
```

### 4. **Output Writing**

```java
@Override
public void doTag(XMLOutput output) throws Exception {
    output.write("<p>Hello from tag!</p>");
}
```

## 🔍 Deep Dive: How JellyServlet Connects Everything

### In JellyServlet.java:

```java
public void processRequest(HttpServletRequest request, ...) {
    // 1. Create context
    JellyContext context = new JellyContext();
    
    // 2. Add request data to context
    context.setVariable("request", request);
    context.setVariable("session", request.getSession());
    
    // 3. Compile script (parses XML, creates tag instances)
    Script script = context.compileScript(jellyUrl);
    
    // 4. Run script (executes all tags)
    script.run(context, output);
}
```

### During Compilation:

```
1. XML Parser reads <app:sqlUpdate>
2. Looks up "app" namespace → Finds AppTagLibrary
3. Looks up "sqlUpdate" in library → Finds SqlUpdateTag.class
4. Creates tag instance tree (parent-child relationships)
5. Sets up attributes and context references
```

### During Execution:

```
1. Traverse tag tree in document order
2. For each tag, call doTag(output)
3. Tags execute their logic
4. Child tags are executed via invokeBody()
5. Output is collected
6. Final HTML is returned to browser
```

## 💡 Why This Design?

### Advantages:

1. **Separation of Concerns**
   - XML for structure/presentation
   - Java for logic/processing
   - Clean separation of responsibilities

2. **Reusability**
   - Write tag once, use anywhere
   - Like functions for templates
   - Build tag libraries for common operations

3. **Type Safety**
   - Java compilation catches errors early
   - Better than pure scripting
   - IDE support for tag development

4. **Extensibility**
   - Easy to add new tags
   - No core framework changes needed
   - Plugin architecture

5. **Composability**
   - Tags can nest and interact
   - Build complex behavior from simple parts
   - Declarative programming style

6. **Testability**
   - Tag logic can be unit tested
   - Mock JellyContext for testing
   - Isolate tag behavior

## 🎓 Creating Your Own Tag - Complete Example

### Step 1: Create Tag Class

```java
package com.example.webapp.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Custom tag to highlight text with a background color
 * 
 * Usage:
 * <app:highlight text="Important!" color="yellow"/>
 */
public class HighlightTag extends TagSupport {
    private String text;
    private String color;
    
    // Setter for 'text' attribute
    public void setText(String text) {
        this.text = text;
    }
    
    // Setter for 'color' attribute
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            // Default color if not specified
            String bgColor = color != null ? color : "yellow";
            
            // Generate HTML
            String style = String.format(
                "background-color: %s; padding: 2px 5px; border-radius: 3px;", 
                bgColor
            );
            
            String html = String.format(
                "<span style='%s'>%s</span>", 
                style, 
                text
            );
            
            // Write to output
            output.write(html);
            
        } catch (Exception e) {
            throw new JellyTagException("Error in HighlightTag", e);
        }
    }
}
```

### Step 2: Register in AppTagLibrary

```java
public class AppTagLibrary extends TagLibrary {
    public AppTagLibrary() {
        // Existing tags
        registerTag("sqlQuery", SqlQueryTag.class);
        registerTag("sqlUpdate", SqlUpdateTag.class);
        registerTag("sqlExecute", SqlExecuteTag.class);
        registerTag("formatDate", FormatDateTag.class);
        registerTag("formatNumber", FormatNumberTag.class);
        
        // New tag
        registerTag("highlight", HighlightTag.class);
    }
}
```

### Step 3: Add to Tag Library Descriptor (optional)

```xml
<tag>
    <name>highlight</name>
    <tag-class>com.example.webapp.tags.HighlightTag</tag-class>
    <body-content>empty</body-content>
    <attribute>
        <name>text</name>
        <required>true</required>
        <rtexprvalue>true</rtexprvalue>
    </attribute>
    <attribute>
        <name>color</name>
        <required>false</required>
        <rtexprvalue>true</rtexprvalue>
    </attribute>
</tag>
```

### Step 4: Use in Jelly Pages

```xml
<j:jelly xmlns:j="jelly:core" xmlns:app="app">
    <h1>Highlighted Text Example</h1>
    
    <!-- Basic usage -->
    <p>This is <app:highlight text="important" color="red"/> information.</p>
    
    <!-- With default color -->
    <p>This is <app:highlight text="highlighted"/> text.</p>
    
    <!-- With variables -->
    <j:set var="message" value="Dynamic content"/>
    <app:highlight text="${message}" color="lightblue"/>
</j:jelly>
```

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Jelly Page (.jelly)                      │
│  <j:jelly xmlns:app="app">                                  │
│    <app:sqlUpdate var="rows">                               │
│      UPDATE users SET active = TRUE WHERE id = 1            │
│    </app:sqlUpdate>                                         │
│    <p>Rows: ${rows}</p>                                     │
│  </j:jelly>                                                 │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    JellyServlet                             │
│  1. Creates JellyContext                                    │
│  2. Adds request/session to context                         │
│  3. Compiles Jelly script                                   │
│  4. Executes script                                         │
│  5. Returns HTML response                                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  JellyContext                               │
│  - Stores variables (rows, data, etc.)                      │
│  - Knows about tag libraries                                │
│  - Manages tag execution                                    │
└────────────────────────┬────────────────────────────────────┘
                         │
           ┌─────────────┴─────────────┐
           ▼                           ▼
┌──────────────────────┐    ┌──────────────────────┐
│  AppTagLibrary       │    │  Standard Libraries  │
│  registerTag(...)    │    │  jelly:core          │
│  - sqlUpdate         │    │  jelly:xml           │
│  - sqlQuery          │    │  etc.                │
│  - formatDate        │    │                      │
└──────────┬───────────┘    └──────────────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│                    SqlUpdateTag                             │
│  extends TagSupport                                         │
│                                                             │
│  1. setVar("rows") called                                   │
│  2. context injected by TagSupport                          │
│  3. doTag(output) executed                                  │
│     - getBodyText() returns SQL                             │
│     - executeUpdate(sql) runs                               │
│     - context.setVariable("rows", 1)                        │
│  4. Output written to XMLOutput                             │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Advanced Tag Features

### 1. **Accessing Parent Tags**

```java
@Override
public void doTag(XMLOutput output) throws JellyTagException {
    // Get parent tag
    Tag parent = getParent();
    
    if (parent instanceof CustomParentTag) {
        CustomParentTag parentTag = (CustomParentTag) parent;
        // Access parent tag's data
    }
}
```

### 2. **Processing Child Tags**

```java
@Override
public void doTag(XMLOutput output) throws JellyTagException {
    // Execute all child tags
    invokeBody(output);
    
    // Or capture child output
    StringWriter writer = new StringWriter();
    XMLOutput childOutput = XMLOutput.createXMLOutput(writer);
    invokeBody(childOutput);
    String childContent = writer.toString();
}
```

### 3. **Conditional Tag Processing**

```java
@Override
public void doTag(XMLOutput output) throws JellyTagException {
    if (condition) {
        // Only execute children if condition is true
        invokeBody(output);
    }
}
```

### 4. **Iterating Tag**

```java
@Override
public void doTag(XMLOutput output) throws JellyTagException {
    for (Object item : collection) {
        context.setVariable(varName, item);
        invokeBody(output);  // Execute children for each item
    }
}
```

## 🎯 Best Practices

### DO ✅

1. **Always validate inputs**
   ```java
   if (sql == null || sql.trim().isEmpty()) {
       throw new JellyTagException("SQL is required");
   }
   ```

2. **Close resources properly**
   ```java
   try (Connection conn = getConnection()) {
       // Use connection
   }  // Auto-closed
   ```

3. **Provide meaningful error messages**
   ```java
   throw new JellyTagException(
       "Error executing SQL: " + e.getMessage(), e
   );
   ```

4. **Use clear attribute names**
   ```java
   // Good: var, table, timeout
   // Bad: v, t, to
   ```

5. **Document your tags**
   ```java
   /**
    * Executes SQL UPDATE, INSERT, or DELETE statements.
    * 
    * @param var Variable name to store affected row count (optional)
    */
   ```

### DON'T ❌

1. **Don't store state between invocations**
   ```java
   // Bad - tags are recreated each time
   private static int counter = 0;
   ```

2. **Don't catch exceptions silently**
   ```java
   // Bad
   try {
       executeSQL();
   } catch (Exception e) {
       // Silent failure
   }
   ```

3. **Don't hardcode values**
   ```java
   // Bad
   String url = "jdbc:mysql://localhost:3306/db";
   
   // Good
   String url = context.getVariable("dbUrl");
   ```

4. **Don't bypass TagSupport helpers**
   ```java
   // Bad - accessing fields directly
   JellyContext ctx = this.context;
   
   // Good - using getters
   JellyContext ctx = getContext();
   ```

## 📚 Real-World Use Cases

### 1. **ServiceNow-style Tags**

```java
// <g:evaluate var="result">
//   var data = gs.getUser();
//   data.getName();
// </g:evaluate>
public class EvaluateTag extends TagSupport {
    private String var;
    
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        String script = getBodyText();
        Object result = evaluateJavaScript(script, context);
        if (var != null) {
            context.setVariable(var, result);
        }
    }
}
```

### 2. **Template Include Tag**

```java
// <app:include template="header.jelly"/>
public class IncludeTag extends TagSupport {
    private String template;
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        Script script = context.compileScript(template);
        script.run(context, output);
    }
}
```

### 3. **Cache Tag**

```java
// <app:cache key="userList" duration="300">
//   <app:sqlQuery var="users">
//     SELECT * FROM users
//   </app:sqlQuery>
// </app:cache>
public class CacheTag extends TagSupport {
    private String key;
    private int duration;
    
    // Setters...
    
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        Object cached = cache.get(key);
        if (cached != null) {
            output.write(cached.toString());
        } else {
            StringWriter writer = new StringWriter();
            XMLOutput tempOutput = XMLOutput.createXMLOutput(writer);
            invokeBody(tempOutput);
            String content = writer.toString();
            cache.put(key, content, duration);
            output.write(content);
        }
    }
}
```

## 🎓 Summary

**TagSupport** provides the infrastructure for custom tags:
- Context management
- Parent-child relationships
- Body content access
- Output writing

**Tag Libraries** provide the registration:
- Map XML names to Java classes
- Define tag attributes
- Group related tags

**Together** they enable:
- Powerful template customization
- Reusable components
- Clean separation of concerns
- Type-safe template logic

This architecture is what made Jelly powerful enough for ServiceNow to adopt it as their UI templating system! 🚀

## 📖 Further Reading

- [Apache Jelly Documentation](https://commons.apache.org/proper/commons-jelly/)
- `src/main/java/com/example/webapp/tags/` - Example tag implementations
- `CHAPTERS.md` - Tutorial chapters on using custom tags
- `chapter6.jelly` - Chapter on creating custom tags (when available)
