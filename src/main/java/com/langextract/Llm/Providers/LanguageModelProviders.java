package com.langextract.Llm.Providers;

import com.langextract.Llm.Clients.LlmClient;
import com.langextract.Models.ModelConfig;

public interface LanguageModelProviders {
    boolean exists(String modelId) throws Exception;

    LlmClient createClient(ModelConfig modelconfig);
}
