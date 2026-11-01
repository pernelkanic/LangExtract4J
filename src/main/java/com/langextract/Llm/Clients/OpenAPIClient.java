package com.langextract.Llm.Clients;

import com.langextract.Models.ModelConfig;
import com.langextract.Utils.LangUtils;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import org.json.JSONObject;

public class OpenAPIClient implements LlmClient {

  private final ModelConfig modelconfig;

  public OpenAPIClient(ModelConfig modelconfig) {
    this.modelconfig = modelconfig;
  }

  @Override
  public JSONObject generate(String prompt) throws LlmClientException {
    if (LangUtils.isNullOrEmpty(prompt)) {
      throw new LlmClientException("Prompt must not be empty");
    }
    try {
      OpenAiChatModel.OpenAiChatModelBuilder modelBuilder =
        OpenAiChatModel.builder()
          .baseUrl(modelconfig.baseUrl())
          .apiKey(modelconfig.apiKey())
          .modelName(modelconfig.modelId())
          .responseFormat(ResponseFormat.JSON)
          .timeout(Duration.ofMinutes(3));

      if (!LangUtils.isNullOrEmpty(modelconfig.temperature())) {
        modelBuilder.temperature(modelconfig.temperature());
      }

      OpenAiChatModel model = modelBuilder.build();
      if (LangUtils.isNullOrEmpty(model)) {
        throw new LlmClientException(
          "Unable to build OpenAI compatible chat model"
        );
      }
      String response = model.chat(prompt);
      return new JSONObject(response);
    } catch (LlmClientException e) {
      throw e;
    } catch (Exception e) {
      throw new LlmClientException("OpenAI compatible API request failed", e);
    }
  }
}
