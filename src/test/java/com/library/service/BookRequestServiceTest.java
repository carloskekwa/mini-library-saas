package com.library.service;
import com.library.dto.BookRequestDTO;
import com.library.entity.BookRequest;
import com.library.entity.User;
import com.library.repository.BookRequestRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookRequestServiceTest {
    @Mock
    private BookRequestRepository bookRequestRepository;
    @Mock
    private UserRepository userRepository;
    
    private BookRequestService bookRequestService;

    @BeforeEach
    void setUp() {
        bookRequestService = new BookRequestService(bookRequestRepository, userRepository);
    }

    @Test
    void testCreateRequest() {
        User user = new User("user1", "user1@email.com", "hashed");
        BookRequest request = new BookRequest(user, "Book Title", "Author", "ISBN");
        request.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(request);

        BookRequestDTO result = bookRequestService.createRequest(1L, "Book Title", "Author", "ISBN", "Need this");

        assertNotNull(result);
        assertEquals("Book Title", result.getBookTitle());
        verify(bookRequestRepository, times(1)).save(any(BookRequest.class));
    }

    @Test
    void testGetPendingRequests() {
        User user = new User("user1", "user1@email.com", "hashed");
        BookRequest request = new BookRequest(user, "Book Title", "Author", "ISBN");
        request.setId(1L);
        request.setStatus(BookRequest.RequestStatus.PENDING);
        Page<BookRequest> page = new PageImpl<>(Arrays.asList(request));

        when(bookRequestRepository.findByStatus(BookRequest.RequestStatus.PENDING, PageRequest.of(0, 20))).thenReturn(page);

        Page<BookRequestDTO> result = bookRequestService.getPendingRequests(PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testApproveRequest() {
        User user = new User("user1", "user1@email.com", "hashed");
        BookRequest request = new BookRequest(user, "Book Title", "Author", "ISBN");
        request.setId(1L);

        when(bookRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(request);

        bookRequestService.approveRequest(1L);

        assertEquals(BookRequest.RequestStatus.APPROVED, request.getStatus());
        verify(bookRequestRepository, times(1)).save(any(BookRequest.class));
    }
}
