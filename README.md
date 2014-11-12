Resteeth
========

Resteeth dynamically creates rest clients based on plain java interface with Spring MVC annotations. Ready to use beans are available through standard Spring injections.

[![Build Status](https://travis-ci.org/Codearte/resteeth.svg)](https://travis-ci.org/Codearte/resteeth) [![Coverage Status](https://img.shields.io/coveralls/Codearte/resteeth.svg)](https://coveralls.io/r/Codearte/resteeth?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.codearte.resteeth/resteeth/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.codearte.resteeth/resteeth) [![Apache 2](http://img.shields.io/badge/license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Usage
-----

1) Add dependencies

In Maven projects (pom.xml):

```xml
<pom>
    ...
    <dependencies>
        <dependency>
            <groupId>eu.codearte.resteeth</groupId>
            <artifactId>resteeth</artifactId>
            <version>0.2.0</version>
        </dependency>
    </dependencies>
    ...
</pom>
```

In Gradle projects (build.gradle):

```groovy
repositories {
   mavenCentral()
}
...
testCompile 'eu.codearte.resteeth:resteeth:0.2.0'
```

2) Prepare interface

```java
interface FooRestInterface {

	@RequestMapping(value = "/foos/{id}", method = RequestMethod.GET)
	Foo getFoo(@PathVariable("id") Integer id);

	@RequestMapping(value = "/foos", method = RequestMethod.POST)
	void postFoo(@RequestBody Foo user);

}
```

3) Use!

with single URL

```java
@RestClient(endpoints = {"http://api.mydomain.com"})
private FooRestInterface fooRestInterface;

Foo = fooRestInterface.getFoo(123);
```

or with round robin load balancing

```java
@RestClient(endpoints = {"http://api1.mydomain.com/", "http://api2.mydomain.com/"})
private FooRestInterface fooRestInterface;

Foo = fooRestInterface.getFoo(123);
```
