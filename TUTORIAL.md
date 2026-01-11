# Apache Jelly Tutorial

## Understanding Apache Jelly

### What Makes Jelly Unique?

Apache Jelly is an **XML-based scripting engine** that bridges XML and Java. Unlike XSLT (which transforms XML to XML/HTML), Jelly:
- Executes Java code through XML tags
- Provides control flow (if/else, loops) in XML
- Dynamically generates any output format
- Integrates seamlessly with Java applications

### Core Architecture

```
┌─────────────────┐
│  Jelly Script   │  (.jelly file - XML with special tags)
│   (XML file)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Jelly Parser   │  (Parses XML, builds tag tree)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ JellyContext    │  (Execution environment, variables)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Tag Execution  │  (Each tag executes in sequence)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  XMLOutput      │  (Output stream - can be file, stdout, etc)
└─────────────────┘
```

## Key Components Explained

### 1. Jelly Script Structure

Every Jelly script is valid XML with namespace declarations:

```xml
<?xml version="1.0"?>
<j:jelly xmlns:j="jelly:core">
  <!-- Your tags here -->
</j:jelly>
```

**Key points:**
- Root element: `<j:jelly>`
- Namespace prefix: `xmlns:j="jelly:core"`
- All tags must be properly closed
- Can include multiple namespaces for different tag libraries

### 2. JellyContext

The **JellyContext** is the execution environment:

```java
JellyContext context = new JellyContext();
context.setVariable("myVar", "Hello");  // Set from Java
context.runScript(file, output);
```

**Features:**
- Stores variables (mutable key-value map)
- Scoped (parent/child contexts)
- Shared across script invocations
- Accessible from Java code

### 3. Tag Libraries

Tag libraries are collections of related tags:

| Library | Namespace | Purpose | Example Tags |
|---------|-----------|---------|--------------|
| **core** | `jelly:core` | Control flow, variables | `<j:if>`, `<j:forEach>`, `<j:set>` |
| **xml** | `jelly:xml` | XML processing | `<x:parse>`, `<x:set>`, `<x:forEach>` |
| **define** | `jelly:define` | Custom tags | `<define:tag>`, `<define:attribute>` |
| **log** | `jelly:log` | Logging | `<log:info>`, `<log:error>` |
| **sql** | `jelly:sql` | Database | `<sql:query>`, `<sql:update>` |

### 4. Expressions (JEXL)

Jelly uses **JEXL** (Java Expression Language) for expressions:

```xml
${variableName}              <!-- Variable reference -->
${user.name}                 <!-- Property access -->
${list[0]}                   <!-- Array/list access -->
${a + b}                     <!-- Arithmetic -->
${x > 10 ? 'big' : 'small'} <!-- Ternary operator -->
${object.method()}           <!-- Method calls -->
```

**JEXL Features:**
- Arithmetic: `+`, `-`, `*`, `/`, `%`
- Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Logical: `and`, `or`, `not`
- String operations: concatenation, methods
- Java object access: properties, methods, constructors

## Tag Reference

### Core Tags

#### `<j:set>` - Set Variables
```xml
<j:set var="name" value="Alice"/>
<j:set var="count" value="42"/>
<j:set var="result" value="${x + y}"/>
```

#### `<j:out>` - Output Values
```xml
<j:out value="Hello, World!"/>
<j:out value="${userName}"/>
```

#### `<j:if>` - Conditional
```xml
<j:if test="${score >= 60}">
  <j:out value="Pass"/>
</j:if>
```

#### `<j:choose>/<j:when>/<j:otherwise>` - Multiple Conditions
```xml
<j:choose>
  <j:when test="${grade == 'A'}">Excellent</j:when>
  <j:when test="${grade == 'B'}">Good</j:when>
  <j:otherwise>Needs improvement</j:otherwise>
</j:choose>
```

#### `<j:forEach>` - Iteration
```xml
<!-- Iterate over collection -->
<j:forEach var="item" items="${list}">
  <j:out value="${item}"/>
</j:forEach>

<!-- Numeric range -->
<j:forEach var="i" begin="1" end="10">
  <j:out value="${i}"/>
</j:forEach>
```

#### `<j:while>` - Loop While True
```xml
<j:while test="${counter < 10}">
  <j:out value="${counter}"/>
  <j:set var="counter" value="${counter + 1}"/>
</j:while>
```

