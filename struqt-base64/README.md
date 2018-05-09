# Struqt Base64 Codec

Base64 codec implementation as [RFC 2045](https://tools.ietf.org/html/rfc2045#section-6.8) and [RFC 4648](https://tools.ietf.org/html/rfc4648) specified

[![Maven Central](https://img.shields.io/maven-central/v/com.struqt/struqt-base64.svg)](https://maven-badges.herokuapp.com/maven-central/com.struqt/struqt-base64)
[![Javadocs](https://javadoc.io/badge/com.struqt/struqt-base64.svg?color=yellow)](https://javadoc.io/doc/com.struqt/struqt-base64)

Quick Start
-----------

Run with `Maven`:

Add the following dependency element into pom.xml

```xml
<dependency>
  <groupId>com.struqt</groupId>
  <artifactId>struqt-base64</artifactId>
  <version>1.1.0</version>
</dependency>
```


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
