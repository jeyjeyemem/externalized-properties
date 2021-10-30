package io.github.jeyjeyemem.externalizedproperties.core.resolvers;

/**
 * A {@link MapPropertyResolver} extension which resolves requested properties 
 * from environment variables.
 */
public class EnvironmentVariablePropertyResolver extends MapPropertyResolver {
    /**
     * Constructor.
     */
    public EnvironmentVariablePropertyResolver() {
        // For any unresolved property, try to resolve again from env var
        // in case new env vars were added after this resolver was constructed.
        super(System.getenv(), propName -> System.getenv(propName));
    }
}
