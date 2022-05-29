# Property Post-Processing

Externalized Properties provides a mechanism to selectively apply post-processing to resolved properties.

This feature may be used to apply transformations to resolved properties such as automatic decryption, masking, validation, etc. This can be achieved via a combination of annotations and [Processor](../core/src/main/java/io/github/joeljeremy7/externalizedproperties/core/Processor.java)s e.g.

```java
public interface ApplicationProperties {
    @ExternalizedProperty("encrypted.property.aes")
    @Decrypt("MyAESDecryptor")
    String aesEncryptedProperty();

    @ExternalizedProperty("encrypted.property.rsa")
    @Decrypt("MyRSADecryptor")
    String rsaEncryptedProperty();
}

public static void main(String[] args) {
    // Processor to decrypt @Decrypt("MyAESDecryptor")
    DecryptProcessor aesDecryptProcessor = aesDecryptProcessor();
    // Processor to decrypt @Decrypt("MyRSADecryptor")
    DecryptProcessor rsaDecryptProcessor = rsaDecryptProcessor();

    ExternalizedProperties externalizedProperties = ExternalizedProperties.builder()
        .defaults()
        .processors(aesDecryptProcessor, rsaDecryptProcessor)
        .build();

     // Proxied interface.
    ApplicationProperties props = externalizedProperties.initialize(ApplicationProperties.class);

    // Automatically decrypted via @Decrypt/DecryptProcessor.
    String decryptedAesProperty = props.aesEncryptedProperty();
    String decryptedRsaProperty = props.rsaEncryptedProperty();
}

private static ProcessorProvider<DecryptProcessor> aesDecryptProcessor() {
    return new DecryptProcessor(
        JceDecryptor.factory().symmetric(
            "MyAESDecryptor",
            "AES/GCM/NoPadding", 
            getSecretKey(),
            getGcmParameterSpec()
        )
    );
}

private static ProcessorProvider<DecryptProcessor> rsaDecryptProcessor() {
    return new DecryptProcessor(
        JceDecryptor.factory().asymmetric(
            "MyRSADecryptor",
            "RSA", 
            getPrivateKey()
        )
    );
}
```

## 🚀 Custom Processors

### 1. Create a processor by implementing the [Processor](../core/src/main/java/io/github/joeljeremy7/externalizedproperties/core/Processor.java) interface

```java
public class Base64EncodeProcessor implements Processor {
    @Override
    public String process(ProxyMethod proxyMethod, String valueToProcess) {
        return base64Encode(proxyMethod, valueToProcess);
    }
}
```

### 2. Create an annotation to annotate methods which should go through the processor

Requirements for custom processor annotations:  

a. The annotation should target methods  
b. The annotation should have runtime retention  
c. The annotation should be annotated with the [ProcessWith](../core/src/main/java/io/github/joeljeremy7/externalizedproperties/core/processing/ProcessWith.java) annotation

e.g.

```java
@ProcessWith(Base64EncodeProcessor.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Encode {}
```

### 3. Register the processor and use custom processor annotation

```java
public interface ApplicationProperties {
    @ExternalizedProperty("my.property")
    @Base64Encode
    String myProperty();
}

public static void main(String[] args) {
    ExternalizedProperties externalizedProperties = ExternalizedProperties.builder()
        .defaults()
        // Register custom processor to process @Base64Encode
        .processors(new Base64EncodeProcessor())
        .build();

    ApplicationProperties props = externalizedProperties.initialize(ApplicationProperties.class);

    // Automatically encoded to Base64.
    String base64EncodedProperty = props.myProperty();
}
```