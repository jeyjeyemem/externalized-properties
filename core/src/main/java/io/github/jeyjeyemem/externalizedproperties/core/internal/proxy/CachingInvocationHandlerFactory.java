package io.github.jeyjeyemem.externalizedproperties.core.internal.proxy;

import io.github.jeyjeyemem.externalizedproperties.core.CacheStrategy;
import io.github.jeyjeyemem.externalizedproperties.core.Converter;
import io.github.jeyjeyemem.externalizedproperties.core.Resolver;
import io.github.jeyjeyemem.externalizedproperties.core.internal.cachestrategies.WeakConcurrentHashMapCacheStrategy;
import io.github.jeyjeyemem.externalizedproperties.core.internal.cachestrategies.WeakHashMapCacheStrategy;
import io.github.jeyjeyemem.externalizedproperties.core.proxy.InvocationHandlerFactory;

import java.lang.reflect.Method;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.Arguments.requireNonNull;

/**
 * The factory for {@link CachingInvocationHandler}.
 */
public class CachingInvocationHandlerFactory 
    implements InvocationHandlerFactory<CachingInvocationHandler> {

    private final InvocationHandlerFactory<?> decorated;
    private final CacheStrategy<Method, Object> cacheStrategy;

    /**
     * Constructor.
     * 
     * @param decorated The decorated {@link InvocationHandlerFactory} instance.
     * @param cacheStrategy The cache strategy keyed by a {@link Method} and whose values
     * are the resolved properties. It is recommended that the {@link CacheStrategy} 
     * implementation only holds weak references to the {@link Method} key in order to avoid
     * leaks and class unloading issues.
     * 
     * @see WeakConcurrentHashMapCacheStrategy
     * @see WeakHashMapCacheStrategy
     */
    public CachingInvocationHandlerFactory(
            InvocationHandlerFactory<?> decorated,
            CacheStrategy<Method, Object> cacheStrategy
    ) {
        this.decorated = requireNonNull(decorated, "decorated");
        this.cacheStrategy = requireNonNull(cacheStrategy, "cacheStrategy");
    }

    /** {@inheritDoc} */
    @Override
    public CachingInvocationHandler create(
            Resolver resolver, 
            Converter<?> converter,
            Class<?> proxyInterface
    ) { 
        return new CachingInvocationHandler(
            decorated.create(resolver, converter, proxyInterface),
            cacheStrategy,
            proxyInterface
        );
    }
    
}
