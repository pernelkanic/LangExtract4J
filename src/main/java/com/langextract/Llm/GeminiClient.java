package com.langextract.Llm;
import com.langextract.Utils.LangUtils;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.json.JSONObject;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;


// Todo(venkat): For ollama models, in order to connect to cloud hosted model , We need a seperate custom headers map with bearer - apikey for auth.
public class GeminiClient implements LlmClient{
    //api key from the google console.
    private static final String API_KEY = "";

    @Override
    public void generate(String prompt, String examples) throws LlmClientException {
        try {
            GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(API_KEY)
                    .modelName("gemini-3.8-flash")
                    .temperature(0.0)
                    .responseFormat(ResponseFormat.JSON)
                    .timeout(Duration.ofMinutes(3))
                    .build();
            if(LangUtils.isNullOrEmpty(model)){
                throw new LlmClientException("The model pojo is empty please check the request builder");
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
