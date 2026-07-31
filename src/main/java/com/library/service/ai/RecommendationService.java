package com.library.service.ai;

import com.library.dto.ai.AIChatResponse;
import com.library.dto.ai.BookRecommendation;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full RAG pipeline for book recommendations.
 *
 * Flow:
 *   1. Query the vector store for semantically similar books
 *   2. Fetch live availability from MySQL for those books
 *   3. Optionally personalize with the user's borrow history
 *   4. Build a grounded prompt and call the Ollama LLM
 *   5. Return the LLM narrative + structured book cards
 */
@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private static final int TOP_K = 5;

    private final VectorSearchService vectorSearchService;
    private final ChatClient chatClient;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public RecommendationService(VectorSearchService vectorSearchService,
                                  ChatClient.Builder chatClientBuilder,
                                  BookRepository bookRepository,
                                  BorrowRecordRepository borrowRecordRepository) {
        this.vectorSearchService = vectorSearchService;
        this.chatClient = chatClientBuilder.build();
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * Generate personalised book recommendations using RAG.
     *
     * @param userMessage the user's natural-language request
     * @param userId      optional user ID for borrow-history personalisation
     * @return {@link AIChatResponse} containing the LLM narrative and book cards
     */
    public AIChatResponse recommend(String userMessage, Long userId) {
        // 1. Semantic similarity search
        List<Document> similarDocs = vectorSearchService.searchSimilar(userMessage, TOP_K);

        if (similarDocs.isEmpty()) {
            return new AIChatResponse(
                "I couldn't find any books in our catalog that match your request. Try asking about a genre, topic, or author.",
                List.of()
            );
        }

        // 2. Hydrate books from DB (live availability)
        List<Book> contextBooks = new ArrayList<>();
        for (Document doc : similarDocs) {
            Object bookIdObj = doc.getMetadata().get("bookId");
            if (bookIdObj != null) {
                try {
                    Long bookId = Long.parseLong(bookIdObj.toString());
                    Optional<Book> book = bookRepository.findById(bookId);
                    book.ifPresent(contextBooks::add);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (contextBooks.isEmpty()) {
            return new AIChatResponse(
                "I found some potential matches but couldn't retrieve the book details. Please try again.",
                List.of()
            );
        }

        // 3. Build personalization context from borrow history
        String borrowContext = buildBorrowContext(userId);

        // 4. Compose grounded prompt
        String prompt = buildPrompt(userMessage, contextBooks, borrowContext);

        // 5. Call LLM
        String answer = callLlm(prompt);

        // 6. Build structured book cards for UI
        List<BookRecommendation> recommendations = contextBooks.stream()
            .map(book -> new BookRecommendation(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory() != null ? book.getCategory().getName() : "Uncategorized",
                book.getAvailableCopies() > 0 ? "Available" : "Not Available",
                book.getAvailableCopies()
            ))
            .toList();

        return new AIChatResponse(answer, recommendations);
    }

    private String buildBorrowContext(Long userId) {
        if (userId == null) return "";
        try {
            List<String> recentTitles = borrowRecordRepository.findRecentBorrowTitlesByUserId(
                userId, PageRequest.of(0, 5)
            );
            if (!recentTitles.isEmpty()) {
                return "The user has previously borrowed: " + String.join(", ", recentTitles) + ".\n";
            }
        } catch (Exception e) {
            logger.warn("Could not fetch borrow history for userId={}: {}", userId, e.getMessage());
        }
        return "";
    }

    private String buildPrompt(String userMessage, List<Book> books, String borrowContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a knowledgeable and friendly library assistant. ")
              .append("Recommend books ONLY from the catalog below — never invent or suggest books not listed.\n\n");

        if (!borrowContext.isEmpty()) {
            prompt.append("Context about the reader: ").append(borrowContext).append("\n");
        }

        prompt.append("Library catalog (current availability):\n");
        for (Book book : books) {
            prompt.append("  • Title: ").append(book.getTitle())
                  .append(" | Author: ").append(book.getAuthor())
                  .append(" | Category: ").append(book.getCategory() != null ? book.getCategory().getName() : "Unknown")
                  .append(" | Available copies: ").append(book.getAvailableCopies())
                  .append("\n  Description: ").append(
                      book.getDescription() != null ? book.getDescription() : "No description available."
                  ).append("\n\n");
        }

        prompt.append("User request: ").append(userMessage).append("\n\n");
        prompt.append("For each book you recommend, use EXACTLY this format:\n\n");
        prompt.append("Book title: [title]\n");
        prompt.append("Author: [author]\n");
        prompt.append("Why this book matches: [one or two sentences]\n");
        prompt.append("Availability: [Available / Not Available]\n\n");
        prompt.append("Recommend only books from the catalog above.");

        return prompt.toString();
    }

    private String callLlm(String prompt) {
        try {
            return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        } catch (Exception e) {
            logger.error("LLM call failed: {}", e.getMessage());
            return "The AI assistant encountered an error. Please ensure Ollama is running and the model has been pulled. " +
                   "Run: `docker exec library-ollama ollama pull llama3.2`";
        }
    }
}
