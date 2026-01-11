# Performance Optimization Guide

Guidelines for optimizing Apache Jelly scripts and applications.

## Understanding Jelly Performance

### Execution Model
1. **Parse Phase** - XML parsing (one-time cost)
2. **Compilation Phase** - Tag tree building (can be cached)
3. **Execution Phase** - Tag execution (main cost)
4. **Output Phase** - Writing results (I/O bound)

### Performance Characteristics
- **Interpreted** - Not compiled to bytecode
- **XML parsing overhead** - DOM/SAX parsing cost
- **Expression evaluation** - JEXL interpretation
- **I/O bound** - Output writing can be bottleneck

---

## Optimization Techniques

### 1. Cache Compiled Scripts

**Problem**: Re-parsing scripts on every execution is expensive.

**Solution**: Compile once, execute many times.

```java
// BAD - Parse every time
public void generateReport() {
    JellyContext context = new JellyContext();
    context.runScript(new File("report.jelly"), output);
}

// GOOD - Cache compiled script
private Script cachedScript;

public void init() throws Exception {
    JellyContext context = new JellyContext();
    cachedScript = context.compileScript(new File("report.jelly"));
}

public void generateReport() {
    JellyContext context = new JellyContext();
    cachedScript.run(context, output);
}
```

**Impact**: 50-80% faster for repeated executions

---

### 2. Minimize Variable Lookups

**Problem**: Repeated variable lookups in expressions have cost.

**Solution**: Cache values in local variables.

```xml
<!-- BAD - Lookup list.size() every iteration -->
<j:forEach var="i" begin="0" end="${list.size() - 1}">
  <j:out value="${list.size()}"/>  <!-- Another lookup -->
</j:forEach>

<!-- GOOD - Cache size once -->
<j:set var="listSize" value="${list.size()}"/>
<j:forEach var="i" begin="0" end="${listSize - 1}">
  <j:out value="${listSize}"/>  <!-- No method call -->
</j:forEach>
```

**Impact**: 20-40% faster for large loops

---

### 3. Use Appropriate Output Buffering

**Problem**: Writing small chunks frequently is inefficient.

**Solution**: Use buffered output streams.

```java
// BAD - Unbuffered
FileOutputStream fos = new FileOutputStream("output.xml");
XMLOutput output = XMLOutput.createXMLOutput(fos);

// GOOD - Buffered
FileOutputStream fos = new FileOutputStream("output.xml");
BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
XMLOutput output = XMLOutput.createXMLOutput(bos);
```

**Impact**: 2-5x faster for large outputs

---

### 4. Limit Context Size

**Problem**: Large contexts consume memory and slow lookups.

**Solution**: Use scopes and clear unused variables.

```xml
<!-- BAD - Keep accumulating variables -->
<j:forEach var="i" begin="1" end="1000">
  <j:set var="temp${i}" value="${i * 2}"/>
</j:forEach>

<!-- GOOD - Use scopes to auto-cleanup -->
<j:forEach var="i" begin="1" end="1000">
  <j:scope>
    <j:set var="temp" value="${i * 2}"/>
    <!-- Use temp -->
  </j:scope>
  <!-- temp is garbage collected -->
</j:forEach>
```

**Impact**: Reduces memory by 50-90%

---

### 5. Avoid Deep Expression Nesting

**Problem**: Complex expressions are slow to evaluate.

**Solution**: Break into multiple simple expressions.

```xml
<!-- BAD - Complex nested expression -->
<j:set var="result" value="${((a + b) * (c - d)) / ((e * f) + (g / h))}"/>

<!-- GOOD - Step by step -->
<j:set var="sum" value="${a + b}"/>
<j:set var="diff" value="${c - d}"/>
<j:set var="numerator" value="${sum * diff}"/>
<j:set var="prod" value="${e * f}"/>
<j:set var="quot" value="${g / h}"/>
<j:set var="denominator" value="${prod + quot}"/>
<j:set var="result" value="${numerator / denominator}"/>
```

**Impact**: 15-25% faster, more readable

---

### 6. Use Native Java for Heavy Computation

**Problem**: JEXL is not efficient for complex calculations.

**Solution**: Do heavy work in Java, pass results to Jelly.

```java
// GOOD - Process in Java
List<ProcessedData> data = complexAlgorithm(rawData);
context.setVariable("processedData", data);
context.runScript(template, output);
```

