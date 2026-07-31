package com.library.service.ai;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages book embeddings in ChromaDB via Spring AI's VectorStore abstraction.
 * Provides indexing, similarity search, and deletion operations.
 */
@Service
public class VectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final BookRepository bookRepository;

    public VectorSearchService(VectorStore vectorStore,
                                EmbeddingService embeddingService,
                                BookRepository bookRepository) {
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.bookRepository = bookRepository;
    }

    /**
     * Index (upsert) a single book into the vector store.
     * Uses the book's database ID as the document ID for idempotency.
     */
    public void indexBook(Book book) {
        String text = embeddingService.buildBookText(book);
        String docId = "book-" + book.getId();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookId", String.valueOf(book.getId()));
        metadata.put("title", book.getTitle());
        metadata.put("author", book.getAuthor());
        metadata.put("category", book.getCategory() != null ? book.getCategory().getName() : "");

        // Delete existing entry first (upsert behaviour)
        try {
            vectorStore.delete(List.of(docId));
        } catch (Exception ignored) {
            // Document may not exist on first index
        }

        Document document = new Document(docId, text, metadata);
        vectorStore.add(List.of(document));
        logger.debug("Indexed book id={} title='{}'", book.getId(), book.getTitle());
    }

    /**
     * Run a semantic similarity search against the vector store.
     *
     * @param query natural-language query from the user
     * @param topK  number of results to return
     * @return matching Document objects with metadata
     */
    public List<Document> searchSimilar(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build()
        );
    }

    /**
     * Remove a book from the vector store when it is deleted from the catalog.
     */
    public void deleteBook(Long bookId) {
        try {
            vectorStore.delete(List.of("book-" + bookId));
            logger.debug("Removed book id={} from vector store", bookId);
        } catch (Exception e) {
            logger.warn("Could not remove book id={} from vector store: {}", bookId, e.getMessage());
        }
    }

    /**
     * Bulk-index all books in the catalog.
     * Called once on application startup via {@link BookVectorIndexer}.
     */
    public void indexAllBooks() {
        List<Book> books = bookRepository.findAll();
        logger.info("Starting bulk vector indexing of {} books...", books.size());
        int indexed = 0;
        for (Book book : books) {
            try {
                indexBook(book);
                indexed++;
            } catch (Exception e) {
                logger.warn("Failed to index book id={}: {}", book.getId(), e.getMessage());
            }
        }
        logger.info("Bulk vector indexing complete: {}/{} books indexed.", indexed, books.size());
    }
}
