package com.langextract.Grounding;

import com.langextract.Models.CharacterIndex;
import com.langextract.Models.ExtractionData;
import com.langextract.Models.MatchNode;
import com.langextract.Models.MatchType;
import com.langextract.Models.TextToken;
import com.langextract.Utils.LangUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceAligner implements Aligner {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{L}\\p{N}_]+|[^\\s]");

    @Override
    public ExtractionData align(String source, ExtractionData llmData, Integer chunkStartInx) {
        return alignAll(source, Collections.singletonList(llmData), chunkStartInx).get(0);
    }

    @Override
    public List<ExtractionData> alignAll(String source, List<ExtractionData> extractions, Integer chunkStartInx) {
        validateInputs(source, extractions, chunkStartInx);
        if (LangUtils.isNullOrEmpty(extractions)) {
            return List.of();
        }

        List<TextToken> sourceTokens = tokenize(source);
        List<MatchNode> allMatches = new ArrayList<>();
        for (int extractionIndex = 0; extractionIndex < extractions.size(); extractionIndex++) {
            ExtractionData extraction = extractions.get(extractionIndex);
            List<TextToken> extractionTokens = tokenize(extraction.extractionText());
            if (LangUtils.isNullOrEmpty(extractionTokens) || extractionTokens.size() > sourceTokens.size()) {
                continue;
            }

            for (int start = 0; start <= sourceTokens.size() - extractionTokens.size(); start++) {
                if (!matchesAt(sourceTokens, extractionTokens, start)) {
                    continue;
                }

                int score = extractionTokens.size();
                MatchNode previous = null;
                for (MatchNode previousMatch : allMatches) {
                    if (previousMatch.extractionIndex() < extractionIndex
                            && previousMatch.endToken() <= start
                            && previousMatch.score() + extractionTokens.size() > score) {
                        score = previousMatch.score() + extractionTokens.size();
                        previous = previousMatch;
                    }
                }

                MatchNode current = new MatchNode(extractionIndex, start,
                        start + extractionTokens.size(), score, previous);
                allMatches.add(current);
            }
        }

        MatchNode best = null;
        for (MatchNode match : allMatches) {
            if (LangUtils.isNullOrEmpty(best) || match.score() > best.score()) {
                best = match;
            }
        }

        ExtractionData[] aligned = new ExtractionData[extractions.size()];
        while (!LangUtils.isNullOrEmpty(best)) {
            ExtractionData original = extractions.get(best.extractionIndex());
            TextToken firstToken = sourceTokens.get(best.startToken());
            TextToken lastToken = sourceTokens.get(best.endToken() - 1);
            int startIndex = Math.addExact(chunkStartInx, firstToken.startIndex());
            int endIndex = Math.addExact(chunkStartInx, lastToken.endIndex());
            aligned[best.extractionIndex()] = buildResponse(original, startIndex, endIndex, MatchType.MATCH_EXACT);
            best = best.previous();
        }

        List<ExtractionData> results = new ArrayList<>(extractions.size());
        for (int i = 0; i < extractions.size(); i++) {
            results.add(LangUtils.isNullOrEmpty(aligned[i]) ? buildUnmatchedResponse(extractions.get(i)) : aligned[i]);
        }
        return results;
    }

    private void validateInputs(String source, List<ExtractionData> extractions, Integer chunkStartInx) {
        if (LangUtils.isNullOrEmpty(source)) {
            throw new IllegalArgumentException("source must not be empty");
        }
        if (LangUtils.isNullOrEmpty(chunkStartInx)) {
            throw new IllegalArgumentException("chunkStartInx must not be null");
        }
        if (chunkStartInx < 0) {
            throw new IllegalArgumentException("chunkStartInx must be zero or greater");
        }
        if (extractions == null) {
            throw new IllegalArgumentException("extractions must not be null");
        }
        for (ExtractionData extraction : extractions) {
            if (LangUtils.isNullOrEmpty(extraction)) {
                throw new IllegalArgumentException("extractions must not contain null values");
            }
            if (LangUtils.isNullOrEmpty(extraction.extractionText()) || extraction.extractionText().isBlank()) {
                throw new IllegalArgumentException("extractionText must not be empty");
            }
        }
    }

    private List<TextToken> tokenize(String text) {
        List<TextToken> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(text);
        while (matcher.find()) {
            tokens.add(new TextToken(matcher.group().toLowerCase(Locale.ROOT), matcher.start(), matcher.end()));
        }
        return tokens;
    }

    private boolean matchesAt(List<TextToken> sourceTokens, List<TextToken> extractionTokens, int start) {
        for (int i = 0; i < extractionTokens.size(); i++) {
            if (!sourceTokens.get(start + i).text().equals(extractionTokens.get(i).text())) {
                return false;
            }
        }
        return true;
    }

    private ExtractionData buildUnmatchedResponse(ExtractionData llmData) {
        return buildResponse(llmData, null, null, MatchType.UNMATCHED);
    }

    private ExtractionData buildResponse(ExtractionData llmData, Integer startIndex, Integer endIndex,
                                         MatchType matchType) {
        CharacterIndex charIndex = new CharacterIndex(startIndex, endIndex);
        return new ExtractionData(llmData.extractionClass(), llmData.extractionText(), llmData.attributes(),
                llmData.contextText(), charIndex, matchType);
    }

}
