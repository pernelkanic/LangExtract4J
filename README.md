# LangExtract4J
LangExtract4J is a Java library for langextract.

## For example:
Arjun joined Microsoft in 2023.
We want the LLM to identify information such as a person's name, a
company, and a year, and return them in a structured format that Java
code can use.

## How the pipeline works
What each step does
1. TextChunker: Splits a large document into smaller pieces and
   records each chunk's position in the original text.
2. PromptBuilder: Creates a prompt using the user's instructions,
   optional examples, the expected JSON format, and the current chunk.
3. LlmClient: Sends the prompt to an LLM provider and returns the
   response.
4. ExtractionParser: Parses the response JSON into Java extraction
   objects.
5. SourceAligner: Locates extracted phrases in the source text and
   calculates their start and end positions.
6. LangExtract: Coordinates the components, processes all chunks,
   combines the results, and returns an AnnotatedDocument.
   This is an extraction pipeline, not a RAG system. It does not need a
   vector database or embeddings for its initial implementation.
   How we can improve it

## Improvisation points:
- Better chunking: Split text at sentence or paragraph boundaries
  instead of cutting at a fixed character count.
- More reliable output: Validate LLM responses and handle invalid
  JSON or missing fields clearly.
- Better source alignment: Handle repeated phrases, punctuation
  differences, and text that cannot be matched exactly.
- Remove duplicates: Overlapping chunks can produce the same
  extraction more than once, so results need deduplication.
- Add some custom exceptions and Loggers.