#### `<j:scope>` - Variable Scope
```xml
<j:scope>
  <j:set var="localVar" value="scoped"/>
  <!-- localVar only exists within this scope -->
</j:scope>
```

## Common Patterns

### Pattern 1: Code Generation

Generate Java classes from data:

```xml
<j:forEach var="field" items="${fields}">
  private ${field.type} ${field.name};
  
  public ${field.type} get${field.name.capitalize()}() {
    return ${field.name};
  }
</j:forEach>
```

### Pattern 2: Configuration Templates

```xml
<j:set var="env" value="${System.getenv('ENV')}"/>
<config>
  <database>
    <j:choose>
      <j:when test="${env == 'prod'}">
        <url>jdbc:mysql://prod-db:3306/app</url>
      </j:when>
      <j:otherwise>
        <url>jdbc:mysql://localhost:3306/app_dev</url>
      </j:otherwise>
    </j:choose>
  </database>
</config>
```

### Pattern 3: Report Generation

```xml
<report>
  <summary>Total: ${items.size()} items</summary>
  <items>
    <j:forEach var="item" items="${items}">
      <item id="${item.id}">
        <name>${item.name}</name>
        <status>${item.status}</status>
      </item>
    </j:forEach>
  </items>
</report>
```

## Best Practices

### 1. Keep Logic in Java
✅ **Good:** Use Java for complex logic, Jelly for presentation
```java
context.setVariable("processedData", processData(rawData));
```
```xml
<j:forEach var="item" items="${processedData}">
  <j:out value="${item}"/>
</j:forEach>
```

❌ **Bad:** Complex calculations in Jelly
```xml
<j:set var="result" value="${((a * b) + (c / d)) - (e % f)}"/>
```

### 2. Use Descriptive Variable Names
✅ **Good:**
```xml
<j:set var="totalSalesAmount" value="${sales.total}"/>
<j:set var="activeCustomerCount" value="${customers.size()}"/>
```

❌ **Bad:**
```xml
<j:set var="x" value="${sales.total}"/>
<j:set var="n" value="${customers.size()}"/>
```

### 3. Modularize with Custom Tags
✅ **Good:**
```xml
<define:tag name="formatCurrency">
  <j:out value="$${String.format('%.2f', amount)}"/>
</define:tag>
```

### 4. Handle Nulls Gracefully
```xml
<j:if test="${user != null}">
  <j:out value="Hello, ${user.name}"/>
</j:if>

<!-- Or use ternary -->
<j:out value="${user != null ? user.name : 'Guest'}"/>
```

## When to Use Jelly

### ✅ Good Use Cases:
- **Code generation** from templates
- **Build scripts** with dynamic logic
- **Report generation** with structured data
- **Configuration files** with conditional sections
- **XML transformation** with imperative logic

### ❌ When NOT to Use:
- **Web applications** (use modern frameworks)
- **Complex business logic** (use Java directly)
- **Performance-critical** code (interpreted, not compiled)
- **Simple XML transforms** (use XSLT instead)

## Debugging Tips

### 1. Use Log Tags
```xml
<j:jelly xmlns:j="jelly:core" xmlns:log="jelly:log">
  <log:info>Starting process</log:info>
  <log:debug>Variable value: ${myVar}</log:debug>
</j:jelly>
```

### 2. Output Debug Information
```xml
<j:out value="DEBUG: count=${count}, total=${total}"/>
```

### 3. Check Variable Values
```java
// In Java
System.out.println("Context variables: " + context.getVariables());
```

## Performance Considerations

1. **Cache parsed scripts** - Don't re-parse on every execution
2. **Minimize context variables** - Only store what's needed
3. **Use Java for heavy lifting** - Jelly is for templating
4. **Avoid deep nesting** - Flatten when possible

## Comparison with Other Technologies

| Feature | Jelly | XSLT | JSP | Velocity |
|---------|-------|------|-----|----------|
| XML-based | ✅ | ✅ | ❌ | ❌ |
| Java integration | ✅ | ❌ | ✅ | ✅ |
| Control flow | ✅ | Limited | ✅ | ✅ |
| Learning curve | Medium | High | Medium | Low |
| Use case | Code gen, builds | XML transform | Web pages | Templates |

## Next Steps

1. Run the examples: `mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyExamplesRunner"`
2. Experiment with custom tags in `examples/06-custom-tags.jelly`
3. Try creating your own code generator
4. Explore other tag libraries (SQL, HTTP, etc.)
