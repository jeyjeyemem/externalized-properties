# Profiles

Externalized Properties has the concept of profiles. Applications typically are deployed to multiple environments and more often than not, these environment needs different configurations. This is where profiles can help. It allows applications to define different configurations per environment.

Setting up profiles can be done while building [ExternalizedProperties](core/src/main/java/io/github/joeljeremy7/externalizedproperties/core/ExternalizedProperties.java) e.g.

```java
public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = ExternalizedProperties.builder()
        .defaults()
        .onProfiles("test").configure(new MyTestProfileConfigurator())
        .onProfiles("staging").configure(new MyStagingProfileConfigurator())
        .onProfiles("prod").configure(new MyProdProfileConfigurator())
        // This will be applied regardless of the active profile.
        .onProfiles().configure(new MyWildcardProfileConfigurator())
        // This will be applied to both test and staging.
        .onProfiles("test", "staging").configure(new MyNonProdProfileConfigurator())
        .build();
}

public class MyTestProfileConfigurator implements ProfileConfigurator {
    @Override
    public void configure(String profile, BuilderConfiguration builder) {
        // profile is "test".

        builder.resolvers(...)
            .converters(...)
            .processors(...);
    }
}

public class MyStagingProfileConfigurator implements ProfileConfigurator {
    @Override
    public void configure(String activeProfile, BuilderConfiguration builder) {
        // activeProfile is "staging".

        builder.resolvers(...)
            .converters(...)
            .processors(...);
    }
}

public class MyProdProfileConfigurator implements ProfileConfigurator {
    @Override
    public void configure(String activeProfile, BuilderConfiguration builder) {
        // activeProfile is "prod".

        builder.resolvers(...)
            .converters(...)
            .processors(...);
    }
}

public class MyNonProdProfileConfigurator implements ProfileConfigurator {
    @Override
    public void configure(String activeProfile, BuilderConfiguration builder) {
        // activeProfile can be "test" or "staging" depending on the active profile.

        builder.resolvers(...)
            .converters(...)
            .processors(...);
    }
}

public class MyWildcardProfileConfigurator implements ProfileConfigurator {
    @Override
    public void configure(String activeProfile, BuilderConfiguration builder) {
        // This will be applied regardless of the active profile.
        builder.resolvers(applicationProperties(activeProfile));
    }

    private ResourceResolver applicationProperties(String activeProfile) {
        // Changes based on the active profile:
        // application.properties
        // application-test.properties
        // application-staging.properties
        // application-prod.properties
        String resourceNameSuffix = !profile.isEmpty() ? ("-" + activeProfile) : "";
        String resourceName = "/application" + resourceNameSuffix + ".properties";
        try {
            return ResourceResolver.fromUrl(getClass().getResource(resourceName))
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load " + resourceName, ex);
        }
    }
}
```

## Activating an Externalized Properties Profile

The active Externalized Properties profile can be set via:

- System properties (`externalizedproperties.profile`)
- Environment variables (`EXTERNALIZEDPROPERTIES_PROFILE`).