# Struqt Utilities Benchmarks

Benchmark tests with [JMH](http://openjdk.java.net/projects/code-tools/jmh/) (Java Microbenchmark Harness)

[![Maven Central](https://img.shields.io/maven-central/v/com.struqt/struqt-util-benchmark.svg)](https://maven-badges.herokuapp.com/maven-central/com.struqt/struqt-util-benchmark)

How to build
------------

Prepare the following requirement:
* Latest stable [Oracle JDK 8](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)

Build with Maven command:

```Bash
mvn clean package
```

How to run
----------

```Bash
java -jar target/jmh-struqt-util-benchmark-?VERSION?.jar
```

> Note: The value of `?VERSION?` depends on the version of your work copy
