package com.library.repository;
import com.library.entity.SearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchIndexRepository extends JpaRepository<SearchIndex, Long> {
    Page<SearchIndex> findByBookId(Long bookId, Pageable pageable);
    
    @Query("SELECT s FROM SearchIndex s WHERE s.keywords LIKE %?1%")
    Page<SearchIndex> searchByKeywords(String keyword, Pageable pageable);
}
