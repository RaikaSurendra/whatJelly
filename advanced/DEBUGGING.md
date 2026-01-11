# Debugging Apache Jelly Scripts

A practical guide to debugging and troubleshooting Jelly scripts.

## Common Issues and Solutions

### 1. Expression Errors

#### Symptom
```
org.apache.commons.jexl.parser.ParseException: Encountered "java" at line 1, column 5
```

#### Cause
JEXL 1.1 doesn't support the `new` keyword for object instantiation.

#### Solution
```xml
<!-- BAD -->
<j:set var="list" value="${new java.util.ArrayList()}"/>

<!-- GOOD - Pass objects from Java -->
```

```java
List<String> list = new ArrayList<>();
context.setVariable("list", list);
```

---

### 2. Variable Not Found

#### Symptom
Expression evaluates to null or empty, no error thrown.

#### Cause
Variable not set or out of scope.

#### Debug Strategy
```xml
<!-- Add debug output -->
<j:out value="DEBUG: myVar = ${myVar}"/>
<j:out value="${'\n'}"/>
<j:out value="DEBUG: myVar is null = ${myVar == null}"/>
<j:out value="${'\n'}"/>
```

#### Solution
- Check variable name spelling
- Verify variable is set before use
- Check if variable was set in different scope

---

### 3. XML Parsing Errors

#### Symptom
```
SAXParseException: The value of attribute "test" must not contain the '<' character
```

#### Cause
XML special characters not properly escaped.

#### Solution
```xml
<!-- BAD -->
<j:if test="${age < 18}">

<!-- GOOD - Use 'lt' operator -->
<j:if test="${age lt 18}">

<!-- Or escape -->
<j:if test="${age &lt; 18}">
```

**XML Escape Reference:**
- `<` → `&lt;` or use `lt`
- `>` → `&gt;` or use `gt`
- `&` → `&amp;`
- `"` → `&quot;`
- `'` → `&apos;`

---

### 4. Tag Library Not Found

#### Symptom
```
ClassNotFoundException: define
Could not load class: define so taglib instantiation failed
```

#### Cause
Tag library not available in classpath or wrong namespace.

#### Solution
1. Check Maven dependencies
2. Verify namespace declaration
3. Use only available tag libraries

```xml
<!-- Check if library is supported -->
<j:jelly xmlns:j="jelly:core">  <!-- Core is always available -->
</j:jelly>
```

---

### 5. Null Pointer Exceptions

#### Symptom
```
NullPointerException at line X
```

#### Cause
Accessing properties/methods on null object.

#### Debug Strategy
```xml
<!-- Check for null before accessing -->
<j:if test="${object != null}">
  <j:out value="${object.property}"/>
</j:if>

<!-- Or provide default -->
<j:out value="${object != null ? object.property : 'N/A'}"/>
```

---

### 6. Output Not Appearing

#### Symptom
Script runs but produces no output.

#### Causes & Solutions

**Cause 1: Output not flushed**
```java
XMLOutput output = XMLOutput.createXMLOutput(writer);
context.runScript(file, output);
output.flush();  // Add this!
```

**Cause 2: Writing to wrong stream**
```java
// Make sure you're looking at the right output
XMLOutput output = XMLOutput.createXMLOutput(System.out);
```

**Cause 3: Conditional blocks all false**
```xml
<!-- Add else clause to debug -->
<j:choose>
  <j:when test="${condition1}">...</j:when>
  <j:when test="${condition2}">...</j:when>
  <j:otherwise>
    <j:out value="DEBUG: No conditions matched!"/>
  </j:otherwise>
</j:choose>
```

---

## Debugging Techniques

### 1. Add Trace Output

```xml
<j:jelly xmlns:j="jelly:core">
  <j:out value="[TRACE] Script started"/>
  <j:out value="${'\n'}"/>
  
  <!-- Your code -->
  <j:set var="x" value="10"/>
  <j:out value="[TRACE] x set to: ${x}"/>
  <j:out value="${'\n'}"/>
  
  <!-- More code -->
  <j:out value="[TRACE] Before loop"/>
  <j:out value="${'\n'}"/>
  <j:forEach var="i" begin="1" end="${x}">
    <j:out value="[TRACE] Loop iteration: ${i}"/>
    <j:out value="${'\n'}"/>
  </j:forEach>
  
  <j:out value="[TRACE] Script completed"/>
  <j:out value="${'\n'}"/>
</j:jelly>
```

### 2. Dump Context Variables

```java
// In Java code
JellyContext context = new JellyContext();
// ... run script ...

// Dump all variables
Map<String, Object> variables = context.getVariables();
for (Map.Entry<String, Object> entry : variables.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}
```

### 3. Use Conditional Debugging

```xml
<!-- Set debug flag -->
<j:set var="DEBUG" value="${true}"/>

<!-- Conditional debug output -->
<j:if test="${DEBUG}">
  <j:out value="DEBUG: Processing ${item}"/>
  <j:out value="${'\n'}"/>
</j:if>
```

