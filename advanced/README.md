# Advanced Apache Jelly Concepts

This folder contains advanced examples and concepts for Apache Jelly that go beyond the basics.

## 📚 Contents

### Examples
1. **dynamic-script-composition.jelly** - Building scripts dynamically from multiple sources
2. **custom-expression-evaluator.jelly** - Advanced expression handling
3. **nested-contexts.jelly** - Working with parent/child contexts
4. **script-library.jelly** - Creating reusable script modules
5. **data-driven-generation.jelly** - Generating complex structures from data
6. **maven-plugin-simulation.jelly** - Simulating Maven plugin behavior

### Documentation
- **ADVANCED_PATTERNS.md** - Design patterns for complex Jelly applications
- **PERFORMANCE.md** - Performance optimization techniques
- **DEBUGGING.md** - Advanced debugging strategies

## Prerequisites

Before exploring advanced examples, ensure you understand:
- Basic Jelly syntax (variables, loops, conditionals)
- JEXL expression language
- JellyContext and variable scope
- Tag libraries and namespaces

## Running Advanced Examples

```bash
# From project root
./run-example.sh advanced/dynamic-script-composition

# Or with Maven
mvn exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="advanced/dynamic-script-composition.jelly"
```

## Learning Path

1. Start with **nested-contexts.jelly** to understand scope
2. Move to **script-library.jelly** for modularity
3. Explore **data-driven-generation.jelly** for practical applications
4. Study **dynamic-script-composition.jelly** for advanced techniques

## Key Advanced Concepts

### 1. Context Hierarchies
Understanding parent-child relationships in JellyContext for proper variable scoping.

### 2. Script Composition
Breaking down complex scripts into reusable components.

### 3. Dynamic Generation
Using Jelly to generate code, configurations, and complex XML structures.

### 4. Performance Optimization
Techniques to improve execution speed and memory usage.

### 5. Error Handling
Proper exception handling and debugging in Jelly scripts.
