package com.langextract.Models;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** One expected result inside an {@link ExampleData} prompt example. */
public record ExampleExtraction(
        String extractionClass,
        String extractionText,
        Map<String, Object> attributes
) {
    public ExampleExtraction {
        attributes = attributes == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }
}
