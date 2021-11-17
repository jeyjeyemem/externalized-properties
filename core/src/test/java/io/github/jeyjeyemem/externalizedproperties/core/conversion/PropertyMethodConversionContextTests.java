package io.github.jeyjeyemem.externalizedproperties.core.conversion;

import io.github.jeyjeyemem.externalizedproperties.core.ResolvedProperty;
import io.github.jeyjeyemem.externalizedproperties.core.internal.InternalConverter;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.StubExternalizedPropertyMethodInfo;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.OptionalProxyInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropertyMethodConversionContextTests {
    @Nested
    class Constructor {
        @Test
        @DisplayName("should throw when converter argument is null")
        public void test1() {
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );

            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    null,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value"), 
                    Optional.class,
                    String.class
                )
            );
        }

        @Test
        @DisplayName("should throw when externalized property method info argument is null")
        public void test2() {
            Converter converter = new InternalConverter();

            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    converter,
                    null, 
                    ResolvedProperty.with("name", "value"), 
                    Optional.class,
                    String.class
                )
            );
        }

        @Test
        @DisplayName("should throw when resolved property argument is null")
        public void test3() {
            Converter converter = new InternalConverter();

            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    null, 
                    propertyMethod.genericReturnType(),
                    propertyMethod.genericReturnTypeGenericTypeParameters()
                )
            );
        }

        @Test
        @DisplayName("should throw when expected type argument is null")
        public void test5() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value"), 
                    null,
                    propertyMethod.genericReturnTypeGenericTypeParameters()
                )
            );
        }

        @Test
        @DisplayName(
            "should throw when expected type generic type parameters argument is null"
        )
        public void test6() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value"), 
                    propertyMethod.genericReturnType(),
                    (Type[])null
                )
            );
        }

        @Test
        @DisplayName(
            "should throw when expected type generic type parameters varargs argument is null"
        )
        public void test7() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> new PropertyMethodConversionContext(
                    converter,
                    propertyMethod,
                    ResolvedProperty.with("name", "value"),
                    propertyMethod.genericReturnType(),
                    (Type[])null
                )
            );
        }

        @Test
        @DisplayName(
            "should use property method's generic return type and generic return type generic type parameters " + 
            "when expected type and expected type generic type parameters are not set."
        )
        public void test8() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            PropertyMethodConversionContext context =
                new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value")
                );

            assertEquals(propertyMethod.genericReturnType(), context.expectedType());
            assertArrayEquals(
                propertyMethod.genericReturnTypeGenericTypeParameters(), 
                context.expectedTypeGenericTypeParameters()
            );
        }

        @Test
        @DisplayName(
            "should allow expected type to be different from property method's return type."
        )
        public void test9() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            PropertyMethodConversionContext context =
                new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value"),
                    List.class,
                    String.class
                );

            assertNotEquals(propertyMethod.returnType(), context.expectedType());
        }

        @Test
        @DisplayName(
            "should allow generic expected type generic type parameters to be different " + 
            "from property method's generic return type generic type parameters."
        )
        public void test10() {
            Converter converter = new InternalConverter();
            StubExternalizedPropertyMethodInfo propertyMethod = 
                StubExternalizedPropertyMethodInfo.fromMethod(
                    OptionalProxyInterface.class, 
                    "optionalProperty"
                );
            
            PropertyMethodConversionContext context =
                new PropertyMethodConversionContext(
                    converter,
                    propertyMethod, 
                    ResolvedProperty.with("name", "value"),
                    List.class,
                    Integer.class
                );

            assertArrayEquals(
                new Class<?>[] { Integer.class }, 
                context.expectedTypeGenericTypeParameters()
            );
        }
    }
}
