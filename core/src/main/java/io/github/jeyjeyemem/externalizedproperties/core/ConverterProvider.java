package io.github.jeyjeyemem.externalizedproperties.core;

import java.util.concurrent.atomic.AtomicReference;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.Arguments.requireNonNull;

/**
 * Provider of {@link Converter} instance.
 * @param <T> The type of converter.
 */
public interface ConverterProvider<T extends Converter<?>> {
    /**
     * Get an instance of {@link Converter}.
     * 
     * @param externalizedProperties The {@link ExternalizedProperties} instance.
     * @param rootConverter The root {@link Converter} instance. The root converter
     * which contains all registered converters and delegates accordingly.
     * @return An instance of {@link Converter}.
     */
    T get(
        ExternalizedProperties externalizedProperties,
        Converter<?> rootConverter
    );

    /**
     * Create a {@link ConverterProvider} which memoizes the result of another
     * {@link ConverterProvider}.
     * 
     * @param <T> The type of converter.
     * @param provider The {@link ConverterProvider} whose result will be memoized.
     * @return A {@link ConverterProvider} which memoizes the result of another
     * {@link ConverterProvider}.
     */
    static <T extends Converter<?>> ConverterProvider<T> memoize(
            ConverterProvider<T> provider
    ) {
        requireNonNull(provider, "provider");

        final AtomicReference<T> memoized = new AtomicReference<>(null);
        return (externalizedProperties, rootConverter) -> {
            T result = memoized.get();
            if (result == null) {
                result = provider.get(externalizedProperties, rootConverter);
                memoized.compareAndSet(null, result);
                result = memoized.get();
                if (result == null) {
                    throw new IllegalStateException("Memoized provider returned null.");
                }
            }
            return result;
        };
    }
}