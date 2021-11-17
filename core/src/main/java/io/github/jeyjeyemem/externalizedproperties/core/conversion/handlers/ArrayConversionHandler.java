package io.github.jeyjeyemem.externalizedproperties.core.conversion.handlers;

import io.github.jeyjeyemem.externalizedproperties.core.ExternalizedPropertyMethodInfo;
import io.github.jeyjeyemem.externalizedproperties.core.ResolvedProperty;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.ConversionContext;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.PropertyMethodConversionContext;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.ConversionHandler;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.Converter;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.annotations.Delimiter;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.annotations.StripEmptyValues;
import io.github.jeyjeyemem.externalizedproperties.core.exceptions.ConversionException;
import io.github.jeyjeyemem.externalizedproperties.core.internal.utils.TypeUtilities;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.utils.Arguments.requireNonNull;

/**
 * Supports conversion of resolved properties to an array.
 * 
 * @implNote By default, this uses ',' as default delimiter when splitting resolved property values.
 * This can overriden by annotating the externalized property method with {@link Delimiter} annotation
 * in which case the {@link Delimiter#value()} attribute will be used as the delimiter.
 * 
 * @implNote If stripping of empty values from the array is desired, 
 * the method can be annotated with the {@link StripEmptyValues} annotation. 
 */
public class ArrayConversionHandler implements ConversionHandler<Object[]> {
    private static final String DEFAULT_DELIMITER = ",";

    /** {@inheritDoc} */
    @Override
    public boolean canConvertTo(Class<?> expectedType) {
        return expectedType != null && expectedType.isArray();
    }

    @Override
    public Object[] convert(ConversionContext context) {
        requireNonNull(context, "context");
        return convertInternal(context, null);
    }

    /** {@inheritDoc} */
    @Override
    public Object[] convert(PropertyMethodConversionContext context) {
        requireNonNull(context, "context");
        return convertInternal(context, context.externalizedPropertyMethodInfo());
    }

    // ExternalizedPropertyMethodInfo may be null when resolution/conversion was not initiated 
    // from a proxy interface.
    private Object[] convertInternal(
            ConversionContext context, 
            ExternalizedPropertyMethodInfo externalizedPropertyMethodInfo
    ) {
        try {    
            // Do not allow T[].
            throwIfArrayHasTypeVariables(context);

            final String[] values = getValues(context, externalizedPropertyMethodInfo);

            Class<?> rawArrayComponentType = context.rawExpectedType().getComponentType();
            if (rawArrayComponentType == null) {
                throw new ConversionException("Expected type is not an array.");
            }
            
            // If array is String[] or Object[], return the string values.
            if (String.class.equals(rawArrayComponentType) || Object.class.equals(rawArrayComponentType)) {
                return values;
            }

            // Generic array component type handling.
            GenericArrayType genericArrayType = TypeUtilities.asGenericArrayType(context.expectedType());
            if (genericArrayType != null) {
                Type genericArrayComponentType = genericArrayType.getGenericComponentType();
                
                Type[] genericTypeParametersOfGenericArrayComponentType = 
                    TypeUtilities.getTypeParameters(genericArrayComponentType);
            
                return convertValuesToArrayComponentType(
                    context,
                    values, 
                    genericArrayComponentType,
                    genericTypeParametersOfGenericArrayComponentType
                );
            }

            // Just convert to raw type.
            return convertValuesToArrayComponentType(
                context,
                values, 
                rawArrayComponentType,
                new Type[0]
            );
        } catch (Exception ex) {
            throw new ConversionException(
                String.format(
                    "Failed to convert property %s to an array. Property value: %s",
                    context.resolvedProperty().name(),
                    context.resolvedProperty().value()
                ),  
                ex
            );
        }
    }

    private Object[] convertValuesToArrayComponentType(
            ConversionContext context, 
            String[] values,
            Type arrayComponentType, 
            Type[] arrayComponentTypeGenericTypeParameters
    ) {
        Converter converter = context.converter();

        // Convert and return values.
        return IntStream.range(0, values.length).mapToObj(i -> {
            String value = values[i];
            return converter.convert(
                new ConversionContext(
                    converter,
                    ResolvedProperty.with(
                        indexedName(context.resolvedProperty().name(), i),
                        value
                    ), 
                    arrayComponentType,
                    arrayComponentTypeGenericTypeParameters
                )
            );
        })
        .toArray(arrayLength -> (Object[])Array.newInstance(
            TypeUtilities.getRawType(arrayComponentType), 
            arrayLength
        ));
    }

    private String[] getValues(
            ConversionContext context,
            ExternalizedPropertyMethodInfo externalizedPropertyMethodInfo
    ) {
        String propertyValue = context.resolvedProperty().value();
        if (propertyValue.isEmpty()) {
            return new String[0];
        }

        if (externalizedPropertyMethodInfo != null) {
            // Determine delimiter.
            String delimiter = externalizedPropertyMethodInfo.findAnnotation(Delimiter.class)
                .map(d -> d.value())
                .orElse(DEFAULT_DELIMITER);

            if (externalizedPropertyMethodInfo.hasAnnotation(StripEmptyValues.class)) {
                // Filter empty values.
                return Arrays.stream(propertyValue.split(delimiter))
                    .filter(v -> !v.isEmpty())
                    .toArray(String[]::new);
            }
            return propertyValue.split(Pattern.quote(delimiter));
        }

        return propertyValue.split(Pattern.quote(DEFAULT_DELIMITER));
    }

    private void throwIfArrayHasTypeVariables(ConversionContext context) {
        Type genericExpectedType = context.expectedType();
        GenericArrayType genericArray = TypeUtilities.asGenericArrayType(genericExpectedType);
        if (genericArray != null) {
            if (TypeUtilities.isTypeVariable(genericArray.getGenericComponentType())) {
                throw new ConversionException(
                    "Type variables e.g. T[] are not supported."
                );
            }
        }
    }

    private static String indexedName(String name, int index) {
        return name + "[" + index + "]";
    }
}
