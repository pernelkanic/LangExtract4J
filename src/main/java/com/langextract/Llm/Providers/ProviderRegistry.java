package com.langextract.Llm.Providers;

import java.util.ServiceLoader;

public class ProviderRegistry {
    private static final ServiceLoader<LanguageModelProviders> serviceLoader = ServiceLoader.load(LanguageModelProviders.class);
    private static final ProviderRegistry INSTANCE = new ProviderRegistry();

    private ProviderRegistry(){}

    public static ProviderRegistry getProviderRegistryInstance(){
        return INSTANCE;
    }

    public LanguageModelProviders resolve(String modelId) throws Exception {
        for(LanguageModelProviders provider : serviceLoader){
            if(provider.exists(modelId)){
                return provider;
            }
        }
        throw new Exception("The model is not found for the model ID");
    }



}
