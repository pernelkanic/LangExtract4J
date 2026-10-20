package com.langextract.Models;

import java.util.List;
import java.util.Objects;

/** A sample input and the extractions the LLM should learn to produce from it. */
public record ExampleData(String text, List<ExampleExtraction> extractions) {
    public ExampleData {
        Objects.requireNonNull(text, "text must not be null");
        extractions = extractions == null ? List.of() : List.copyOf(extractions);
    }
}
