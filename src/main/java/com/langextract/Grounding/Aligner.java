package com.langextract.Grounding;

import com.langextract.Models.ExtractionData;

import java.util.ArrayList;
import java.util.List;

public interface Aligner {
    ExtractionData align(String source, ExtractionData llmData, Integer chunkStartInx) throws Exception;

    /**
     * Aligns a group together so repeated mentions can be placed in extraction order.
     * Implementations that only support single items keep working through this default.
     */
    default List<ExtractionData> alignAll(String source, List<ExtractionData> extractions,
                                          Integer chunkStartInx) throws Exception {
        if (extractions == null) {
            throw new IllegalArgumentException("extractions must not be null");
        }
        List<ExtractionData> aligned = new ArrayList<>(extractions.size());
        for (ExtractionData extraction : extractions) {
            aligned.add(align(source, extraction, chunkStartInx));
        }
        return aligned;
    }
}
