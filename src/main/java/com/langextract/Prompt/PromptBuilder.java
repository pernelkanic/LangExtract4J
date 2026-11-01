package com.langextract.Prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.langextract.Models.ExampleData;
import com.langextract.Models.ExtractionRequest;
import com.langextract.Utils.LangUtils;

/** Builds the prompt sent to a language model for an extraction request. */
public class PromptBuilder {

  private static final String RESPONSE_FORMAT = """
  Extract information from the following text.

  Return only valid JSON.
  Do not include Markdown code fences or explanations.
  The extractionText must be an exact substring of the input text.
  Extract complete names and words; do not return a partial word or a prefix of an entity name.
  Preserve the original spelling, capitalization, and punctuation. Do not paraphrase extracted text.

  The response must have this structure:
  {
    "extractions": [
      {
        "extractionClass": "example_category",
        "extractionText": "exact text from the input",
        "attributes": {
          "attribute_name": "attribute_value"
        }
      }
    ]
  }

  The extractionClass, extractionText, and attributes must reflect the requested extraction task.
  Attributes are optional and can have any keys and values relevant to the extraction instructions.
  Do not use the example attribute names unless they are relevant to the current task.

  If nothing relevant is found, return:
  {"extractions": []}

  """;

  private final ObjectMapper objectMapper;

  public PromptBuilder() {
    this(new ObjectMapper());
  }

  public PromptBuilder(ObjectMapper objectMapper) {
    if (LangUtils.isNullOrEmpty(objectMapper)) {
      throw new IllegalArgumentException("objectMapper must not be null");
    }
    this.objectMapper = objectMapper;
  }

  public String buildPrompt(ExtractionRequest request) {
    requireRequest(request);
    return buildPrompt(request, request.text());
  }

  public String buildPrompt(ExtractionRequest request, String chunkText) {
    requireRequest(request);
    if (LangUtils.isNullOrEmpty(chunkText) || chunkText.isBlank()) {
      throw new IllegalArgumentException("Text chunk must not be empty");
    }

    StringBuilder prompt = new StringBuilder();
    prompt
      .append("You are an information extraction assistant.\n\n")
      .append(
        "Follow the instructions carefully. Extract only information supported by the input text. "
      )
      .append("Do not invent information.\n\n")
      .append("Instructions:\n")
      .append(request.promptDescription())
      .append("\n\n");

    if (!LangUtils.isNullOrEmpty(request.examples())) {
      prompt.append("Here are examples of the expected extractions:\n\n");
      for (ExampleData example : request.examples()) {
        prompt
          .append("Input:\n")
          .append(example.text())
          .append("\n\n")
          .append("Expected output:\n")
          .append(formatExtractions(example))
          .append("\n\n");
      }
    }

    prompt
      .append(RESPONSE_FORMAT)
      .append("Input text:\n")
      .append(chunkText)
      .append("\n\nJSON response:\n");
    return prompt.toString();
  }

  private String formatExtractions(ExampleData example) {
    try {
      ObjectNode response = objectMapper.createObjectNode();
      response.set(
        "extractions",
        objectMapper.valueToTree(example.extractions())
      );
      return objectMapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
        "Failed to serialize extraction examples",
        e
      );
    }
  }

  private void requireRequest(ExtractionRequest request) {
    if (LangUtils.isNullOrEmpty(request)) {
      throw new IllegalArgumentException("ExtractionRequest must not be null");
    }
  }
}
