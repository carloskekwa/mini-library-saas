package com.library.repository;
import com.library.entity.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findByUserId(Long userId);
    Page<BookRequest> findByStatus(BookRequest.RequestStatus status, Pageable pageable);
    Page<BookRequest> findByUserId(Long userId, Pageable pageable);
    Page<BookRequest> findAllByOrderByRequestedAtDesc(Pageable pageable);
}
