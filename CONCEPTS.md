# Apache Jelly Core Concepts Deep Dive

## 1. The Execution Model

### How Jelly Processes Scripts

```
┌─────────────────────────────────────────────────────────────┐
│ 1. PARSE PHASE                                              │
│    XML Parser reads .jelly file                             │
│    ↓                                                         │
│    Jelly Parser identifies tags and namespaces              │
│    ↓                                                         │
│    Tag tree structure created in memory                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. EXECUTION PHASE                                          │
│    JellyContext initialized (variable storage)              │
│    ↓                                                         │
│    Tags execute depth-first, in document order              │
│    ↓                                                         │
│    Each tag can: read/write variables, generate output      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. OUTPUT PHASE                                             │
│    XMLOutput stream collects all output                     │
│    ↓                                                         │
│    Output written to destination (file, stdout, etc)        │
└─────────────────────────────────────────────────────────────┘
```

### Tag Execution Order

```xml
<j:jelly xmlns:j="jelly:core">
  <!-- 1. This executes first -->
  <j:set var="a" value="1"/>
  
  <!-- 2. Then this -->
  <j:out value="${a}"/>
  
  <!-- 3. Tags execute depth-first -->
  <j:if test="${a == 1}">
    <!-- 4. Inner tags execute if condition true -->
    <j:set var="b" value="2"/>
  </j:if>
  
  <!-- 5. Finally this -->
  <j:out value="${b}"/>
</j:jelly>
```

## 2. JellyContext Deep Dive

### What is JellyContext?

The **JellyContext** is a mutable key-value store that:
- Holds all variables during script execution
- Supports nested/scoped contexts (parent-child relationships)
- Can be shared across multiple script invocations
- Provides Java integration (access to Java objects)

### Variable Scope

```xml
<j:jelly xmlns:j="jelly:core">
  <!-- Global scope -->
  <j:set var="global" value="I'm global"/>
  
  <!-- Create nested scope -->
  <j:scope>
    <!-- Can access parent variables -->
    <j:out value="${global}"/>
    
    <!-- Create scoped variable -->
    <j:set var="local" value="I'm local"/>
    <j:out value="${local}"/>
  </j:scope>
  
  <!-- local is not accessible here -->
  <!-- This would be null/undefined -->
  <j:out value="${local}"/>
</j:jelly>
```

### Java Code Example

```java
JellyContext context = new JellyContext();

// Set variables from Java
context.setVariable("userName", "Alice");
context.setVariable("count", 42);

// Run script - script can access these variables
context.runScript(file, output);

// Get variables set by script
Object result = context.getVariable("result");

// Variables persist across script runs
context.runScript(anotherFile, output); // Still has userName, count
```

## 3. Expression Language (JEXL)

### Expression Syntax

Jelly uses **JEXL (Java Expression Language)** for expressions:

```xml
<!-- Variable reference -->
${variableName}

<!-- Property access (calls getProperty()) -->
${object.property}
${user.name}

<!-- Method calls -->
${string.toUpperCase()}
${list.size()}
${Math.max(a, b)}

<!-- Array/List access -->
${array[0]}
${list[i]}

<!-- Map access -->
${map.key}
${map['key']}

<!-- Arithmetic -->
${a + b}
${x * y}
${10 / 3}

<!-- Comparison -->
${a > b}
${x == 10}
${y != null}

<!-- Logical -->
${a and b}
${x or y}
${not flag}

<!-- Ternary operator -->
${condition ? trueValue : falseValue}

<!-- Null-safe navigation -->
${object != null ? object.property : 'default'}
```

### JEXL vs Java

| Operation | JEXL Expression | Java Equivalent |
|-----------|----------------|-----------------|
| Addition | `${a + b}` | `a + b` |
| Method call | `${str.length()}` | `str.length()` |
| Property | `${user.name}` | `user.getName()` |
| Ternary | `${x > 0 ? 'pos' : 'neg'}` | `x > 0 ? "pos" : "neg"` |
| Logical AND | `${a and b}` | `a && b` |
| New object | `${new java.util.Date()}` | `new java.util.Date()` |

## 4. Tag Libraries Architecture

### How Tag Libraries Work

Each tag library is a collection of Java classes:

```
jelly:core namespace
    ↓
Commons-jelly-tags-core JAR
    ↓
Contains Tag classes:
    - SetTag.java (implements <j:set>)
    - IfTag.java (implements <j:if>)
    - ForEachTag.java (implements <j:forEach>)
    etc.
```

### Using Multiple Tag Libraries

```xml
<j:jelly 
    xmlns:j="jelly:core"         
    xmlns:x="jelly:xml"          
    xmlns:log="jelly:log"        
    xmlns:define="jelly:define">
  
  <!-- Core tags -->
  <j:set var="data" value="test"/>
  
  <!-- XML tags -->
  <x:parse var="doc">
    <root><item>value</item></root>
  </x:parse>
  
  <!-- Log tags -->
  <log:info>Processing ${data}</log:info>
  
  <!-- Define tags -->
  <define:tag name="custom">
    <!-- Custom tag logic -->
  </define:tag>
  
</j:jelly>
```

