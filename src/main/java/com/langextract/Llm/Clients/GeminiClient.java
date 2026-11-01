package com.langextract.Llm.Clients;

import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import java.time.Duration;
import org.json.JSONObject;

// Todo(venkat): For ollama models, in order to connect to cloud hosted model , We need a seperate custom headers map with bearer - apikey for auth.
public class GeminiClient implements LlmClient {

  private final ModelConfig modelconfig;

  public GeminiClient(ModelConfig modelconfig) {
    this.modelconfig = modelconfig;
  }

  @Override
  public JSONObject generate(String prompt) throws LlmClientException {
    if (LangUtils.isNullOrEmpty(prompt)) {
      throw new LlmClientException("Prompt must not be empty");
    }
    try {
      GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder modelBuilder =
        GoogleAiGeminiChatModel.builder()
          .apiKey(modelconfig.apiKey())
          .modelName(modelconfig.modelId())
          .responseFormat(ResponseFormat.JSON)
          .timeout(Duration.ofMinutes(3));

      if (!LangUtils.isNullOrEmpty(modelconfig.temperature())) {
        modelBuilder.temperature(modelconfig.temperature());
      }
      GoogleAiGeminiChatModel model = modelBuilder.build();
      if (LangUtils.isNullOrEmpty(model)) {
        throw new LlmClientException(
          "The Model pojo is empty please check the request builder"
        );
      }
      String response = model.chat(prompt);
      return new JSONObject(response);
    } catch (LlmClientException e) {
      throw e;
    } catch (Exception e) {
      throw new LlmClientException("Gemini request failed", e);
    }
  }
}
