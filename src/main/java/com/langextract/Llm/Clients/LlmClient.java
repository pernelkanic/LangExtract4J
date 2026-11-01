package com.langextract.Llm.Clients;

import org.json.JSONObject;

public interface LlmClient {
  JSONObject generate(String prompt) throws Exception;
}
