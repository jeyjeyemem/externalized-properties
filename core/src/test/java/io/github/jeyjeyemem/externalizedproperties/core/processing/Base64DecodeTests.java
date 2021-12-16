package io.github.jeyjeyemem.externalizedproperties.core.processing;

import io.github.jeyjeyemem.externalizedproperties.core.exceptions.ProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base64DecodeTests {
    @Nested
    class Constructor {
        @Test
        @DisplayName("should throw when decoder argument is null")
        public void test1() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new Base64Decode((Base64.Decoder)null)
            );
        }
    }

    @Nested
    class ProcessPropertyMethod {
        @Test
        @DisplayName("should throw when property argument is null")
        public void test1() {
            Base64Decode base64Decode = new Base64Decode();
            assertThrows(
                IllegalArgumentException.class, 
                () -> base64Decode.processProperty(null)
            );
        }

        @Test
        @DisplayName("should apply base 64 decoding to property")
        public void test2() {
            String property = "test";
            String base64Property = base64Encode(property, Base64.getEncoder());
            
            Base64Decode base64Decode = new Base64Decode();
            String decoded = base64Decode.processProperty(base64Property);

            assertEquals(property, decoded);
        }

        @Test
        @DisplayName("should apply base 64 decoding to property using configured decoder")
        public void test3() {
            String property = "test";
            Base64.Decoder decoder = Base64.getUrlDecoder();
            
            String base64Property = base64Encode(property, Base64.getUrlEncoder());
            
            Base64Decode base64Decode = new Base64Decode(decoder);
            String decoded = base64Decode.processProperty(base64Property);

            assertEquals(property, decoded);
        }

        @Test
        @DisplayName("should wrap exceptions in ProcessingException and propagate.")
        public void test4() {
            Base64.Decoder decoder = Base64.getDecoder();
            
            String invalidBase64 = "%%%";
            
            Base64Decode base64Decode = new Base64Decode(decoder);

            assertThrows(
                ProcessingException.class, 
                () -> base64Decode.processProperty(invalidBase64)
            );
        }

        private String base64Encode(String property, Base64.Encoder encoder) {
            return new String(encoder.encode(property.getBytes()));
        }
    }
}
