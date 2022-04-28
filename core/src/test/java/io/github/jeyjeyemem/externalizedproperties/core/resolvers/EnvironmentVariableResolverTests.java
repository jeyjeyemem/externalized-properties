package io.github.jeyjeyemem.externalizedproperties.core.resolvers;

import io.github.jeyjeyemem.externalizedproperties.core.ExternalizedProperties;
import io.github.jeyjeyemem.externalizedproperties.core.ResolverProvider;
import io.github.jeyjeyemem.externalizedproperties.core.proxy.ProxyMethod;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.ProxyMethods;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentVariableResolverTests {
    @Nested
    class ProviderMethod {
        @Test
        @DisplayName("should not return null.")
        public void test1() {
            ResolverProvider<EnvironmentVariableResolver> provider = 
                EnvironmentVariableResolver.provider();

            assertNotNull(provider);
        }

        @Test
        @DisplayName("should return an instance on get.")
        public void test2() {
            ResolverProvider<EnvironmentVariableResolver> provider = 
                EnvironmentVariableResolver.provider();

            assertNotNull(
                provider.get(ExternalizedProperties.builder().withDefaults().build())
            );
        }
    }
    
    @Nested
    class ResolveMethod {
        @Test
        @DisplayName("should resolve property value from environment variables.")
        void test1() {
            EnvironmentVariableResolver resolver = resolverToTest();
            ProxyMethod proxyMethod = ProxyMethods.path();

            Optional<String> result = resolver.resolve(
                proxyMethod,
                "PATH"
            );

            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(
                System.getenv("PATH"), 
                result.get()
            );
        }

        @Test
        @DisplayName(
            "should return empty Optional when environment variable is not found."
        )
        void test2() {
            EnvironmentVariableResolver resolver = resolverToTest();
            ProxyMethod proxyMethod = ProxyMethods.property();

            Optional<String> result = resolver.resolve(
                proxyMethod,
                "property"
            );

            assertNotNull(result);
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName(
            "should attempt to resolve environment variable by formatting " + 
            "property name to environment variable format."
        )
        void test3() {
            EnvironmentVariableResolver resolver = resolverToTest();
            ProxyMethod proxyMethod = ProxyMethods.javaHome();

            Optional<String> result1 = resolver.resolve(
                proxyMethod,
                // java.home should be converted to JAVA_HOME
                "java.home"
            );

            Optional<String> result2 = resolver.resolve(
                proxyMethod,
                // java-home should be converted to JAVA_HOME
                "java-home"  
            );

            assertNotNull(result1);
            assertNotNull(result2);
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            assertEquals(System.getenv("JAVA_HOME"), result1.get());
            assertEquals(System.getenv("JAVA_HOME"), result2.get());
        }
    }

    private EnvironmentVariableResolver resolverToTest() {
        return new EnvironmentVariableResolver();
    }
}
