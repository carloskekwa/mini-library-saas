package com.library.controller;

import com.library.dto.ai.AIChatRequest;
import com.library.dto.ai.AIChatResponse;
import com.library.dto.ai.AIInsightRequest;
import com.library.dto.ai.AIInsightResponse;
import com.library.service.ai.LibrarianInsightService;
import com.library.service.ai.RecommendationService;
import com.library.service.ai.VectorSearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AI-powered librarian assistant features.
 *
 * <ul>
 *   <li>POST /api/ai/recommend — book recommendations (MEMBER, LIBRARIAN, ADMIN)</li>
 *   <li>POST /api/ai/insights  — librarian analytics (LIBRARIAN, ADMIN)</li>
 *   <li>POST /api/ai/reindex   — re-embed all books (ADMIN only)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    private final RecommendationService recommendationService;
    private final LibrarianInsightService librarianInsightService;
    private final VectorSearchService vectorSearchService;

    public AIController(RecommendationService recommendationService,
                        LibrarianInsightService librarianInsightService,
                        VectorSearchService vectorSearchService) {
        this.recommendationService = recommendationService;
        this.librarianInsightService = librarianInsightService;
        this.vectorSearchService = vectorSearchService;
    }

    /**
     * AI book recommendations via RAG.
     * Accessible to all authenticated users (members see it in their workspace).
     */
    @PostMapping("/recommend")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    public ResponseEntity<AIChatResponse> recommend(@Valid @RequestBody AIChatRequest request) {
        logger.debug("AI recommend request: '{}'", request.getMessage());
        AIChatResponse response = recommendationService.recommend(request.getMessage(), request.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * AI librarian insights backed by real library statistics.
     * Restricted to LIBRARIAN and ADMIN roles.
     */
    @PostMapping("/insights")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<AIInsightResponse> insights(@Valid @RequestBody AIInsightRequest request) {
        logger.debug("AI insights request: '{}'", request.getQuestion());
        AIInsightResponse response = librarianInsightService.analyze(request.getQuestion());
        return ResponseEntity.ok(response);
    }

    /**
     * Trigger a full re-indexing of all books in the vector store.
     * Restricted to ADMIN role.
     */
    @PostMapping("/reindex")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reindex() {
        logger.info("Manual vector re-index triggered by admin");
        vectorSearchService.indexAllBooks();
        return ResponseEntity.noContent().build();
    }
}
