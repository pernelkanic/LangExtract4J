package com.langextract.Llm.Clients;

import com.langextract.Models.ModelConfig;

public class OllamaClient implements LlmClient {
    private final ModelConfig modelconfig;

    public OllamaClient(ModelConfig modelconfig) {
        this.modelconfig = modelconfig;
    }

    @Override
    public void generate(String prompt, String examples) {
        throw new UnsupportedOperationException("Ollama client is not implemented yet");
    }
}
