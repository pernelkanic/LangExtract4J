package com.langextract;

/** Raised when a document cannot be processed by the extraction pipeline. */
public class ExtractionException extends Exception {

  public ExtractionException(String message, Throwable cause) {
    super(message, cause);
  }
}
