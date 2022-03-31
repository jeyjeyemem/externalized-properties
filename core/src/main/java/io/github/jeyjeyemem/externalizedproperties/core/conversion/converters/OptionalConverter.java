package io.github.jeyjeyemem.externalizedproperties.core.conversion.converters;

import io.github.jeyjeyemem.externalizedproperties.core.ConversionContext;
import io.github.jeyjeyemem.externalizedproperties.core.ConversionResult;
import io.github.jeyjeyemem.externalizedproperties.core.Converter;
import io.github.jeyjeyemem.externalizedproperties.core.TypeUtilities;
import io.github.jeyjeyemem.externalizedproperties.core.conversion.ConversionException;

import java.lang.reflect.Type;
import java.util.Optional;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.Arguments.requireNonNull;

/**
 * Supports conversion of values to an {@link Optional} instance.
 */
public class OptionalConverter implements Converter<Optional<?>> {
    
    /** {@inheritDoc} */
    @Override
    public boolean canConvertTo(Class<?> targetType) {
        return Optional.class.equals(targetType);
    }

    /** {@inheritDoc} */
    @Override
    public ConversionResult<? extends Optional<?>> convert(ConversionContext context) {
        requireNonNull(context, "context");

        Type[] genericTypeParams = context.targetTypeGenericTypeParameters();
        
        // Assume initially as Optional of string type.
        Type targetOptionalType = String.class;
        if (genericTypeParams.length > 0) {
            // Do not allow Optional<T>, Optional<T extends ...>, etc.
            targetOptionalType = throwIfTypeVariable(genericTypeParams[0]);
        }

        String propertyValue = context.value();
        if (propertyValue.isEmpty()) {
            return ConversionResult.of(Optional.empty());
        }

        Class<?> rawTargetOptionalType = TypeUtilities.getRawType(targetOptionalType);

        // If Optional<String> or Optional<Object>, return String value.
        if (String.class.equals(rawTargetOptionalType) || 
                Object.class.equals(rawTargetOptionalType)) {
            return ConversionResult.of(Optional.of(propertyValue));
        }

        return ConversionResult.of(
            convertToOptionalType(
                context,
                propertyValue,
                targetOptionalType
            )
        );
    }

    private Optional<?> convertToOptionalType(
            ConversionContext context,
            String value,
            Type optionalGenericTypeParameter
    ) {
        ConversionResult<?> converted = context.converter().convert(
            context.with(value, optionalGenericTypeParameter)
        );
        // Convert property and wrap in Optional.
        return Optional.ofNullable(converted.value());
    }

    private Type throwIfTypeVariable(Type optionalGenericTypeParameter) {
        if (TypeUtilities.isTypeVariable(optionalGenericTypeParameter)) {
            throw new ConversionException(
                "Type variables e.g. Optional<T> are not supported."
            );
        }

        return optionalGenericTypeParameter;
    }
}
