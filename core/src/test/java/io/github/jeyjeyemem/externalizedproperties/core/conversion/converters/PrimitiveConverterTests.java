package io.github.jeyjeyemem.externalizedproperties.core.conversion.converters;

import io.github.jeyjeyemem.externalizedproperties.core.ConversionResult;
import io.github.jeyjeyemem.externalizedproperties.core.ConverterProvider;
import io.github.jeyjeyemem.externalizedproperties.core.ExternalizedProperties;
import io.github.jeyjeyemem.externalizedproperties.core.internal.conversion.RootConverter;
import io.github.jeyjeyemem.externalizedproperties.core.proxy.ProxyMethod;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.ListProxyInterface;
import io.github.jeyjeyemem.externalizedproperties.core.testentities.proxy.PrimitiveProxyInterface;
import io.github.jeyjeyemem.externalizedproperties.core.testfixtures.ProxyMethodUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimitiveConverterTests {
    @Nested
    class ProviderMethod {
        @Test
        @DisplayName("should not return null.")
        public void test1() {
            ConverterProvider<PrimitiveConverter> provider = 
                PrimitiveConverter.provider();

            assertNotNull(provider);
        }

        @Test
        @DisplayName("should return an instance on get.")
        public void test2() {
            ConverterProvider<PrimitiveConverter> provider = 
                PrimitiveConverter.provider();
            
            ExternalizedProperties externalizedProperties = 
                ExternalizedProperties.builder()
                    .withDefaultResolvers()
                    .converters(provider)
                    .build();
            
            assertNotNull(
                provider.get(
                    externalizedProperties,
                    new RootConverter(externalizedProperties, provider)
                )
            );
        }
    }

    @Nested
    class CanConvertToMethod {
        @Test
        @DisplayName("should return false when target type is null.")
        void test1() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(null);
            assertFalse(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is an Integer.")
        void test2() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Integer.class);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a primitive int.")
        void test3() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Integer.TYPE);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a Long.")
        void test4() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Long.class);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a primitive long.")
        void test5() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Long.TYPE);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a Float.")
        void test6() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Float.class);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a primitive float.")
        void test7() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Float.TYPE);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a Double.")
        void test8() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Double.class);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return true when target type is a primitive double.")
        void test9() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(Double.TYPE);
            assertTrue(canConvert);
        }

        @Test
        @DisplayName("should return false when target type is not a primitive type.")
        void test10() {
            PrimitiveConverter converter = converterToTest();
            boolean canConvert = converter.canConvertTo(List.class);
            assertFalse(canConvert);
        }
    }

    @Nested
    class ConvertMethod {
        @Test
        @DisplayName("should return skip result when target type is not a primitive type.")
        void test1() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod proxyMethod = 
                ProxyMethodUtils.fromMethod(
                    ListProxyInterface.class,
                    "listProperty" // This method returns a List class
                );
            
            ConversionResult<?> result =  converter.convert(
                proxyMethod,
                "a,b,c"
            );
            assertEquals(ConversionResult.skip(), result);
        }

        @Test
        @DisplayName("should convert value to an Integer or primitive int.")
        void test2() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "integerWrapperProperty" // This method returns a Integer wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "intPrimitiveProperty" // This method returns an int primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();

            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Integer);
            assertTrue(primitiveValue instanceof Integer);

            assertEquals(1, (Integer)wrapperValue);
            assertEquals(2, (int)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Long or primitive long.")
        void test3() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "longWrapperProperty" // This method returns a Long wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "longPrimitiveProperty" // This method returns an long primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Long);
            assertTrue(primitiveValue instanceof Long);

            assertEquals(1L, (Long)wrapperValue);
            assertEquals(2L, (long)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Float or primitive float.")
        void test4() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "floatWrapperProperty" // This method returns a Float wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "floatPrimitiveProperty" // This method returns an float primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1.0"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2.0"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Float);
            assertTrue(primitiveValue instanceof Float);

            assertEquals(1.0F, (Float)wrapperValue);
            assertEquals(2.0F, (float)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Double or primitive double.")
        void test5() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "doubleWrapperProperty" // This method returns a Double wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "doublePrimitiveProperty" // This method returns a double primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1.0"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2.0"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Double);
            assertTrue(primitiveValue instanceof Double);

            assertEquals(1.0D, (Double)wrapperValue);
            assertEquals(2.0D, (double)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Short or primitive short.")
        void test6() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "shortWrapperProperty" // This method returns a Short wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "shortPrimitiveProperty" // This method returns a short primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Short);
            assertTrue(primitiveValue instanceof Short);

            assertEquals((short)1, (Short)wrapperValue);
            assertEquals((short)2, (short)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Boolean or primitive boolean.")
        void test7() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "booleanWrapperProperty" // This method returns a Boolean wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "booleanPrimitiveProperty" // This method returns a boolean primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "true"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "false"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Boolean);
            assertTrue(primitiveValue instanceof Boolean);

            assertEquals(true, (Boolean)wrapperValue);
            assertEquals(false, (boolean)primitiveValue);
        }

        @Test
        @DisplayName("should convert to false when property value is not a valid Boolean/boolean.")
        void test8() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "booleanWrapperProperty" // This method returns a Boolean wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "booleanPrimitiveProperty" // This method returns a primitive boolean
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethodInfo,
                "invalid_boolean"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethodInfo,
                "invalid_boolean"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Boolean);
            assertTrue(primitiveValue instanceof Boolean);

            assertEquals(false, (Boolean)wrapperValue);
            assertEquals(false, (boolean)primitiveValue);
        }

        @Test
        @DisplayName("should convert value to a Byte or primitive byte.")
        void test9() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "byteWrapperProperty" // This method returns a Byte wrapper class
                );

            ProxyMethod primitiveProxyMethod = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "bytePrimitiveProperty" // This method returns a byte primitive
                );

            ConversionResult<?> wrapperResult = converter.convert(
                wrapperProxyMethod,
                "1"
            );
            ConversionResult<?> primitiveResult = converter.convert(
                primitiveProxyMethod,
                "2"
            );
            
            assertNotNull(wrapperResult);
            assertNotNull(primitiveResult);
            Object wrapperValue = wrapperResult.value();
            Object primitiveValue = primitiveResult.value();
            
            assertNotNull(wrapperValue);
            assertNotNull(primitiveValue);

            assertTrue(wrapperValue instanceof Byte);
            assertTrue(primitiveValue instanceof Byte);

            assertEquals((byte)1, (Byte)wrapperValue);
            assertEquals((byte)2, (byte)primitiveValue);
        }

        @Test
        @DisplayName("should throw when property value is not a valid Byte/byte.")
        void test10() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "byteWrapperProperty" // This method returns a Byte wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "bytePrimitiveProperty" // This method returns a primitive byte
                );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_byte")
            );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_byte")
            );
        }

        @Test
        @DisplayName("should throw when property value is not a valid Short/short.")
        void test11() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "shortWrapperProperty" // This method returns Short wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "shortPrimitiveProperty" // This method returns a primitive short
                );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_short")
            );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_short")
            );
        }

        @Test
        @DisplayName("should throw when property value is not a valid Integer/int.")
        void test12() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "integerWrapperProperty" // This method returns an Integer wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "intPrimitiveProperty" // This method returns a primitive int
                );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_int")
            );
            
            assertThrows(
                NumberFormatException.class, 
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_int")
            );
        }

        @Test
        @DisplayName("should throw when property value is not a valid Long/long.")
        void test13() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "longWrapperProperty" // This method returns Long wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "longPrimitiveProperty" // This method returns a primitive long
                );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_long")
            );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_long")
            );
        }

        @Test
        @DisplayName("should throw when property value is not a valid Float/float.")
        void test14() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "floatWrapperProperty" // This method returns a Float wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "floatPrimitiveProperty" // This method returns a primitive float
                );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_float")
            );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_float")
            );
        }
        
        @Test
        @DisplayName("should throw when property value is not a valid Double/double.")
        void test15() {
            PrimitiveConverter converter = converterToTest();

            ProxyMethod wrapperProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "doubleWrapperProperty" // This method returns a Double wrapper class
                );

            ProxyMethod primitiveProxyMethodInfo = 
                ProxyMethodUtils.fromMethod(
                    PrimitiveProxyInterface.class,
                    "doublePrimitiveProperty" // This method returns a primitive double
                );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(wrapperProxyMethodInfo, "invalid_double")
            );
            
            assertThrows(
                NumberFormatException.class,
                () -> converter.convert(primitiveProxyMethodInfo, "invalid_double")
            );
        }
    }

    private PrimitiveConverter converterToTest() {
        return new PrimitiveConverter();
    }
}
