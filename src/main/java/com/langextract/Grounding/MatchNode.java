package com.langextract.Grounding;

/** A source match and the earlier match in its chain. */
record MatchNode(
  int extractionIndex,
  int startToken,
  int endToken,
  int score,
  MatchNode previous
) {}
