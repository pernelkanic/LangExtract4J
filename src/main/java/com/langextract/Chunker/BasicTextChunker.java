package com.langextract.Chunker;

import com.langextract.Models.ChunkData;
import com.langextract.Utils.LangUtils;

import java.util.ArrayList;
import java.util.List;

public class BasicTextChunker implements TextChunker{
    private final int chunkSize;
    private final int overLap;
    BasicTextChunker(int chunkSize, int overLap) throws Exception {
        if(LangUtils.isNullOrEmpty(chunkSize) || LangUtils.isNullOrEmpty(overLap)){
           throw new Exception("The chunkSize or overLap is empty or null");
        }
        if(chunkSize<= 0){
            throw new Exception("The chunkSize must be greater than zero");
        }
        if(overLap < 0 || overLap >= chunkSize){
            throw new Exception("The overlap is empty or greater than chunk size");
        }
        this.chunkSize = chunkSize;
        this.overLap = overLap;
    }
    @Override
    public List<ChunkData> chunk(String data){
        if(LangUtils.isNullOrEmpty(data)){
            return List.of();
        }
        List<ChunkData> chunks = new ArrayList<>();
        int startIndex = 0;
        while(startIndex < data.length()){
            int endIndex = Math.min(data.length(), startIndex + chunkSize);
            String chunkText = data.substring(startIndex, endIndex);
            chunks.add(new ChunkData(chunkText, startIndex, endIndex));
            if(endIndex == data.length()){
                break;
            }
            startIndex = endIndex - overLap;
        }
        return chunks;
    }

}
