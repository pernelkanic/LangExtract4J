package com.langextract.Models;

public record ChunkData(
        String chunkText,
        int startIndex,
        int endIndex
) {}
