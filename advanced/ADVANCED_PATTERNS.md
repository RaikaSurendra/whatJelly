# Advanced Jelly Patterns

This document covers design patterns and best practices for building complex applications with Apache Jelly.

## Table of Contents
1. [Fragment Composition Pattern](#fragment-composition-pattern)
2. [Context Inheritance Pattern](#context-inheritance-pattern)
3. [Template Method Pattern](#template-method-pattern)
4. [Builder Pattern](#builder-pattern)
5. [Strategy Pattern](#strategy-pattern)

---

## Fragment Composition Pattern

### Problem
You need to build complex outputs from reusable pieces without code duplication.

### Solution
Define script fragments as variables and compose them dynamically.

### Example
```xml
<!-- Define fragments -->
<j:set var="header">
  <j:scope>
    <j:out value="Header: ${title}"/>
  </j:scope>
</j:set>

<j:set var="body">
  <j:scope>
    <j:out value="Body: ${content}"/>
  </j:scope>
</j:set>

<!-- Compose -->
<j:set var="title" value="My Document"/>
<j:set var="content" value="Document content"/>
${header}
${body}
```

### Benefits
- Reusability across scripts
- Easier maintenance
- Cleaner separation of concerns

### Use Cases
- Template generation
- Code scaffolding
- Report building

---

## Context Inheritance Pattern

### Problem
You need to share data across multiple script invocations while maintaining isolation.

### Solution
Use parent-child context relationships with proper scoping.

### Example
```xml
<!-- Global context -->
<j:set var="config" value="production"/>

<j:scope>
  <!-- Child can read parent -->
  <j:out value="Config: ${config}"/>
  
  <!-- Child sets local variable -->
  <j:set var="localData" value="scoped"/>
  
  <!-- Modify parent variable -->
  <j:set var="config" value="staging"/>
</j:scope>

<!-- Parent sees modified value -->
<j:out value="Updated config: ${config}"/>
```

### Benefits
- Controlled variable visibility
- Prevents naming conflicts
- Enables modular script design

### Pitfalls to Avoid
- Don't rely on scoped variables outside their scope
- Be explicit about which variables should be modified
- Document variable ownership clearly

---

## Template Method Pattern

### Problem
Multiple scripts share the same structure but differ in specific steps.

### Solution
Create a master template that calls configurable fragments.

### Example
```xml
<!-- Template structure -->
<j:jelly>
  <!-- Step 1: Always the same -->
  <j:out value="=== Report Start ==="/>
  
  <!-- Step 2: Customizable -->
  ${customHeader}
  
  <!-- Step 3: Always the same -->
  <j:out value="--- Data ---"/>
  
  <!-- Step 4: Customizable -->
  ${customContent}
  
  <!-- Step 5: Always the same -->
  <j:out value="=== Report End ==="/>
</j:jelly>
```

### Benefits
- Enforces consistent structure
- Allows customization at specific points
- Reduces duplication

---

## Builder Pattern

### Problem
Constructing complex outputs requires many configuration steps.

### Solution
Build outputs incrementally using variables as accumulator.

### Example
```xml
<j:set var="sql" value="SELECT "/>

<!-- Add columns -->
<j:forEach var="col" items="${columns}" indexVar="i">
  <j:if test="${i gt 0}">
    <j:set var="sql" value="${sql + ', '}"/>
  </j:if>
  <j:set var="sql" value="${sql + col}"/>
</j:forEach>

<!-- Add FROM clause -->
<j:set var="sql" value="${sql + ' FROM ' + tableName}"/>

<!-- Add WHERE if needed -->
<j:if test="${whereClause != null}">
  <j:set var="sql" value="${sql + ' WHERE ' + whereClause}"/>
</j:if>

<j:out value="${sql}"/>
```

### Benefits
- Step-by-step construction
- Easy to add conditional parts
- Clear output building process

---

## Strategy Pattern

### Problem
Need to select different algorithms or approaches based on runtime conditions.

### Solution
Use conditionals to select and execute different script fragments.

### Example
```xml
<!-- Define strategies -->
<j:set var="jsonStrategy">
  <j:scope>
    <j:out value="{&quot;name&quot;: &quot;${data}&quot;}"/>
  </j:scope>
</j:set>

<j:set var="xmlStrategy">
  <j:scope>
    <j:out value="&lt;name&gt;${data}&lt;/name&gt;"/>
  </j:scope>
</j:set>

<!-- Select strategy -->
<j:choose>
  <j:when test="${format == 'json'}">
    ${jsonStrategy}
  </j:when>
  <j:when test="${format == 'xml'}">
    ${xmlStrategy}
  </j:when>
</j:choose>
```

### Benefits
- Runtime flexibility
- Easy to add new strategies
- Clear separation of algorithms

---

## Best Practices Summary

### DO
- ✅ Use scopes to limit variable lifetime
- ✅ Document variable purposes and ownership
- ✅ Break complex scripts into fragments
- ✅ Use meaningful variable names
- ✅ Test fragments independently

### DON'T
- ❌ Create deeply nested scopes (max 3 levels)
- ❌ Rely on variable side effects
- ❌ Mix concerns in single fragment
- ❌ Use global variables for temporary data
- ❌ Forget to handle null/empty values

---

## Performance Considerations

### Script Caching
```java
// Cache compiled scripts for reuse
Script script = context.compileScript(file);
script.run(context, output);  // Reuse
```

### Variable Management
- Clear unused variables when possible
- Use scopes to auto-cleanup
- Don't store large objects unnecessarily

### Output Buffering
- Stream large outputs directly
- Don't build huge strings in memory
- Use appropriate buffer sizes

---

## Testing Strategies

### Unit Testing Fragments
```java
@Test
public void testFragment() {
    JellyContext context = new JellyContext();
    context.setVariable("input", "test");
    
    StringWriter output = new StringWriter();
    XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
    
    context.runScript("fragment.jelly", xmlOutput);
    
    assertEquals("expected", output.toString());
}
```

### Integration Testing
- Test complete script compositions
- Verify context inheritance
- Check output format correctness

---

## Real-World Examples

### Maven Plugin Pattern
Apache Maven historically used Jelly for plugin scripts:
```xml
<maven>
  <preGoal name="java:compile">
    <!-- Execute before compilation -->
  </preGoal>
</maven>
```

### Code Generation Pattern
Generate DAOs, DTOs, and service classes:
```xml
<j:forEach var="entity" items="${entities}">
  <!-- Generate DAO -->
  <j:set var="className" value="${entity.name}DAO"/>
  ${generateDAO}
  
  <!-- Generate DTO -->
  <j:set var="className" value="${entity.name}DTO"/>
  ${generateDTO}
</j:forEach>
```

### Configuration Management Pattern
Generate environment-specific configs:
```xml
<j:choose>
  <j:when test="${env == 'prod'}">
    ${prodConfig}
  </j:when>
  <j:when test="${env == 'staging'}">
    ${stagingConfig}
  </j:when>
  <j:otherwise>
    ${devConfig}
  </j:otherwise>
</j:choose>
```
