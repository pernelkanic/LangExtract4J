package com.langextract.Chunker;

import com.langextract.Models.ChunkData;

import java.util.List;

public interface TextChunker {
    List<ChunkData> chunk(String text);
}
