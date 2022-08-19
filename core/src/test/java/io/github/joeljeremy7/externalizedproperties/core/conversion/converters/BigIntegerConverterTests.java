package io.github.joeljeremy7.externalizedproperties.core.conversion.converters;

import io.github.joeljeremy7.externalizedproperties.core.ConversionResult;
import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperties;
import io.github.joeljeremy7.externalizedproperties.core.ExternalizedProperty;
import io.github.joeljeremy7.externalizedproperties.core.InvocationContext;
import io.github.joeljeremy7.externalizedproperties.core.testfixtures.InvocationContextUtils;
import io.github.joeljeremy7.externalizedproperties.core.testfixtures.InvocationContextUtils.InvocationContextTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BigIntegerConverterTests {
    private static final InvocationContextTestFactory<ProxyInterface> INVOCATION_CONTEXT_FACTORY =
        InvocationContextUtils.testFactory(ProxyInterface.class);

    @Nested
    class CanConvertToMethod {
        @Test
        @DisplayName("should return true when target type is a BigInteger")
        void test1() {
            BigIntegerConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(BigInteger.class);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return false when target type is not a BigInteger")
        void test2() {
            BigIntegerConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Integer.class);
            assertFalse(canConvert);
        }
    }

    @Nested
    class ConvertMethod {
        @Test
        @DisplayName("should convert value to a BigInteger")
        void test1() {
            BigIntegerConverter converter = converterToTest();

            InvocationContext context = INVOCATION_CONTEXT_FACTORY.fromMethodReference(
                ProxyInterface::bigIntegerProperty,
                externalizedProperties(converter)
            );

            ConversionResult<BigInteger> result = converter.convert(
                context,
                "1"
            );
            
            assertNotNull(result);
            assertEquals(BigInteger.valueOf(1), result.value());
        }

        @Test
        @DisplayName("should throw when value is not a valid BigInteger")
        void test2() {
            BigIntegerConverter converter = converterToTest();
            InvocationContext context = INVOCATION_CONTEXT_FACTORY.fromMethodReference(
                ProxyInterface::bigIntegerProperty,
                externalizedProperties(converter)
            );

            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(
                    context,
                    "invalid_value"
                )
            );
        }
    }

    private static BigIntegerConverter converterToTest() {
        return new BigIntegerConverter();
    }

    private static ExternalizedProperties externalizedProperties(
            BigIntegerConverter converterToTest
    ) {
        return ExternalizedProperties.builder()
            .converters(converterToTest)
            .build();
    }

    private static interface ProxyInterface {
        @ExternalizedProperty("property.biginteger")
        BigInteger bigIntegerProperty();
    }
}
