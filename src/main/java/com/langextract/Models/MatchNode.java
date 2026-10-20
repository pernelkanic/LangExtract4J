package com.langextract.Models;

/** A possible source match and the earlier match it can follow. */
public record MatchNode(
        int extractionIndex,
        int startToken,
        int endToken,
        int score,
        MatchNode previous
) {}
