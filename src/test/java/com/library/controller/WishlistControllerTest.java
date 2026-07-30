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
class WishlistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddToWishlist() throws Exception {
        mockMvc.perform(post("/api/wishlist?bookId=1").header("Authorization", "Bearer token"))
            .andExpect(status().isCreated());
    }

    @Test
    void testGetUserWishlist() throws Exception {
        mockMvc.perform(get("/api/wishlist").header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
    }

    @Test
    void testRemoveFromWishlist() throws Exception {
        mockMvc.perform(delete("/api/wishlist/1").header("Authorization", "Bearer token"))
            .andExpect(status().isNoContent());
    }
}
