# Externalized Properties

[![Gradle Build](https://github.com/jeyjeyemem/externalized-properties/actions/workflows/gradle-build.yaml/badge.svg)](https://github.com/jeyjeyemem/externalized-properties/actions/workflows/gradle-build.yaml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/jeyjeyemem/externalized-properties/blob/main/LICENSE)

A lightweight and extensible library to resolve application properties from various external sources.

## [Twelve Factor Methodology](https://12factor.net)

Externalized Properties was inspired by the [The Twelve Factor Methodology](https://12factor.net)'s section [III. Config](https://12factor.net/config).  

The goal of this library is to make it easy for applications to implement configuration best practices by providing easy-to-use APIs as well as providing the flexibility to choose where to store their configurations/properties.

## Wiki

- For more information and examples please browse through the wiki: <https://github.com/jeyjeyemem/externalized-properties/wiki>

## Getting Started

### Gradle

```gradle
implementation 'io.github.jeyjeyemem.externalizedproperties:core:1.0.0-SNAPSHOT'
```

### Maven

```xml
<dependency>
    <groupId>io.github.jeyjeyemem.externalizedproperties</groupId>
    <artifactId>core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Externalized Properties makes the best of of Java's strong typing by proxying an interface and using that as a facade to resolve properties.

## Features

### Interface Proxying

Given an interface:

```java
public interface ApplicationProperties {
    @ExternalizedProperty("DATABASE_URL")
    String databaseUrl();
    @ExternalizedProperty("DATABASE_DRIVER")
    String databaseDriver();
}
```

We can initialize and start resolving external configurations/properties by:

```java
public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = buildExternalizedProperties();

    // Proxied interface.
    ApplicationProperties props = externalizedProperties.proxy(ApplicationProperties.class);

    // Use properties.
    String databaseUrl = props.databaseUrl();
    String databaseDriver = props.databaseDriver();

    System.out.println("Database URL: " + databaseUrl);
    System.out.println("Database Driver: " + databaseDriver);
}

private ExternalizedProperties buildExternalizedProperties() {
    // Create the ExternalizedProperties instance with default and additional resolvers.
    // Default resolvers include system properties and environment variable resolvers.
    // AWS SSM Resolver and Database Resolver are not part of the core module. They 
    // are part of a separate resolver-aws-ssm and resolver-database modules.

    ExternalizedProperties externalizedProperties = ExternalizedPropertiesBuilder.newBuilder()
        .withDefaultResolvers() 
        .resolvers( 
            new AwsSsmPropertyResolver(ssmClient),
            new DatabaseResolver(entityManagerFactory)
        ) 
        .build();
    
    return externalizedProperties;
}
```

### Direct Property Resolution

Another option is to resolve properties directly from the `ExternalizedProperties` instance if you want to avoid overhead of using proxies:

```java
public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = buildExternalizedProperties();

    // Direct resolution via ExternalizedProperties API.
    Optional<String> databaseUrl = externalizedProperties.resolveProperty("database.url");
    Optional<String> databaseDriver = externalizedProperties.resolveProperty("database.url");

    // Use property:
    System.out.println("Database URL: " + databaseUrl.get());
    System.out.println("Database Driver: " + databaseDriver.get());
}
```

### Property Conversion

Externalized Properties supports conversion of properties to various types. There are several build-in conversion handlers but anyone is free to create a custom conversion handler by implementing the `ConversionHandler` interface.

To register conversion handlers to the library, it must be done through the builder:

```java
private ExternalizedProperties buildExternalizedProperties() {
    ExternalizedProperties externalizedProperties = ExternalizedPropertiesBuilder.newBuilder()
        .withDefaultResolvers()
        .conversionHandlers(
            new PrimitiveConversionHandler(),
            new CustomConversionHandler()
        )
        .build();

    return externalizedProperties;
}
```

To convert a property via the proxy interface, just set the method return type to the target type, and the library will handle the conversion behind the scenes - using the registered conversion handlers.

```java
public interface ApplicationProperties {
    @ExternalizedProperties("thread-count")
    int numberOfThreads();
}

public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = buildExternalizedProperties();

    // Proxied interface.
    ApplicationProperties props = externalizedProperties.proxy(ApplicationProperties.class);

    // Use properties.
    int numberOfThreads = props.numberOfThreads();

    System.out.println("Database URL: " + numberOfThreads);
}
```

To convert a property via the `ExternalizedProperties` API, the `resolveProperty` method which accepts a target type must be used:

```java
public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = buildExternalizedProperties();

    // Use properties.
    int numberOfThreads = externalizedProperties.resolveProperty("number-of-threads", int.class);
    List<Integer> validNumbers = externalizedProperties.resolveProperty("valid-numbers", new TypeReference<List<Integer>>(){});

    System.out.println("Number of threads: " + numberOfThreads);
    System.out.println("Valid numbers: " + validNumbers);
}
```
