package com.langextract.Llm;

public interface LlmClient {
    void generate(String prompt, String examples) throws Exception;
}
