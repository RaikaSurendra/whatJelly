#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: ./run-example.sh <example-name>"
    echo ""
    echo "Available examples:"
    echo "  Basic Examples:"
    echo "    01-hello-world"
    echo "    02-variables"
    echo "    03-conditionals"
    echo "    04-loops"
    echo "    05-xml-generation"
    echo "    06-custom-tags"
    echo "    07-file-operations"
    echo "    08-script-invocation"
    echo "    09-expressions"
    echo "    10-java-integration"
    echo ""
    echo "  Practical Examples:"
    echo "    code-generator"
    echo "    sql-generator"
    echo "    config-generator"
    echo "    html-report"
    echo ""
    echo "  Advanced Examples:"
    echo "    nested-contexts"
    echo "    script-library"
    echo "    data-driven-generation"
    echo "    dynamic-script-composition"
    echo ""
    echo "  Special:"
    echo "    all           - Run all basic examples"
    echo "    context-demo  - Run JellyContext demonstration"
    echo ""
    echo "Example: ./run-example.sh 01-hello-world"
    echo "Example: ./run-example.sh nested-contexts"
    exit 1
fi

EXAMPLE=$1

if [ "$EXAMPLE" = "all" ]; then
    mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyExamplesRunner"
elif [ "$EXAMPLE" = "context-demo" ]; then
    mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyContextDemo"
elif [[ "$EXAMPLE" == practical/* ]] || [[ "$EXAMPLE" == code-generator ]] || [[ "$EXAMPLE" == sql-generator ]] || [[ "$EXAMPLE" == config-generator ]] || [[ "$EXAMPLE" == html-report ]]; then
    if [[ "$EXAMPLE" != practical/* ]]; then
        EXAMPLE="practical/$EXAMPLE"
    fi
    mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" -Dexec.args="examples/${EXAMPLE}.jelly"
elif [[ "$EXAMPLE" == advanced/* ]] || [[ "$EXAMPLE" == nested-contexts ]] || [[ "$EXAMPLE" == script-library ]] || [[ "$EXAMPLE" == data-driven-generation ]] || [[ "$EXAMPLE" == dynamic-script-composition ]]; then
    if [[ "$EXAMPLE" != advanced/* ]]; then
        EXAMPLE="advanced/$EXAMPLE"
    fi
    mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" -Dexec.args="${EXAMPLE}.jelly"
else
    mvn compile exec:java -Dexec.mainClass="com.learning.jelly.JellyRunner" -Dexec.args="examples/${EXAMPLE}.jelly"
fi
