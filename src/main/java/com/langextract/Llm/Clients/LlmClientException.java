package com.langextract.Llm.Clients;

public class LlmClientException extends Exception {
    public LlmClientException(String message) {
        super(message);
    }

    public LlmClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
