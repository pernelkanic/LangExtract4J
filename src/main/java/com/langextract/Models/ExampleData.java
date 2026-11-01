package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.List;

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
