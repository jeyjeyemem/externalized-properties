package io.github.jeyjeyemem.externalizedproperties.core.variableexpansion;

import io.github.jeyjeyemem.externalizedproperties.core.VariableExpander;

/**
 * A no-op {@link VariableExpander} implementation.
 * Use this if variable expansion feature is not required
 * and better performance is desired.
 */
public class NoOpVariableExpander implements VariableExpander {

    /**
     * Singleton instance.
     */
    public static final NoOpVariableExpander INSTANCE = Singleton.INSTANCE;

    private NoOpVariableExpander(){}

    /** {@inheritDoc} */
    @Override
    public String expandVariables(String value) {
        return value;
    }
    
    /**
     * Singleton holder.
     */
    private static final class Singleton {
        private static final NoOpVariableExpander INSTANCE = new NoOpVariableExpander();
    }
}
