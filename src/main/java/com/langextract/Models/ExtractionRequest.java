package com.langextract.Models;

import java.util.List;
import java.util.Objects;

/** User-provided text, instructions, and optional examples for extraction. */
public record ExtractionRequest(
        String text,
        String promptDescription,
        List<ExampleData> examples
) {
    public ExtractionRequest {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(promptDescription, "promptDescription must not be null");
        examples = examples == null ? List.of() : List.copyOf(examples);
    }
}
