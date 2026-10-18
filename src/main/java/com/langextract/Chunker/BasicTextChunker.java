package com.langextract.Chunker;

import com.langextract.Utils.LangUtils;

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
}
