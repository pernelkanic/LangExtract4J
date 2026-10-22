package com.langextract.Llm;

import com.langextract.Llm.Clients.LlmClient;
import com.langextract.Llm.Providers.LanguageModelProviders;
import com.langextract.Llm.Providers.ProviderRegistry;
import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;

public class LanguageModelFactory {
    private static final ProviderRegistry provider = ProviderRegistry.getProviderRegistryInstance();

    public static LlmClient create(ModelConfig modelConfig) throws Exception {
        LanguageModelProviders modelProvider = provider.resolve(modelConfig.modelId());
        return modelProvider.createClient(modelConfig);
    }
}