```xml
<!-- Then just iterate in Jelly -->
<j:forEach var="item" items="${processedData}">
  <j:out value="${item.name}"/>
</j:forEach>
```

**Impact**: 10-100x faster depending on complexity

---

### 7. Optimize String Concatenation

**Problem**: String concatenation in loops is inefficient.

**Solution**: Build lists and join, or use Java StringBuilder.

```xml
<!-- BAD - Concatenate in loop -->
<j:set var="result" value=""/>
<j:forEach var="item" items="${items}">
  <j:set var="result" value="${result + item + ','}"/>
</j:forEach>

<!-- BETTER - Use direct output -->
<j:forEach var="item" items="${items}" indexVar="i">
  <j:if test="${i gt 0}">
    <j:out value=","/>
  </j:if>
  <j:out value="${item}"/>
</j:forEach>
```

**Impact**: 50-200x faster for large lists

---

## Benchmarking Tips

### Measure Script Execution

```java
public class JellyBenchmark {
    public static void main(String[] args) throws Exception {
        JellyContext context = new JellyContext();
        Script script = context.compileScript(new File("test.jelly"));
        
        // Warmup
        for (int i = 0; i < 100; i++) {
            script.run(context, XMLOutput.createDummyXMLOutput());
        }
        
        // Measure
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            script.run(context, XMLOutput.createDummyXMLOutput());
        }
        long end = System.nanoTime();
        
        System.out.println("Average time: " + 
            (end - start) / 1000000.0 / 1000 + " ms");
    }
}
```

### Profile Memory Usage

```java
Runtime runtime = Runtime.getRuntime();
runtime.gc();
long before = runtime.totalMemory() - runtime.freeMemory();

// Run script
context.runScript(script, output);

runtime.gc();
long after = runtime.totalMemory() - runtime.freeMemory();

System.out.println("Memory used: " + (after - before) / 1024 + " KB");
```

---

## Common Performance Pitfalls

### ❌ Don't Do This

1. **Parsing in loops**
   ```java
   for (int i = 0; i < 1000; i++) {
       context.runScript(new File("template.jelly"), output);
   }
   ```

2. **Large string building in Jelly**
   ```xml
   <j:set var="huge" value=""/>
   <j:forEach var="i" begin="1" end="10000">
     <j:set var="huge" value="${huge + i}"/>
   </j:forEach>
   ```

3. **Deep recursion**
   ```xml
   <!-- Avoid recursive script calls -->
   <j:if test="${depth lt 100}">
     <j:include uri="recursive.jelly"/>
   </j:if>
   ```

4. **Unnecessary object creation**
   ```xml
   <j:forEach var="i" begin="1" end="1000">
     <j:set var="temp" value="${String.format('%d', i)}"/>
   </j:forEach>
   ```

---

## Performance Checklist

Before deploying:
- [ ] Scripts are compiled and cached
- [ ] Output uses buffered streams
- [ ] Variable lookups minimized in loops
- [ ] Heavy computation done in Java
- [ ] Scopes used for temporary variables
- [ ] String concatenation optimized
- [ ] Benchmarks run and targets met
- [ ] Memory profiling done
- [ ] No deep nesting (max 3 levels)
- [ ] Complex expressions broken down

---

## Expected Performance

### Typical Metrics (Modern Hardware)

| Operation | Time | Throughput |
|-----------|------|------------|
| Parse + compile 100 line script | 10-50 ms | - |
| Execute cached script | 1-5 ms | 200-1000 ops/sec |
| Variable lookup | 0.001 ms | 1M ops/sec |
| Expression evaluation | 0.01-0.1 ms | 10K-100K ops/sec |
| Output line (buffered) | 0.001 ms | 1M lines/sec |

### Scaling Guidelines

- **Small scripts** (< 100 lines): < 5ms per execution
- **Medium scripts** (100-500 lines): 5-20ms per execution
- **Large scripts** (> 500 lines): 20-100ms per execution

**Note**: Actual performance depends on:
- Expression complexity
- Number of iterations
- I/O operations
- Java method calls
- Context size

---

## When NOT to Use Jelly

Consider alternatives if:
- Sub-millisecond latency required
- Processing millions of records
- Real-time performance critical
- Heavy computational workload
- Memory extremely constrained

**Better alternatives**:
- Direct Java code
- Compiled template engines (Freemarker, Velocity)
- Native code generation tools
- Streaming processors
