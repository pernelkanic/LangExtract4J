package com.langextract.models;

import java.util.Map;

public record ExtractionData() {
       static String extractionClass;
       static String extractionText;
       static Map<String,Object> attributes;
       //Text surrounding the fetched data to get which one was picked.
       static String contextText;
       static CharacterIndex chInx;
       static MatchType matchType;
}
