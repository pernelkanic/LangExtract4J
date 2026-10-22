package com.langextract.Llm.Providers;

import com.langextract.Llm.Clients.LlmClient;
import com.langextract.Llm.Clients.OpenAPIClient;
import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;

public class OpenAPIProvider implements LanguageModelProviders {
    private static final String REGEX_MATCHER = "^openapi-.*$";
    @Override
    public boolean exists(String modelId) throws Exception {
        if(LangUtils.isNullOrEmpty(modelId)){
            throw new Exception("The model passed is empty");
        }
        return modelId.matches(REGEX_MATCHER);
    }

    @Override
    public LlmClient createClient(ModelConfig modelconfig) {
        return new OpenAPIClient(modelconfig);
    }
}
