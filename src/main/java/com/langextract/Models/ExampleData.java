package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.List;

/** A sample input and the extractions the LLM should learn to produce from it. */
public record ExampleData(String text, List<ExampleExtraction> extractions) {
  public ExampleData {
    if (LangUtils.isNullOrEmpty(text)) {
      throw new IllegalArgumentException("text must not be empty");
    }
    extractions = LangUtils.isNullOrEmpty(extractions)
      ? List.of()
      : List.copyOf(extractions);
  }
}
