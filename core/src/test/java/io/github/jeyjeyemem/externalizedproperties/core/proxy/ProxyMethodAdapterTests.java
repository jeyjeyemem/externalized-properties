package io.github.jeyjeyemem.externalizedproperties.core.proxy;

import io.github.jeyjeyemem.externalizedproperties.core.ExternalizedProperty;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.Delimiter;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.ProxyMethodUtils;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.BasicProxyInterface;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.DefaultValueProxyInterface;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.NoAnnotationProxyInterface;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.OptionalProxyInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProxyMethodAdapterTests {
    @Nested
    class Constructor {
        @Test
        @DisplayName("should throw when method argument is null")
        public void test1() {
            assertThrows(
                IllegalArgumentException.class, 
                () -> new ProxyMethodAdapter(null)
            );
        }
    }

    @Nested
    class DeclaringClassMethod {
        @Test
        @DisplayName("should return method's declaring class.")
        public void test1() {
            String methodName = "property";

            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                methodName
            );

            Method method = ProxyMethodUtils.getMethod(
                BasicProxyInterface.class, 
                methodName
            );
            
            assertEquals(method.getDeclaringClass(), proxyMethod.declaringClass());
        }
    }

    @Nested
    class NameMethod {
        @Test
        @DisplayName("should return method name.")
        public void test1() {
            String methodName = "property";

            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                methodName
            );
            
            assertEquals(methodName, proxyMethod.name());
        }
    }

    @Nested
    class ExternalizedPropertyAnnotationMethod {
        @Test
        @DisplayName("should return @ExternalizedProperty instance when method is annotated.")
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Optional<ExternalizedProperty> annotation = 
                proxyMethod.externalizedPropertyAnnotation();
            
            // BasicProxyInterface.property is annotated with @ExternalizedProperty
            assertTrue(annotation.isPresent());
        }

        @Test
        @DisplayName("should return an empty Optional when method is not annotated.")
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                NoAnnotationProxyInterface.class, 
                "propertyWithNoAnnotationButWithDefaultValue"
            );

            Optional<ExternalizedProperty> annotation = 
                proxyMethod.externalizedPropertyAnnotation();
            
            // NoAnnotationProxyInterface.propertyWithNoAnnotationButWithDefaultValue 
            // is not annotated with @ExternalizedProperty
            assertFalse(annotation.isPresent());
        }
    }

    @Nested
    class ExternalizedPropertyNameMethod {
        @Test
        @DisplayName("should return name of the property.")
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );
            
            Optional<String> propertyName = proxyMethod.externalizedPropertyName();
            
            assertTrue(propertyName.isPresent());

            // See BasicProxyInterface.property @ExternalizedProperty annotation value.
            assertEquals("property", propertyName.get());
        }
    }

    @Nested
    class AnnotationsMethod {
        @Test
        @DisplayName(
            "should return all annotations the method is annotated with."
        )
        public void test() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );
        
            Annotation[] annotations = proxyMethod.annotations();
            
            assertTrue(annotations.length == 1);
            assertEquals(ExternalizedProperty.class, annotations[0].annotationType());
        }

        @Test
        @DisplayName(
            "should return empty array when method is not annotated with any annotations."
        )
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                NoAnnotationProxyInterface.class, 
                "propertyWithNoAnnotationAndNoDefaultValue"
            );

            Annotation[] annotations = proxyMethod.annotations();

            assertTrue(annotations.length == 0);
        }
    }

    @Nested
    class FindAnnotationMethod {
        @Test
        @DisplayName(
            "should return the annotation " + 
            "when method is annotation with the specified annotation class."
        )
        public void test() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );
        
            Optional<ExternalizedProperty> annotation = 
                proxyMethod.findAnnotation(ExternalizedProperty.class);
            
            assertTrue(annotation.isPresent());
        }

        @Test
        @DisplayName(
            "should return an empty Optional " + 
            "when method is not annotated with the specified annotation class."
        )
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            // Method not annotated with @Delimiter.
            Optional<Delimiter> nonExistentAnnotation =
                proxyMethod.findAnnotation(Delimiter.class);
            
            assertFalse(nonExistentAnnotation.isPresent());
        }
    }

    @Nested
    class HasAnnotationMethod {
        @Test
        @DisplayName(
            "should return true when method is annotated with the specified annotation class."
        )
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            assertTrue(
                proxyMethod.hasAnnotation(ExternalizedProperty.class)
            );
        }

        @Test
        @DisplayName(
            "should return false when method is annotated with the specified annotation class."
        )
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            assertFalse(
                proxyMethod.hasAnnotation(Delimiter.class)
            );
        }
    }

    @Nested
    class ReturnTypeMethod {
        @Test
        @DisplayName("should return method's return type.")
        public void test1() {
            Method proxyInterfaceMethod = ProxyMethodUtils.getMethod(
                BasicProxyInterface.class, 
                "property"
            );

            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Class<?> returnType = proxyMethod.returnType();

            assertEquals(proxyInterfaceMethod.getReturnType(), returnType);
        }
    }

    @Nested
    class GenericReturnTypeMethod {
        @Test
        @DisplayName("should return method's generic return type.")
        public void test1() {
            Method proxyInterfaceMethod = ProxyMethodUtils.getMethod(
                BasicProxyInterface.class, 
                "property"
            );

            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Type genericReturnType = proxyMethod.genericReturnType();

            assertEquals(proxyInterfaceMethod.getGenericReturnType(), genericReturnType);
        }
    }

    @Nested
    class ParameterTypesMethod {
        @Test
        @DisplayName("should return method's parameter types.")
        public void test1() {
            Method proxyInterfaceMethod = ProxyMethodUtils.getMethod(
                DefaultValueProxyInterface.class, 
                "propertyWithDefaultValueParameter",
                String.class
            );

            ProxyMethod proxyMethod = proxyMethod(
                DefaultValueProxyInterface.class, 
                "propertyWithDefaultValueParameter",
                String.class
            );

            Class<?>[] parameterTypes = proxyMethod.parameterTypes();

            assertArrayEquals(proxyInterfaceMethod.getParameterTypes(), parameterTypes);
        }
    }

    @Nested
    class GenericParameterTypesMethod {
        @Test
        @DisplayName("should return method's generic parameter types.")
        public void test1() {
            Method proxyInterfaceMethod = ProxyMethodUtils.getMethod(
                DefaultValueProxyInterface.class, 
                "propertyWithDefaultValueParameter",
                String.class
            );

            ProxyMethod proxyMethod = proxyMethod(
                DefaultValueProxyInterface.class, 
                "propertyWithDefaultValueParameter",
                String.class
            );

            Type[] parameterTypes = proxyMethod.genericParameterTypes();

            assertArrayEquals(proxyInterfaceMethod.getGenericParameterTypes(), parameterTypes);
        }
    }

    @Nested
    class HasReturnTypeMethod {
        @Test
        @DisplayName("should return true when method's return type matches.")
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            assertTrue(
                proxyMethod.hasReturnType(String.class)
            );
        }

        @Test
        @DisplayName("should return false when method's return type does not match.")
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            assertFalse(
                proxyMethod.hasReturnType(Integer.class)
            );
        }
    }

    @Nested
    class HasReturnTypeMethodWithTypeArgument {
        @Test
        @DisplayName("should return true when method's return type matches.")
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Type type = String.class;

            assertTrue(
                proxyMethod.hasReturnType(type)
            );
        }

        @Test
        @DisplayName("should return false when method's return type does not match.")
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Type type = Integer.class;

            assertFalse(
                proxyMethod.hasReturnType(type)
            );
        }
    }

    @Nested
    class GenericReturnTypeParametersMethod {
        @Test
        @DisplayName(
            "should return method return type's generic type parameters " + 
            "when method's return type is a generic type."
        )
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            Type[] genericTypeParameters = 
                proxyMethod.returnTypeGenericTypeParameters();

            // Optional has <String> generic type parameter
            assertTrue(genericTypeParameters.length > 0);
            assertEquals(String.class, genericTypeParameters[0]);
        }

        @Test
        @DisplayName(
            "should return empty generic type parameter list " + 
            "when method's return type is not a generic type."
        )
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Type[] genericTypeParameters = 
                proxyMethod.returnTypeGenericTypeParameters();

            // BasicProxyInterface.property returns a String which is not generic.
            assertTrue(genericTypeParameters.length == 0);
        }
    }

    @Nested
    class GenericReturnTypeParameterMethod {
        @Test
        @DisplayName(
            "should return method return type's generic type parameter " + 
            "when method's return type is a generic type."
        )
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            Optional<Type> genericTypeParameter = 
                proxyMethod.returnTypeGenericTypeParameter(0);

            // Optional has <String> generic type parameter
            assertTrue(genericTypeParameter.isPresent());
            assertEquals(String.class, genericTypeParameter.get());
        }

        @Test
        @DisplayName(
            "should return empty Optional when method's return type " + 
            "is a generic type but requested type parameter index is out of bounds."
        )
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            // Index out of bounds. 
            Optional<Type> genericTypeParameter = 
                proxyMethod.returnTypeGenericTypeParameter(99);

            assertFalse(genericTypeParameter.isPresent());
        }

        @Test
        @DisplayName(
            "should return empty Optional" + 
            "when method's return type is not a generic type."
        )
        public void test3() {
            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            Optional<Type> genericTypeParameter = 
                proxyMethod.returnTypeGenericTypeParameter(0);

            // BasicProxyInterface.property returns a String which is not generic.
            assertFalse(genericTypeParameter.isPresent());
        }
    }
    
    @Nested
    class IsDefaultInterfaceMethodMethod {
        @Test
        @DisplayName("should return true when method is a default interface method.")
        public void test1() {
            ProxyMethod proxyMethod = proxyMethod(
                DefaultValueProxyInterface.class, 
                "propertyWithDefaultValue"
            );

            assertTrue(
                proxyMethod.isDefaultInterfaceMethod()
            );
        }

        @Test
        @DisplayName("should return false when method is not a default interface method.")
        public void test2() {
            ProxyMethod proxyMethod = 
                proxyMethod(
                    BasicProxyInterface.class, 
                    "property"
                );

            assertFalse(
                proxyMethod.isDefaultInterfaceMethod()
            );
        }
    }

    
    @Nested
    class MethodSignatureStringMethod {
        @Test
        @DisplayName("should return method's signature string.")
        public void test1() {
            Method proxyInterfaceMethod = ProxyMethodUtils.getMethod(
                BasicProxyInterface.class, 
                "property"
            );

            ProxyMethod proxyMethod = proxyMethod(
                BasicProxyInterface.class, 
                "property"
            );

            String methodSignature = proxyMethod.methodSignatureString();

            // Since method is non-generic,
            // Method.toGenericString() and Method.toString() returns the same signature.
            assertEquals(proxyInterfaceMethod.toGenericString(), methodSignature);
            assertEquals(proxyInterfaceMethod.toString(), methodSignature);
        }

        @Test
        @DisplayName("should return method's generic signature string.")
        public void test2() {
            Method optionalProperty = ProxyMethodUtils.getMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            ProxyMethod proxyMethod = proxyMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            String methodSignature = proxyMethod.methodSignatureString();

            assertEquals(optionalProperty.toGenericString(), methodSignature);
            // Method.toString() does not include generic types.
            assertNotEquals(optionalProperty.toString(), methodSignature);
        }
    }

    @Nested
    class ToString {
        @Test
        @DisplayName("should match methodSignatureString() method.")
        public void test2() {
            ProxyMethod proxyMethod = proxyMethod(
                OptionalProxyInterface.class, 
                "optionalProperty"
            );

            String methodSignature = proxyMethod.methodSignatureString();

            assertEquals(methodSignature, proxyMethod.toString());
        }
    }

    private ProxyMethod proxyMethod(
            Class<?> proxyInterface, 
            String methodName,
            Class<?>... parameterTypes
    ) {
        Method method = ProxyMethodUtils.getMethod(
            proxyInterface, 
            methodName, 
            parameterTypes
        );
        return new ProxyMethodAdapter(method);
    }
}
