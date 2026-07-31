package com.library.service.ai;

import com.library.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

/**
 * Converts Book entities into embedding-ready text and delegates to Spring AI's EmbeddingModel.
 * Backed by Ollama's nomic-embed-text model.
 */
@Service
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Build a rich text representation of a book for embedding.
     * Combines the most semantically meaningful fields.
     */
    public String buildBookText(Book book) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(book.getTitle()).append(". ");
        sb.append("Author: ").append(book.getAuthor()).append(". ");

        if (book.getCategory() != null) {
            sb.append("Category: ").append(book.getCategory().getName()).append(". ");
        }
        if (book.getDescription() != null && !book.getDescription().isBlank()) {
            sb.append("Description: ").append(book.getDescription()).append(". ");
        }
        if (book.getPublisher() != null && !book.getPublisher().isBlank()) {
            sb.append("Publisher: ").append(book.getPublisher()).append(". ");
        }
        if (book.getPublicationYear() != null) {
            sb.append("Year: ").append(book.getPublicationYear()).append(". ");
        }

        return sb.toString().trim();
    }

    /**
     * Generate an embedding vector for the given text.
     */
    public float[] embed(String text) {
        logger.debug("Generating embedding for text of length {}", text.length());
        return embeddingModel.embed(text);
    }
}
