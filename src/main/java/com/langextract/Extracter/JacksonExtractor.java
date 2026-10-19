package com.langextract.Extracter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langextract.Models.ExtractionData;
import com.langextract.Utils.LangUtils;

import java.util.List;

public class JacksonExtractor {
    private static final String EXTRACTION_KEY = "extractions";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    //(TODO:venkat) add some custom exceptions and Loggers here.
    public List<ExtractionData> parse(String json) throws Exception{
        if(LangUtils.isNullOrEmpty(json)){
            throw new Exception("The response from the LLM is empty");
        }
        try {
            JsonNode responseNode = objectMapper.readTree(json);
            JsonNode extraction = responseNode.get(EXTRACTION_KEY);
            if (LangUtils.isNullOrEmpty(extraction) || !LangUtils.isArray(extraction)) {
                throw new Exception("The extraction data is not array");
            }
            return objectMapper.readerForListOf(ExtractionData.class).readValue(extraction);
        }
        catch(Exception e){
            throw new Exception("Failed to parse the LLM JSON response", e);
        }
    }
}
