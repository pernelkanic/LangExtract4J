package com.langextract.Llm.Clients;

import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import java.util.Map;
import org.json.JSONObject;

public class OllamaClient implements LlmClient {

  private final ModelConfig modelconfig;

  public OllamaClient(ModelConfig modelconfig) {
    this.modelconfig = modelconfig;
  }

  @Override
  public JSONObject generate(String prompt) throws LlmClientException {
    if (LangUtils.isNullOrEmpty(prompt)) {
      throw new LlmClientException("Prompt must not be empty");
    }
    try {
      OllamaChatModel.OllamaChatModelBuilder modelBuilder =
        OllamaChatModel.builder()
          .baseUrl("https://ollama.com")
          .modelName("gemma4:31b")
          .responseFormat(ResponseFormat.JSON)
          .timeout(Duration.ofMinutes(3))
          .customHeaders(
            Map.of("Authorization", "Bearer " + modelconfig.apiKey())
          );

      if (!LangUtils.isNullOrEmpty(modelconfig.temperature())) {
        modelBuilder.temperature(modelconfig.temperature());
      }

      OllamaChatModel model = modelBuilder.build();
      if (LangUtils.isNullOrEmpty(model)) {
        throw new LlmClientException("Unable to build Ollama chat model");
      }
      String response = model.chat(prompt);
      return new JSONObject(response);
    } catch (LlmClientException e) {
      throw e;
    } catch (Exception e) {
      throw new LlmClientException("Ollama request failed", e);
    }
  }
}
