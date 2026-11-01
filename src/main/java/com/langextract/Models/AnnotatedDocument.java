package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.List;

/** The source document together with its aligned extractions. */
public record AnnotatedDocument(String text, List<ExtractionData> extractions) {
  public AnnotatedDocument {
    extractions = LangUtils.isNullOrEmpty(extractions)
      ? List.of()
      : List.copyOf(extractions);
  }
}
