# Apache Jelly Learning Project

A comprehensive learning environment for understanding Apache Jelly through practical examples and documentation.

## 🚀 Quick Setup

### Prerequisites

1. **Java 11 or higher**
   ```bash
   # Check Java version
   java -version
   
   # If not installed, download from:
   # https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
   ```

2. **Maven 3.6 or higher**
   ```bash
   # Check Maven version
   mvn -version
   
   # If not installed:
   # macOS: brew install maven
   # Linux: sudo apt-get install maven
   # Windows: Download from https://maven.apache.org/download.cgi
   ```

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/RaikaSurendra/whatJelly.git
   cd whatJelly
   ```

2. **Compile the project**
   ```bash
   mvn clean compile
   ```
   This will download all dependencies (Apache Jelly, JEXL, etc.) and compile the Java sources.

3. **Run your first example**
   ```bash
   # Make the script executable (Unix/Mac)
   chmod +x run-example.sh
   
   # Run hello world example
   ./run-example.sh 01-hello-world
   ```

### Available Commands

```bash
# Run all examples
./run-example.sh all

# Run specific example
./run-example.sh 02-variables
./run-example.sh 04-loops

# Run practical examples
./run-example.sh code-generator
./run-example.sh sql-generator

# Run with Maven directly
mvn exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" \
  -Dexec.args="examples/01-hello-world.jelly"
```

## What is Apache Jelly?

Apache Jelly is a **Java and XML-based scripting and processing engine**. It's a tool for turning XML into executable code. Jelly combines the power of:
- **XML syntax** - for structure and readability
- **Java** - for logic and execution
- **Tag libraries** - reusable components

## Key Concepts

### 1. **Jelly Scripts**
- Written in XML format (typically `.jelly` files)
- Consist of tags from various tag libraries
- Execute dynamically at runtime

### 2. **Tag Libraries**
Jelly provides several built-in tag libraries:
- **core** (`jelly:core`) - Basic control flow, variables, loops
- **xml** - XML manipulation
- **log** - Logging functionality
- **define** - Create custom tags
- **bean** - JavaBean manipulation
- **sql** - Database operations
- **http** - HTTP requests
- **jms** - Message queuing

### 3. **JellyContext**
- The execution environment for Jelly scripts
- Stores variables and their values
- Can be passed between scripts

### 4. **Variables**
- Set with `<j:set>` tag
- Referenced using `${variableName}` syntax (JEXL expressions)
- Scoped within the JellyContext

## How It Works

1. **Parse** - XML is parsed into a Jelly script tree
2. **Execute** - Tags are executed in order
3. **Output** - Results are written to an XMLOutput stream

## Use Cases

- **Code Generation** - Generate Java/SQL/HTML from templates
- **Build Tools** - Maven uses Jelly for plugin scripts
- **XML Transformation** - More flexible than XSLT
- **Testing** - Dynamic test generation
- **Configuration** - Dynamic configuration files

## Running Jelly

```bash
# Using Maven
mvn compile exec:java -Dexec.mainClass="JellyRunner" -Dexec.args="script.jelly"

# Programmatically in Java
JellyContext context = new JellyContext();
XMLOutput output = XMLOutput.createXMLOutput(System.out);
context.runScript("script.jelly", output);
```
