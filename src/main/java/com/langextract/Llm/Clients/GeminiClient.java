package com.langextract.Llm.Clients;
import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.json.JSONObject;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;


// Todo(venkat): For ollama models, in order to connect to cloud hosted model , We need a seperate custom headers map with bearer - apikey for auth.
public class GeminiClient implements LlmClient{
    private final ModelConfig modelconfig;

    public GeminiClient(ModelConfig modelconfig) {
        this.modelconfig = modelconfig;
    }
    @Override
    public void generate(String prompt, String examples) throws LlmClientException {
        try {
            GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder modelBuilder = GoogleAiGeminiChatModel.builder()
                    .apiKey(modelconfig.apiKey())
                    .modelName(modelconfig.modelId())
                    .responseFormat(ResponseFormat.JSON)
                    .timeout(Duration.ofMinutes(3));

            if(!LangUtils.isNullOrEmpty(modelconfig.temperature())){
                modelBuilder.temperature(modelconfig.temperature());
            }
            GoogleAiGeminiChatModel model = modelBuilder.build();
            if(LangUtils.isNullOrEmpty(model)){
                throw new LlmClientException("The Model pojo is empty please check the request builder");
            }
            CompletableFuture<String> response = CompletableFuture.supplyAsync(()->model.chat("Who is the author for harry potter give in the a json format eg - {'book' :'harry-potter' , 'author' : 'jk-rowling' }"));
            JSONObject json = new JSONObject(response.get());
            System.out.println("The json response is " + json);
        }
        catch (LlmClientException le){
            System.out.println("The model failed to  due to llm exception" + le);
        }
        catch (Exception e){
            System.out.println("The model failed to connect" + e);
        }
    }
}
