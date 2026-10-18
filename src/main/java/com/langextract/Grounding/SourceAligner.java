package com.langextract.Grounding;

import com.langextract.Models.CharacterIndex;
import com.langextract.Models.ExtractionData;
import com.langextract.Models.MatchType;
import com.langextract.Utils.LangUtils;

public class SourceAligner implements Aligner{

    @Override
    public ExtractionData align(String source, ExtractionData llmData, Integer chunkStartInx) throws Exception {
        if (LangUtils.isNullOrEmpty(source) || llmData == null  || chunkStartInx == null || LangUtils.isNullOrEmpty(llmData.contextText()) || LangUtils.isNullOrEmpty(llmData.extractionText())) {
            return null;
        }
        String context = llmData.contextText();
        String extraction = llmData.extractionText();
        int contextStartIndex = source.indexOf(context);
        if (contextStartIndex == -1) {
            return buildUnmatchedResponse(llmData);
        }
        int localExtractionStartInx = context.indexOf(extraction);
        if (localExtractionStartInx != -1) {
            int localStartIndex = contextStartIndex + localExtractionStartInx;
            int localEndIndex = localStartIndex + extraction.length();
            String matchedSourceText = source.substring(localStartIndex, localEndIndex);
            MatchType matchType = fetchMatchType(extraction, matchedSourceText);
            return buildResponse(llmData, chunkStartInx + localStartIndex, chunkStartInx + localEndIndex, matchType);
        }
        int extractionStartInxSource = source.indexOf(extraction);
        if (extractionStartInxSource != -1) {
            int extractionEndInSource = extractionStartInxSource + extraction.length();
            return buildResponse(llmData, chunkStartInx + extractionStartInxSource, chunkStartInx + extractionEndInSource, MatchType.MATCH_EXACT);
        }
        return buildUnmatchedResponse(llmData);
    }

    private MatchType fetchMatchType(String extractionText, String matchedSourceText) {
        if (extractionText == null || matchedSourceText == null) {
            return MatchType.UNMATCHED;
        }
        if (extractionText.equals(matchedSourceText)) {
            return MatchType.MATCH_EXACT;
        }
        if (matchedSourceText.contains(extractionText)) {
            return MatchType.MATCH_GREATER;
        }
        if (extractionText.contains(matchedSourceText)) {
            return MatchType.MATCH_LESSER;
        }
        return MatchType.UNMATCHED;
    }

    private ExtractionData buildResponse(ExtractionData llmData, int startIndex, int endIndex, MatchType matchType) {
        CharacterIndex chInx = new CharacterIndex(startIndex, endIndex);
        return new ExtractionData(llmData.extractionClass(), llmData.extractionText(), llmData.attributes(), llmData.contextText(), chInx, matchType);
    }

    private ExtractionData buildUnmatchedResponse(ExtractionData llmData) {
        return new ExtractionData(llmData.extractionClass(), llmData.extractionText(), llmData.attributes(), llmData.contextText(), new CharacterIndex(null, null), MatchType.UNMATCHED);
    }
}
