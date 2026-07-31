package com.library.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Runs once on application startup to bulk-index all books into ChromaDB.
 *
 * Ordered after DataSeeder (Order 1) so seed data is committed before indexing.
 * Failures are non-fatal: the app starts normally even if ChromaDB or Ollama
 * are not yet ready (e.g. models not yet pulled).
 */
@Component
@Order(10)
public class BookVectorIndexer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BookVectorIndexer.class);

    private final VectorSearchService vectorSearchService;

    public BookVectorIndexer(VectorSearchService vectorSearchService) {
        this.vectorSearchService = vectorSearchService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("BookVectorIndexer: starting startup book indexing...");
        try {
            vectorSearchService.indexAllBooks();
        } catch (Exception e) {
            logger.warn(
                "BookVectorIndexer: startup indexing failed ({}). " +
                "AI recommendations will not work until Ollama models are pulled and the app is restarted. " +
                "Run: docker exec library-ollama ollama pull nomic-embed-text && docker exec library-ollama ollama pull llama3.2",
                e.getMessage()
            );
        }
    }
}
