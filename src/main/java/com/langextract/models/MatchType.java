package com.langextract.models;

public enum MatchType{
    MATCH_EXACT(1),
    MATCH_FUZZY(2),
    MATCH_LESSER(3),
    MATCH_GREATER(4),
    UNMATCHED(5);

    MatchType(int i) {
    }
}
