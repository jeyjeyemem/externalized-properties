package io.github.joeljeremy7.externalizedproperties.core.resolvers;

import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperties;
import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperty;
import io.github.joeljeremy7.externalizedproperties.core.Resolver;
import io.github.joeljeremy7.externalizedproperties.core.proxy.ProxyMethod;
import io.github.joeljeremy7.externalizedproperties.core.testfixtures.TestProxyMethodFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Service loader resolvers are configured in resources/META-INF/services folder.
 */
public class ServiceLoaderResolverTests {
    private static final TestProxyMethodFactory<ProxyInterface> PROXY_METHOD_FACTORY =
        new TestProxyMethodFactory<>(ProxyInterface.class);

    @Nested
    class ResolveMethod {
        @Test
        @DisplayName("should load properties from ServiceLoader resolvers.")
        void test1() {
            ServiceLoaderResolver resolver = resolverToTest();
            ProxyMethod javaVersionProxyMethod = PROXY_METHOD_FACTORY.fromMethodReference(
                ProxyInterface::javaVersion,
                externalizedProperties(resolver)
            );
            ProxyMethod pathProxyMethod = PROXY_METHOD_FACTORY.fromMethodReference(
                ProxyInterface::path,
                externalizedProperties(resolver)
            );

            Optional<String> javaVersion = resolver.resolve(
                javaVersionProxyMethod, 
                "java.version"
            );
            Optional<String> pathEnv = resolver.resolve(pathProxyMethod, "path");

            // From SystemPropertyResolver.
            assertNotNull(javaVersion);
            assertEquals(
                System.getProperty("java.version"), 
                javaVersion.get()
            );

            // From EnvironmentVariableResolver.
            assertNotNull(pathEnv);
            assertEquals(
                System.getenv("PATH"), 
                pathEnv.get()
            );
        }
        
        @Test
        @DisplayName(
            "should return empty Optional when property cannot be resolved from " + 
            "any of the ServiceLoader resolvers."
        )
        void test2() {
            ServiceLoaderResolver resolver = resolverToTest();
            ProxyMethod proxyMethod = PROXY_METHOD_FACTORY.fromMethodReference(
                ProxyInterface::notFound,
                externalizedProperties(resolver)
            );

            Optional<String> result = resolver.resolve(
                proxyMethod, 
                "non.found"
            );
            
            assertNotNull(result);
            assertFalse(result.isPresent());
        }
    }

    private static ServiceLoaderResolver resolverToTest() {
        return new ServiceLoaderResolver();
    }
    
    private static ExternalizedProperties externalizedProperties(Resolver... resolvers) {
        return ExternalizedProperties.builder().resolvers(resolvers).build();
    }

    private static interface ProxyInterface {
        @ExternalizedProperty("java.version")
        String javaVersion();

        @ExternalizedProperty("path")
        String path();

        @ExternalizedProperty("property")
        String notFound();
    }
}
