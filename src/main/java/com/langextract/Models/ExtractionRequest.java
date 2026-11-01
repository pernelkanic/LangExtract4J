package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.List;

public record ExtractionRequest(
  String text,
  String promptDescription,
  List<ExampleData> examples
) {
  public ExtractionRequest {
    if (LangUtils.isNullOrEmpty(text) || text.isBlank()) {
      throw new IllegalArgumentException("text must not be blank");
    }
    if (
      LangUtils.isNullOrEmpty(promptDescription) || promptDescription.isBlank()
    ) {
      throw new IllegalArgumentException("promptDescription must not be blank");
    }
    examples = LangUtils.isNullOrEmpty(examples)
      ? List.of()
      : List.copyOf(examples);
  }
}
