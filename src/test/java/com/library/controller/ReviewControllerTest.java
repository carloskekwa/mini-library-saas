package com.library.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateReview() throws Exception {
        mockMvc.perform(post("/api/reviews?bookId=1&rating=5").header("Authorization", "Bearer token"))
            .andExpect(status().isCreated());
    }

    @Test
    void testGetBookReviews() throws Exception {
        mockMvc.perform(get("/api/reviews/book/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAverageRating() throws Exception {
        mockMvc.perform(get("/api/reviews/book/1/rating"))
            .andExpect(status().isOk());
    }
}
