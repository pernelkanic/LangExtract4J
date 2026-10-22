package com.langextract.Llm.Clients;

import com.langextract.Models.ModelConfig;

public class OpenAPIClient implements LlmClient {
    private final ModelConfig modelconfig;

    public OpenAPIClient(ModelConfig modelconfig) {
        this.modelconfig = modelconfig;
    }

    @Override
    public void generate(String prompt, String examples) {
        throw new UnsupportedOperationException("OpenAPI client is not implemented yet");
    }
}
