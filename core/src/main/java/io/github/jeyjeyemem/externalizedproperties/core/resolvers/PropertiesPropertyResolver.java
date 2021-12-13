package io.github.jeyjeyemem.externalizedproperties.core.resolvers;

import io.github.jeyjeyemem.externalizedproperties.core.ExternalizedPropertyResolver;

import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.Arguments.requireNonNull;

/**
 * {@link ExternalizedPropertyResolver} implementation which resolves requested properties 
 * from a given properties instance.
 */
public class PropertiesPropertyResolver extends MapPropertyResolver {
    /**
     * Constructor which builds from a {@link Properties} instance.
     * 
     * @implNote Only properties with keys or values that are of type {@link String}
     * are supported. Properties that do not meet this criteria will be ignored.
     * 
     * @implNote The {@link Properties} keys and values will be copied over to an internal 
     * {@link ConcurrentHashMap} for thread safety and to avoid the performance penalty of 
     * {@link Properties}/{@link Hashtable} synchronization.
     * 
     * @param properties The properties instance to build from.
     */
    public PropertiesPropertyResolver(Properties properties) {
        super(
            ignoreNonStringProperties(requireNonNull(properties, "properties"))
        );
    }

    /**
     * Constructor which builds from a {@link Properties} instance.
     * 
     * @implNote Only properties with keys or values that are of type {@link String}
     * are supported. Properties that do not meet this criteria will be ignored.
     * 
     * @implNote The {@link Properties} keys and values will be copied over to an internal 
     * {@link ConcurrentHashMap} for thread safety and to avoid the performance penalty of 
     * {@link Properties}/{@link Hashtable} synchronization.
     * 
     * @param properties The source properties instance to build from.
     * @param unresolvedPropertyHandler Any properties not found in the source properties will tried 
     * to be resolved via this handler. This should accept a property name and return the property value 
     * for the given property name. {@code null} return values are allowed but will be discarded when 
     * building the {@link Result}.
     */
    public PropertiesPropertyResolver(
            Properties properties, 
            Function<String, String> unresolvedPropertyHandler
    ) {
        super(
            ignoreNonStringProperties(requireNonNull(properties, "properties")),
            requireNonNull(unresolvedPropertyHandler, "unresolvedPropertyHandler")
        );
    }

    private static ConcurrentMap<String, String> ignoreNonStringProperties(
            Properties properties
    ) {
        return properties.entrySet()
            .stream()
            .filter(e -> 
                e.getKey() instanceof String &&
                e.getValue() instanceof String
            )
            .collect(Collectors.toConcurrentMap(
                e -> (String)e.getKey(), 
                e -> (String)e.getValue()
            ));
    }
}
