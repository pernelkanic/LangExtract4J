package com.langextract.Grounding;

import com.langextract.Models.ExtractionData;

public interface Aligner {
    ExtractionData align(String source, ExtractionData llmData, Integer chunkStartInx) throws Exception;
}
