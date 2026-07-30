package com.library.service;
import com.library.dto.SearchIndexDTO;
import com.library.repository.SearchIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for advanced search (Phase 9).
 */
@Service
@Transactional
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final SearchIndexRepository searchIndexRepository;

    public SearchService(SearchIndexRepository searchIndexRepository) {
        this.searchIndexRepository = searchIndexRepository;
    }

    @Transactional(readOnly = true)
    public Page<SearchIndexDTO> searchByKeywords(String keyword, Pageable pageable) {
        logger.info("Searching by keyword: {}", keyword);
        return searchIndexRepository.searchByKeywords(keyword, pageable).map(SearchIndexDTO::from);
    }

    public void indexBook(Long bookId, String keywords) {
        logger.info("Indexing book: {} with keywords: {}", bookId, keywords);
    }
}
