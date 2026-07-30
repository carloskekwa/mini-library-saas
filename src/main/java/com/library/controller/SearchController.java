package com.library.controller;
import com.library.dto.SearchIndexDTO;
import com.library.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for advanced search (Phase 9).
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Advanced Search", description = "APIs for advanced book search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    @Operation(summary = "Search by keywords")
    public ResponseEntity<Page<SearchIndexDTO>> search(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<SearchIndexDTO> results = searchService.searchByKeywords(keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
