# Quick Start Guide

## Setup (One-time)

1. **Ensure Java 11+ and Maven are installed:**
   ```bash
   java -version
   mvn -version
   ```

2. **Compile the project:**
   ```bash
   mvn clean compile
   ```

## Running Examples

### Option 1: Run All Examples
```bash
chmod +x run-example.sh
./run-example.sh all
```

### Option 2: Run Individual Examples
```bash
# Basic examples
./run-example.sh 01-hello-world
./run-example.sh 02-variables
./run-example.sh 03-conditionals
./run-example.sh 04-loops

# Practical examples
./run-example.sh code-generator
./run-example.sh sql-generator
./run-example.sh html-report

# Java integration demo
./run-example.sh context-demo
```

### Option 3: Use Maven Directly
```bash
# Run a specific example
mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="examples/01-hello-world.jelly"

# Run all examples
mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyExamplesRunner"

# Run context demo
mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyContextDemo"
```

## Generating Output Files

Save output to a file:
```bash
mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="examples/practical/code-generator.jelly output/User.java"
```

## Learning Path

1. **Start with basics** - Run examples 01-10 in order
2. **Read TUTORIAL.md** - Comprehensive guide
3. **Explore practical examples** - See real-world use cases
4. **Experiment** - Modify examples or create your own
5. **Read official docs** - https://commons.apache.org/proper/commons-jelly/

## Project Structure

```
try1/
├── README.md              # Overview of Apache Jelly
├── TUTORIAL.md            # Comprehensive tutorial
├── QUICKSTART.md          # This file
├── pom.xml                # Maven configuration
├── run-example.sh         # Convenience script
├── examples/              # Example scripts
│   ├── 01-hello-world.jelly
│   ├── 02-variables.jelly
│   ├── ...
│   └── practical/         # Real-world examples
│       ├── code-generator.jelly
│       ├── sql-generator.jelly
│       ├── config-generator.jelly
│       └── html-report.jelly
└── src/main/java/         # Java runners
    └── com/learning/jelly/
        ├── JellyRunner.java
        ├── JellyExamplesRunner.java
        └── JellyContextDemo.java
```

## Key Concepts (30-Second Summary)

**Apache Jelly = XML + Java + Scripting**

1. **Write scripts in XML** format with special tags
2. **Use expressions** `${variable}` to access data
3. **Control flow** with `<j:if>`, `<j:forEach>`, etc.
4. **Generate output** dynamically (code, configs, reports)
5. **Integrate with Java** seamlessly

## Common Commands

```bash
# Run hello world
./run-example.sh 01-hello-world

# Generate Java code
./run-example.sh code-generator > output/User.java

# Generate SQL
./run-example.sh sql-generator > output/schema.sql

# Generate config for different environments
mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="examples/practical/config-generator.jelly" \
  -Denv=prod

# Generate HTML report
./run-example.sh html-report > output/report.html
```

## Next Steps

- Modify `examples/practical/code-generator.jelly` to generate your own POJOs
- Create a new Jelly script in `examples/` directory
- Read `TUTORIAL.md` for in-depth explanations
- Explore tag libraries: https://commons.apache.org/proper/commons-jelly/tags.html

## Troubleshooting

**Error: "Command not found: mvn"**
- Install Maven: `brew install maven` (macOS) or download from https://maven.apache.org

**Error: "Unsupported class file major version"**
- Check Java version: `java -version` (need Java 11+)
- Update pom.xml compiler version if needed

**Script not found**
- Ensure you're running from project root directory
- Check file path: `ls examples/`

**Output is XML, not plain text**
- This is normal for some examples that generate XML
- Use `> output.txt` to save to file for better viewing
