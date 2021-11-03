package io.github.jeyjeyemem.externalizedproperties.core.internal.utils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.jeyjeyemem.externalizedproperties.core.internal.utils.Arguments.requireNonNull;

/**
 * String-related utility methods.
 */
public class StringUtilities {
    /**
     * Default variable pattern for detecting variables in strings.
     * This matches the pattern: ${variable}
     */
    public static final Pattern DEFAULT_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private StringUtilities() {}

    /**
     * Replace variables in the String which matches the {@link #DEFAULT_VARIABLE_PATTERN}.
     * 
     * @param value The string whose variables are to be replaced.
     * @param variableValueProvider The variable values will be resolved via this provider function.
     * @return The processed String whose variables are replaced, if there were any that matched the pattern.
     */
    public static String replaceVariables(String value, Function<String, String> variableValueProvider) {
        return replaceVariables(value, DEFAULT_VARIABLE_PATTERN, variableValueProvider);
    }

    /**
     * Replace variables in the String which matches the provided pattern.
     * 
     * @param value The string whose variables are to be replaced.
     * @param variablePattern The pattern to match variables in the string.
     * @param variableValueProvider The variable values will be resolved via this provider function.
     * @return The processed String whose variables are replaced, if there were any that matched the pattern.
     */
    public static String replaceVariables(
            String value, 
            Pattern variablePattern,
            Function<String, String> variableValueProvider
    ) {
        requireNonNull(value, "value");
        requireNonNull(variablePattern, "variablePattern");
        requireNonNull(variableValueProvider, "variableValueProvider");

        StringBuilder output = new StringBuilder();

        Matcher matcher = variablePattern.matcher(value);
        int currentIndex = 0;
        while (matcher.find()) {
            // Get text before matched variable.
            String textBeforeMatchedVariable = 
                value.substring(currentIndex, matcher.start());
            
            // Resolve property from variable.
            String propertyNameVariable = matcher.group(1);
            
            if (isNullOrEmpty(propertyNameVariable)) {
                // e.g. for default variable pattern, an empty '${}' was matched.
                throw new IllegalStateException(
                    "Variable pattern matched with an null/empty capturing group value. " + 
                    "Variable pattern used in matching: " + variablePattern.pattern()
                );
            }

            String propertyValue = variableValueProvider.apply(propertyNameVariable);
            if (propertyValue == null) {
                throw new IllegalStateException(
                    "Unable to find value for variable: " + propertyNameVariable
                );
            }
            
            // Append text before matched variable and the replacement value.
            output.append(textBeforeMatchedVariable).append(propertyValue);
            
            // Move on to find next variable starting from the new currentIndex.
            currentIndex = matcher.end();
        }

        // Append any text after the variable if there are any.
        return output.append(value.substring(currentIndex)).toString();
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
