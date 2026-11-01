package com.langextract.Models;

public record ModelConfig(
  String modelId,
  String baseUrl,
  String apiKey,
  Double temperature
) {}
