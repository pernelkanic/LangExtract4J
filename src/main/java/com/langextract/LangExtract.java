package com.langextract;

import com.langextract.Chunker.BasicTextChunker;
import com.langextract.Chunker.TextChunker;
import com.langextract.Extracter.JacksonExtractor;
import com.langextract.Grounding.SourceAligner;
import com.langextract.Llm.Clients.LlmClient;
import com.langextract.Models.AnnotatedDocument;
import com.langextract.Models.CharacterIndex;
import com.langextract.Models.ChunkData;
import com.langextract.Models.ExtractionData;
import com.langextract.Models.ExtractionRequest;
import com.langextract.Prompt.PromptBuilder;
import com.langextract.Utils.LangUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class LangExtract implements AutoCloseable {

  private final TextChunker textChunker;
  private final PromptBuilder promptBuilder;
  private final LlmClient llmClient;
  private final JacksonExtractor jacksonExtractor;
  private final SourceAligner sourceAligner;
  private final ExecutorService executor;

  public LangExtract(LlmClient llmClient, int maxConcurrentRequests)
    throws Exception {
    this(
      new BasicTextChunker(32, 10),
      new PromptBuilder(),
      llmClient,
      new JacksonExtractor(),
      new SourceAligner(),
      maxConcurrentRequests
    );
  }

  LangExtract(
    TextChunker textChunker,
    PromptBuilder promptBuilder,
    LlmClient llmClient,
    JacksonExtractor jacksonExtractor,
    SourceAligner sourceAligner,
    int maxConcurrentRequests
  ) {
    this.textChunker = requireDependency(textChunker, "textChunker");
    this.promptBuilder = requireDependency(promptBuilder, "promptBuilder");
    this.llmClient = requireDependency(llmClient, "llmClient");
    this.jacksonExtractor = requireDependency(
      jacksonExtractor,
      "jacksonExtractor"
    );
    this.sourceAligner = requireDependency(sourceAligner, "sourceAligner");
    if (maxConcurrentRequests <= 0) {
      throw new IllegalArgumentException(
        "maxConcurrentRequests must be greater than zero"
      );
    }
    this.executor = Executors.newFixedThreadPool(maxConcurrentRequests);
  }

  public AnnotatedDocument extract(ExtractionRequest request)
    throws ExtractionException {
    if (LangUtils.isNullOrEmpty(request)) {
      throw new IllegalArgumentException("request must not be null");
    }
    String documentText = request.text();
    List<ChunkData> chunks = textChunker.chunk(documentText);
    if (LangUtils.isNullOrEmpty(chunks)) {
      throw new IllegalStateException(
        "The chunker returned no chunks for a non-empty document"
      );
    }

    List<CompletableFuture<List<ExtractionData>>> futures = new ArrayList<>(
      chunks.size()
    );
    for (ChunkData chunk : chunks) {
      futures.add(
        CompletableFuture.supplyAsync(() -> {
          try {
            return processChunk(request, chunk);
          } catch (Exception e) {
            throw new CompletionException(e);
          }
        }, executor)
      );
    }

    List<ExtractionData> allExtractions = new ArrayList<>();
    try {
      for (CompletableFuture<List<ExtractionData>> future : futures) {
        allExtractions.addAll(future.join());
      }
    } catch (CompletionException e) {
      futures.forEach(future -> future.cancel(true));
      Throwable cause = LangUtils.isNullOrEmpty(e.getCause())
        ? e
        : e.getCause();
      throw new ExtractionException(
        "Failed to extract information from the document",
        cause
      );
    }
    return new AnnotatedDocument(documentText, deduplicate(allExtractions));
  }

  private List<ExtractionData> processChunk(
    ExtractionRequest request,
    ChunkData chunk
  ) throws Exception {
    String prompt = promptBuilder.buildPrompt(request, chunk.chunkText());
    JSONObject response = llmClient.generate(prompt);
    List<ExtractionData> extractions = jacksonExtractor.parse(
      response.toString()
    );
    return sourceAligner.alignAll(
      chunk.chunkText(),
      extractions,
      chunk.startIndex()
    );
  }

  private List<ExtractionData> deduplicate(List<ExtractionData> extractions) {
    Map<ExtractionKey, ExtractionData> uniqueBySpan = new LinkedHashMap<>();
    List<ExtractionData> results = new ArrayList<>(extractions.size());

    for (ExtractionData extraction : extractions) {
      CharacterIndex index = extraction.chInx();
      if (
        LangUtils.isNullOrEmpty(index) ||
        LangUtils.isNullOrEmpty(index.startIndex()) ||
        LangUtils.isNullOrEmpty(index.endIndex())
      ) {
        results.add(extraction);
        continue;
      }
      String normalizedClass = extraction
        .extractionClass()
        .trim()
        .toLowerCase()
        .replaceAll("\\s+", "_");

      ExtractionKey key = new ExtractionKey(
        index.startIndex(),
        index.endIndex(),
        normalizedClass
      );

      if (!uniqueBySpan.containsKey(key)) {
        uniqueBySpan.put(key, extraction);
        results.add(extraction);
      }
    }

    return results;
  }

  private record ExtractionKey(
    int startIndex,
    int endIndex,
    String extractionClass
  ) {}

  private static <T> T requireDependency(T dependency, String name) {
    if (LangUtils.isNullOrEmpty(dependency)) {
      throw new IllegalArgumentException(name + " must not be null");
    }
    return dependency;
  }

  @Override
  public void close() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
