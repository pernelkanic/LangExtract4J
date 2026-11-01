package com.langextract.Chunker;

import com.langextract.Models.ChunkData;
import com.langextract.Utils.LangUtils;
import java.util.ArrayList;
import java.util.List;

public class BasicTextChunker implements TextChunker {

  private final int chunkSize;
  private final int overLap;

  public BasicTextChunker(int chunkSize, int overLap) throws Exception {
    if (
      LangUtils.isNullOrEmpty(chunkSize) || LangUtils.isNullOrEmpty(overLap)
    ) {
      throw new Exception("The chunkSize or overLap is empty or null");
    }
    if (chunkSize <= 0) {
      throw new Exception("The chunkSize must be greater than zero");
    }
    if (overLap < 0 || overLap >= chunkSize) {
      throw new Exception("The overlap is empty or greater than chunk size");
    }
    this.chunkSize = chunkSize;
    this.overLap = overLap;
  }

  @Override
  public List<ChunkData> chunk(String data) {
    if (LangUtils.isNullOrEmpty(data)) {
      return List.of();
    }
    List<ChunkData> chunks = new ArrayList<>();
    int startIndex = 0;
    while (startIndex < data.length()) {
      int targetEndIndex = Math.min(data.length(), startIndex + chunkSize);
      int endIndex = adjustEndToWordBoundary(data, startIndex, targetEndIndex);
      String chunkText = data.substring(startIndex, endIndex);
      chunks.add(new ChunkData(chunkText, startIndex, endIndex));
      if (endIndex == data.length()) {
        break;
      }

      int nextStartIndex = Math.max(startIndex + 1, endIndex - overLap);
      while (
        nextStartIndex > 0 &&
        nextStartIndex < data.length() &&
        !Character.isWhitespace(data.charAt(nextStartIndex - 1)) &&
        !Character.isWhitespace(data.charAt(nextStartIndex))
      ) {
        nextStartIndex++;
      }
      while (
        nextStartIndex < data.length() &&
        Character.isWhitespace(data.charAt(nextStartIndex))
      ) {
        nextStartIndex++;
      }
      startIndex = nextStartIndex;
    }
    return chunks;
  }

  private int adjustEndToWordBoundary(
    String text,
    int startIndex,
    int targetEndIndex
  ) {
    if (
      targetEndIndex >= text.length() ||
      Character.isWhitespace(text.charAt(targetEndIndex - 1)) ||
      Character.isWhitespace(text.charAt(targetEndIndex))
    ) {
      return targetEndIndex;
    }

    int previousWhitespace = targetEndIndex - 1;
    while (
      previousWhitespace > startIndex &&
      !Character.isWhitespace(text.charAt(previousWhitespace))
    ) {
      previousWhitespace--;
    }
    if (previousWhitespace > startIndex) {
      return previousWhitespace;
    }

    int nextWhitespace = targetEndIndex;
    while (
      nextWhitespace < text.length() &&
      !Character.isWhitespace(text.charAt(nextWhitespace))
    ) {
      nextWhitespace++;
    }
    return nextWhitespace;
  }
}
