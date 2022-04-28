package io.github.joeljeremy7.externalizedproperties.core.testfixtures;

import io.github.joeljeremy7.externalizedproperties.core.internal.proxy.ProxyMethodAdapter;
import io.github.joeljeremy7.externalizedproperties.core.proxy.ProxyMethod;

import java.lang.reflect.Method;

/**
 * Utility methods to manage {@link ProxyMethod}s.
 */
public class ProxyMethodUtils {
    private ProxyMethodUtils(){}

    public static ProxyMethod fromMethod(
            Class<?> proxyInterface,
            String methodName,
            Class<?>... methodParameterTypes
    ) {
        Method method = getMethod(
            proxyInterface, 
            methodName, 
            methodParameterTypes
        );
        return new ProxyMethodAdapter(method);
    }

    public static ProxyMethod fromMethod(Method proxyInterfaceMethod) {
        return new ProxyMethodAdapter(proxyInterfaceMethod);
    }
    
    public static Method getMethod(
            Class<?> clazz, 
            String name, 
            Class<?>... parameterTypes
    ) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to find method.", e);
        }
    }
}