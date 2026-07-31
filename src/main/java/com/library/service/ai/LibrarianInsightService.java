package com.library.service.ai;

import com.library.dto.ai.AIInsightResponse;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Provides AI-powered analytics insights for librarians and admins.
 *
 * Gathers real statistics from the database (borrow counts, availability gaps,
 * never-borrowed books) and feeds them as grounded context to the LLM.
 */
@Service
public class LibrarianInsightService {

    private static final Logger logger = LoggerFactory.getLogger(LibrarianInsightService.class);

    private final ChatClient chatClient;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public LibrarianInsightService(ChatClient.Builder chatClientBuilder,
                                    BookRepository bookRepository,
                                    BorrowRecordRepository borrowRecordRepository) {
        this.chatClient = chatClientBuilder.build();
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * Analyze a librarian's question using real library statistics as RAG context.
     *
     * @param question free-form librarian question
     * @return {@link AIInsightResponse} with LLM-generated analysis
     */
    public AIInsightResponse analyze(String question) {
        String context = buildStatisticsContext();
        String prompt = buildInsightPrompt(context, question);
        String analysis = callLlm(prompt);

        return new AIInsightResponse(analysis, DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
    }

    private String buildStatisticsContext() {
        StringBuilder ctx = new StringBuilder();
        ctx.append("=== LIBRARY STATISTICS ===\n\n");

        // Most borrowed books (top 10)
        try {
            List<Object[]> borrowStats = borrowRecordRepository.findBookBorrowCounts(PageRequest.of(0, 10));
            if (!borrowStats.isEmpty()) {
                ctx.append("Top 10 most borrowed books:\n");
                for (Object[] row : borrowStats) {
                    ctx.append("  • \"").append(row[0]).append("\" by ").append(row[1])
                       .append(" — borrowed ").append(row[2]).append(" time(s)")
                       .append(", available copies now: ").append(row[3]).append("\n");
                }
                ctx.append("\n");
            }
        } catch (Exception e) {
            logger.warn("Could not fetch borrow stats: {}", e.getMessage());
        }

        // Books with 0 available copies and pending reservations
        try {
            List<Object[]> unavailable = bookRepository.findUnavailableBooksWithPendingReservations();
            if (!unavailable.isEmpty()) {
                ctx.append("Books with 0 available copies that have waiting reservations:\n");
                for (Object[] row : unavailable) {
                    ctx.append("  • \"").append(row[0]).append("\" — ")
                       .append(row[1]).append(" reservation(s) pending\n");
                }
                ctx.append("\n");
            }
        } catch (Exception e) {
            logger.warn("Could not fetch unavailable books with reservations: {}", e.getMessage());
        }

        // Books never borrowed
        try {
            List<String> neverBorrowed = bookRepository.findNeverBorrowedBookTitles();
            if (!neverBorrowed.isEmpty()) {
                ctx.append("Books that have never been borrowed (").append(neverBorrowed.size()).append(" total):\n");
                neverBorrowed.forEach(title -> ctx.append("  • \"").append(title).append("\"\n"));
                ctx.append("\n");
            }
        } catch (Exception e) {
            logger.warn("Could not fetch never-borrowed books: {}", e.getMessage());
        }

        ctx.append("Total books in catalog: ").append(bookRepository.count()).append("\n");
        return ctx.toString();
    }

    private String buildInsightPrompt(String context, String question) {
        return "You are an expert library analytics assistant. " +
               "Use the statistics below to answer the librarian's question with specific, actionable insights.\n\n" +
               context +
               "\n=== LIBRARIAN QUESTION ===\n" +
               question +
               "\n\nProvide a clear, structured analysis referencing the actual data. " +
               "Include specific book titles and numbers where relevant.";
    }

    private String callLlm(String prompt) {
        try {
            return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        } catch (Exception e) {
            logger.error("LLM insights call failed: {}", e.getMessage());
            return "The AI assistant encountered an error. Please ensure Ollama is running and the model has been pulled. " +
                   "Run: `docker exec library-ollama ollama pull llama3.2`";
        }
    }
}
