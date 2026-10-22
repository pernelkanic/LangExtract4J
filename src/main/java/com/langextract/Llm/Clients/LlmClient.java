package com.langextract.Llm.Clients;

public interface LlmClient {
    void generate(String prompt, String examples) throws Exception;
}
