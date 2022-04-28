package io.github.joeljeremy7.externalizedproperties.core.testentities.proxy;

import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperty;
import io.github.joeljeremy7.externalizedproperties.core.conversion.Delimiter;
import io.github.joeljeremy7.externalizedproperties.core.conversion.StripEmptyValues;

import java.util.Optional;

public interface ArrayProxyInterface {
    @ExternalizedProperty("property.array")
    String[] arrayProperty();

    @ExternalizedProperty("property.array.object")
    Object[] arrayPropertyObject();

    @ExternalizedProperty("property.array.custom.delimiter")
    @Delimiter("|")
    String[] arrayCustomDelimiter();

    @ExternalizedProperty("property.array.stripempty")
    @StripEmptyValues
    String[] arrayPropertyStripEmpty();

    @ExternalizedProperty("property.array.integer.wrapper")
    Integer[] arrayIntegerWrapper();

    @ExternalizedProperty("property.array.integer.primitive")
    int[] arrayIntegerPrimitive();

    @ExternalizedProperty("property.array.generic")
    Optional<String>[] arrayPropertyGeneric();

    @ExternalizedProperty("property.array.generic.nested")
    Optional<Optional<String>>[] arrayPropertyNestedGeneric();

    @ExternalizedProperty("property.array.generic.wildcard")
    Optional<?>[] arrayPropertyGenericWildcard();

    @ExternalizedProperty("property.array.T")
    <T> T[] arrayPropertyT();
}