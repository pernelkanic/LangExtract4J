package com.langextract.Models;

import java.util.Map;

public record ExtractionData(
  String extractionClass,
  String extractionText,
  Map<String, Object> attributes,
  String contextText,
  CharacterIndex chInx,
  MatchType matchType
) {}
