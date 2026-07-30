package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BookDTO;
import com.library.dto.CreateBookRequest;
import com.library.dto.UpdateBookRequest;
import com.library.entity.Book;
import com.library.entity.Book.BookStatus;
import com.library.entity.Category;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BookController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book testBook;
    private Category testCategory;
    private CreateBookRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Fiction", "Fiction books");
        testCategory.setId(1L);

        testBook = new Book("The Great Gatsby", "F. Scott Fitzgerald", testCategory);
        testBook.setId(1L);
        testBook.setIsbn("978-0-7432-7356-5");
        testBook.setStatus(BookStatus.AVAILABLE);

        createRequest = new CreateBookRequest("The Great Gatsby", "F. Scott Fitzgerald", 1L);
        createRequest.setIsbn("978-0-7432-7356-5");
    }

    @Test
    void testGetAllBooksReturns200() throws Exception {
        // Arrange
        Page<Book> page = new PageImpl<>(Arrays.asList(testBook));
        when(bookService.getAllBooks(anyInt(), anyInt())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/books")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("The Great Gatsby"));
    }

    @Test
    void testGetBookByIdReturns200() throws Exception {
        // Arrange
        when(bookService.getBookById(1L)).thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(get("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("The Great Gatsby"));
    }

    @Test
    void testGetAvailableBooksReturns200() throws Exception {
        // Arrange
        Page<Book> page = new PageImpl<>(Arrays.asList(testBook));
        when(bookService.getAvailableBooks(anyInt(), anyInt())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/books/available")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].status").value("AVAILABLE"));
    }

    @Test
    void testSearchBooksReturns200() throws Exception {
        // Arrange
        Page<Book> page = new PageImpl<>(Arrays.asList(testBook));
        when(bookService.search(any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(post("/api/books/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"query\": \"Gatsby\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void testSearchByTitleReturns200() throws Exception {
        // Arrange
        Page<Book> page = new PageImpl<>(Arrays.asList(testBook));
        when(bookService.searchByTitle(anyString(), anyInt(), anyInt())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/books/search/title?title=Gatsby")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testSearchByAuthorReturns200() throws Exception {
        // Arrange
        Page<Book> page = new PageImpl<>(Arrays.asList(testBook));
        when(bookService.searchByAuthor(anyString(), anyInt(), anyInt())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/books/search/author?author=Fitzgerald")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")
    void testCreateBookReturns201() throws Exception {
        // Arrange
        when(bookService.createBook(anyString(), anyString(), anyLong(), any(), any()))
            .thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateBookWithoutAuthenticationReturns403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateBookReturns200() throws Exception {
        // Arrange
        UpdateBookRequest updateRequest = new UpdateBookRequest();
        updateRequest.setTitle("Updated Title");

        when(bookService.updateBook(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(put("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteBookReturns204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

}
