# Struqt Unique ID
[![Maven Central](https://img.shields.io/badge/maven-1.0.3-blue.svg)](http://repo1.maven.org/maven2/com/struqt/struqt-unique-id/1.0.3)
[![Javadocs](https://javadoc.io/badge/com.struqt/struqt-unique-id.svg?color=yellow&version=1.0.3)](https://javadoc.io/doc/com.struqt/struqt-unique-id/1.0.3)

Unique ID generator inspired by Twitter's Snowflake

## Quick Start

Run with `Maven`:

Add the following dependency element into pom.xml

```xml
<dependency>
  <groupId>com.struqt</groupId>
  <artifactId>struqt-unique-id</artifactId>
  <version>1.0.3</version>
</dependency>
```

Then, you can use `UniqueIdGenerator` class to generate unique id sequence


How to build
------------

Prepare the following requirement:
* Latest stable [Oracle JDK 8](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)

> Note:
> * JRE 6 or upper is fine as runtime requirement
> * JDK 8 is just as build-time requirement

Build with Maven command:

```Bash
mvn clean package
```
