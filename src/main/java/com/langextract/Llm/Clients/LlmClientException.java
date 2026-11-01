package com.langextract.Llm.Clients;

public class LlmClientException extends Exception {

  LlmClientException(String message) {
    super(message);
  }

  LlmClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
