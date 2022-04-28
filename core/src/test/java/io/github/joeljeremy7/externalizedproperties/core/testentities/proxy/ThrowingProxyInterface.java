package io.github.joeljeremy7.externalizedproperties.core.testentities.proxy;

import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperty;

public interface ThrowingProxyInterface {
    @ExternalizedProperty("property.that.throws")
    default String throwRuntimeException() {
        throw new RuntimeException("Hi from ThrowingProxyInterface.throwingProperty!");
    }

    @ExternalizedProperty("property.that.throws")
    default String throwException() throws Exception {
        throw new Exception("Hi from ThrowingProxyInterface.throwingProperty!");
    }
}