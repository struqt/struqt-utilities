Struqt Unique ID
================

Unique ID generator inspired by Twitter's Snowflake

## Quick Start

Run with `Maven`:

Add the following dependency element into pom.xml

```xml
<dependency>
  <groupId>com.struqt</groupId>
  <artifactId>struqt-unique-id</artifactId>
  <version>1.0.2</version>
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
