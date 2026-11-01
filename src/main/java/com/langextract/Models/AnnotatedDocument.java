package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.List;

public record AnnotatedDocument(String text, List<ExtractionData> extractions) {
  public AnnotatedDocument {
    extractions = LangUtils.isNullOrEmpty(extractions)
      ? List.of()
      : List.copyOf(extractions);
  }
}