## 5. Common Patterns

### Pattern: Template with Data Injection

**Problem:** Generate output from data structure

```java
// Java code
JellyContext context = new JellyContext();
List<User> users = getUsersFromDatabase();
context.setVariable("users", users);
context.runScript("template.jelly", output);
```

```xml
<!-- template.jelly -->
<j:forEach var="user" items="${users}">
  User: ${user.name} (${user.email})
</j:forEach>
```

### Pattern: Configuration Builder

**Problem:** Generate config files for different environments

```xml
<j:set var="env" value="${System.getProperty('env', 'dev')}"/>

<configuration>
  <database>
    <j:choose>
      <j:when test="${env == 'prod'}">
        <host>prod-db.example.com</host>
        <replication>true</replication>
      </j:when>
      <j:otherwise>
        <host>localhost</host>
        <replication>false</replication>
      </j:otherwise>
    </j:choose>
  </database>
</configuration>
```

### Pattern: Code Generation Loop

**Problem:** Generate repetitive code structures

```xml
<j:forEach var="field" items="${fields}">
  private ${field.type} ${field.name};
  
  public ${field.type} get${field.capitalizedName}() {
    return this.${field.name};
  }
  
  public void set${field.capitalizedName}(${field.type} value) {
    this.${field.name} = value;
  }
</j:forEach>
```

## 6. Performance Considerations

### Script Compilation

Jelly scripts are **interpreted**, not compiled:
- Scripts parsed every time (unless cached)
- No bytecode generation
- Slower than native Java

### Optimization Tips

1. **Cache parsed scripts** in production:
   ```java
   Script script = context.compileScript(file);
   script.run(context, output); // Reuse compiled script
   ```

2. **Do heavy work in Java**, not Jelly:
   ```java
   // Good: Process in Java
   List<ProcessedData> data = processData(rawData);
   context.setVariable("data", data);
   
   // Bad: Complex logic in Jelly
   // ${complexCalculation(a, b, c, d, e)}
   ```

3. **Minimize variable lookups**:
   ```xml
   <!-- Good: Store once -->
   <j:set var="size" value="${list.size()}"/>
   <j:forEach var="i" begin="0" end="${size}">
   
   <!-- Bad: Lookup every iteration -->
   <j:forEach var="i" begin="0" end="${list.size()}">
   ```

## 7. Common Pitfalls

### Pitfall 1: XML Escaping

```xml
<!-- Wrong: Special chars not escaped -->
<j:set var="text" value="A & B"/>

<!-- Correct: Use CDATA or entity refs -->
<j:set var="text" value="A &amp; B"/>
<j:set var="text"><![CDATA[A & B]]></j:set>
```

### Pitfall 2: Variable Scope Confusion

```xml
<j:if test="${condition}">
  <j:set var="result" value="yes"/>
</j:if>

<!-- result might be null if condition is false -->
<j:out value="${result}"/> 
```

**Fix:** Initialize variables before conditional blocks

```xml
<j:set var="result" value="no"/>
<j:if test="${condition}">
  <j:set var="result" value="yes"/>
</j:if>
<j:out value="${result}"/> <!-- Always defined -->
```

### Pitfall 3: Expression Evaluation

```xml
<!-- This outputs "10" + "20" = "1020" (string concat) -->
<j:set var="a" value="10"/>
<j:set var="b" value="20"/>
<j:out value="${a + b}"/>

<!-- Fix: Use numeric types -->
<j:set var="a" value="${10}"/>
<j:set var="b" value="${20}"/>
<j:out value="${a + b}"/> <!-- Outputs 30 -->
```

## 8. Comparison with Alternatives

### Jelly vs XSLT

| Feature | Jelly | XSLT |
|---------|-------|------|
| Paradigm | Imperative | Declarative |
| Learning curve | Medium | High |
| Java integration | Excellent | Limited |
| XML transformation | Good | Excellent |
| General purpose | Yes | No (XML-focused) |

### Jelly vs Velocity/Freemarker

| Feature | Jelly | Velocity | Freemarker |
|---------|-------|----------|------------|
| Syntax | XML | Custom | Custom |
| Type safety | Weak | Weak | Weak |
| Java integration | Excellent | Good | Good |
| Use case | Build/codegen | Templates | Templates |
| Popularity | Low | Medium | High |

## 9. Real-World Use Cases

### Maven (Historical)

Early Maven versions used Jelly for plugin scripts:
```xml
<maven>
  <preGoal name="java:compile">
    <ant:echo>Compiling Java sources...</ant:echo>
  </preGoal>
</maven>
```

### Code Generators

Generate boilerplate code:
- DAOs from database schema
- REST endpoints from specifications
- Test fixtures from templates

### Build Automation

Dynamic build scripts with conditionals:
- Environment-specific builds
- Feature toggles during compilation
- Dynamic dependency management

### Report Generation

Create formatted reports:
- Test result summaries
- Metrics dashboards
- Build status reports
