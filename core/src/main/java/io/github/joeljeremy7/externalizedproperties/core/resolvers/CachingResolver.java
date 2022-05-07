package io.github.joeljeremy7.externalizedproperties.core.resolvers;

import io.github.joeljeremy7.externalizedproperties.core.CacheStrategy;
import io.github.joeljeremy7.externalizedproperties.core.Resolver;
import io.github.joeljeremy7.externalizedproperties.core.ResolverProvider;
import io.github.joeljeremy7.externalizedproperties.core.proxy.ProxyMethod;

import java.util.Optional;

import static io.github.joeljeremy7.externalizedproperties.core.internal.Arguments.requireNonNull;

/**
 * A {@link Resolver} decorator which caches resolved properties
 * for a specified duration.
 */
public class CachingResolver implements Resolver {
    private final Resolver decorated;
    private final CacheStrategy<String, String> cacheStrategy;

    /**
     * Constructor.
     * 
     * @param decorated The decorated {@link Resolver} where properties will actually 
     * be resolved from.
     * @param cacheStrategy The cache strategy.
     */
    public CachingResolver(
            Resolver decorated,
            CacheStrategy<String, String> cacheStrategy
    ) {
        this.decorated = requireNonNull(decorated, "decorated");
        this.cacheStrategy = requireNonNull(cacheStrategy, "cacheStrategy");
    }

    /**
     * The {@link ResolverProvider} for {@link CachingResolver}.
     * 
     * @param decorated The decorated {@link ResolverProvider}.
     * @param cacheStrategy The cache strategy.
     * @return The {@link ResolverProvider} for {@link CachingResolver}.
     */
    public static ResolverProvider<CachingResolver> provider(
            ResolverProvider<?> decorated,
            CacheStrategy<String, String> cacheStrategy
    ) {
        requireNonNull(decorated, "decorated");
        requireNonNull(cacheStrategy, "cacheStrategy");

        return externalizedProperties -> new CachingResolver(
            decorated.get(externalizedProperties), 
            cacheStrategy
        );
    }

    /**
     * Resolve property from the decorated {@link Resolver} 
     * and caches the resolved property. If requested property is already in the cache,
     * the cached property will be returned.
     * 
     * @param proxyMethod The proxy method.
     * @param propertyName The property name.
     * @return The resolved property value. Otherwise, an empty {@link Optional}.
     */
    @Override
    public Optional<String> resolve(ProxyMethod proxyMethod, String propertyName) {
        Optional<String> cached = cacheStrategy.get(propertyName);
        if (cached.isPresent()) {
            return cached;
        }

        Optional<String> resolved = decorated.resolve(proxyMethod, propertyName);
        if (resolved.isPresent()) {
            // Cache.
            cacheStrategy.cache(propertyName, resolved.get());
            return resolved;
        }

        return Optional.empty();
    }
}
