package com.langextract.Models;

import com.langextract.Utils.LangUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ExampleExtraction(
  String extractionClass,
  String extractionText,
  Map<String, Object> attributes
) {
  public ExampleExtraction {
    attributes = LangUtils.isNullOrEmpty(attributes)
      ? Map.of()
      : Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
  }
}
