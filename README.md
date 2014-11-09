Resteeth
========

Resteeth dynamically creates rest clients based on plain java interface with Spring MVC annotations. Ready to use beans are available through standard Spring injections.

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
            <version>0.1.0</version>
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
testCompile 'eu.codearte.resteeth:resteeth:0.1.0'
```

2) Prepare interface

```java
@RestClient
interface UserRestInterface {

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getUser(@PathVariable("id") Integer id);

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	void postToUsers(@RequestBody User user);

}
```

3) Define endpoint

```java
	@Bean
	public EndpointProvider endpointProvider() {
		return Endpoints.fixedEndpoint("http://api.mydomain.com/");
	}
```

4) Use!

```java
	@Autowired
	private FooRestInterface fooRestInterface;

	User = fooRestInterface.getUser(123);
```

Advanced usage
-----

1) Round robin load balancing
```java
	@Bean
	public EndpointProvider endpointProvider() {
		return Endpoints.roundRobinEndpoint("http://api1.mydomain.com/",
 				"http://api2.mydomain.com/");
	}
```

2) Different endpoints. When you're using more than one rest service in your application you have to match particular interface with proper EndpointProvider. It's done by qualifiers:
```java
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target([ElementType.METHOD, ElementType.TYPE])
	public @interface Foo {

	}

	@Bean
	@Foo
	public EndpointProvider fooEndpointProvider() {
		return Endpoints.fixedEndpoint("http://foo.mydomain.com/");
	}

	@RestClient
	@Foo
	interface UserRestInterface {

	}
```