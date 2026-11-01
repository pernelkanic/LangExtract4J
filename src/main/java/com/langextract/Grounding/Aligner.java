package com.langextract.Grounding;

import com.langextract.Models.ExtractionData;
import java.util.ArrayList;
import java.util.List;

interface Aligner {
  ExtractionData align(
    String source,
    ExtractionData llmData,
    Integer chunkStartInx
  ) throws Exception;

  /** Aligns a group of extractions in source order. */
  default List<ExtractionData> alignAll(
    String source,
    List<ExtractionData> extractions,
    Integer chunkStartInx
  ) throws Exception {
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
