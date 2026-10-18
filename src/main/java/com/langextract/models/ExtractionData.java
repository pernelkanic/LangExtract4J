package com.langextract.models;

import java.util.Map;

public record ExtractionData(
        String extractionClass,
        String extractionText,
        Map<String,Object> attributes,
        //Text surrounding the fetched data to get which one was picked.
        String contextText,
        CharacterIndex chInx,
        MatchType matchType
) {}