### 4. Validate Inputs

```xml
<!-- At script start -->
<j:if test="${requiredVar == null}">
  <j:out value="ERROR: requiredVar is not set!"/>
  <j:out value="${'\n'}"/>
</j:if>

<!-- Check types -->
<j:out value="Type of myVar: ${myVar.class.name}"/>
<j:out value="${'\n'}"/>
```

### 5. Incremental Testing

Build scripts incrementally:

```xml
<!-- Step 1: Test basic structure -->
<j:jelly xmlns:j="jelly:core">
  <j:out value="Script runs"/>
</j:jelly>

<!-- Step 2: Add variables -->
<j:jelly xmlns:j="jelly:core">
  <j:set var="test" value="value"/>
  <j:out value="${test}"/>
</j:jelly>

<!-- Step 3: Add logic -->
<!-- ... continue building ... -->
```

---

## Using Java Debugger

### Debug Java Runner

```java
public class JellyRunner {
    public static void main(String[] args) throws Exception {
        JellyContext context = new JellyContext();
        
        // Set breakpoint here
        context.setVariable("debug", true);
        
        XMLOutput output = XMLOutput.createXMLOutput(System.out);
        
        // Set breakpoint here
        context.runScript(new File(args[0]), output);
        
        // Set breakpoint here to inspect context
        output.flush();
    }
}
```

### Inspect Variables at Breakpoints
- Check `context.getVariables()`
- Examine `output` stream
- Step through tag execution

---

## Logging Best Practices

### Use Structured Logging

```xml
<j:jelly xmlns:j="jelly:core">
  <!-- Timestamp helper -->
  <j:set var="timestamp" value="${System.currentTimeMillis()}"/>
  
  <!-- Log format: [TIMESTAMP][LEVEL] Message -->
  <j:out value="[${timestamp}][INFO] Starting process"/>
  <j:out value="${'\n'}"/>
  
  <!-- ... process ... -->
  
  <j:out value="[${timestamp}][INFO] Completed successfully"/>
  <j:out value="${'\n'}"/>
</j:jelly>
```

### Log Levels

```xml
<j:set var="logLevel" value="DEBUG"/>  <!-- DEBUG, INFO, WARN, ERROR -->

<!-- Debug logging -->
<j:if test="${logLevel == 'DEBUG'}">
  <j:out value="[DEBUG] Variable state: ${var}"/>
  <j:out value="${'\n'}"/>
</j:if>

<!-- Error logging -->
<j:if test="${error != null}">
  <j:out value="[ERROR] ${error}"/>
  <j:out value="${'\n'}"/>
</j:if>
```

---

## Testing Strategies

### Unit Test Jelly Scripts

```java
@Test
public void testScript() throws Exception {
    JellyContext context = new JellyContext();
    context.setVariable("input", "test");
    
    StringWriter writer = new StringWriter();
    XMLOutput output = XMLOutput.createXMLOutput(writer);
    
    context.runScript(new File("test.jelly"), output);
    output.flush();
    
    String result = writer.toString();
    assertTrue(result.contains("expected"));
}
```

### Test with Different Inputs

```java
@ParameterizedTest
@ValueSource(strings = {"input1", "input2", "input3"})
public void testWithInputs(String input) throws Exception {
    JellyContext context = new JellyContext();
    context.setVariable("data", input);
    // ... test ...
}
```

---

## Common Error Messages Decoded

### "Could not parse Jelly script"
- **Check**: XML well-formedness
- **Solution**: Validate XML syntax

### "Unable to create expression"
- **Check**: JEXL syntax compatibility
- **Solution**: Simplify expression or use Java

### "Tag library could not be loaded"
- **Check**: Namespace declaration
- **Solution**: Verify dependency in pom.xml

### "Variable is null"
- **Check**: Variable initialization
- **Solution**: Add null checks

### "ClassCastException"
- **Check**: Variable types
- **Solution**: Add type validation

---

## Debugging Checklist

Before reporting an issue:
- [ ] Script runs without errors
- [ ] Variables are properly initialized
- [ ] XML is well-formed and valid
- [ ] JEXL expressions are JEXL 1.1 compatible
- [ ] Tag libraries are available
- [ ] Output is flushed
- [ ] Null checks are in place
- [ ] Scope issues ruled out
- [ ] Minimal reproducible example created
- [ ] Error messages reviewed

---

## Getting Help

1. **Check documentation**: TUTORIAL.md, CONCEPTS.md
2. **Review examples**: Basic and practical examples
3. **Enable debug output**: Add trace statements
4. **Isolate the issue**: Create minimal test case
5. **Check JEXL compatibility**: JEXL 1.1 limitations
6. **Review error messages**: Read full stack trace
7. **Consult Apache Jelly docs**: Official documentation

## Useful Commands

```bash
# Run with verbose output
mvn -X exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="script.jelly"

# Run specific test
./run-example.sh 01-hello-world

# Check dependencies
mvn dependency:tree

# Validate POM
mvn validate
```
