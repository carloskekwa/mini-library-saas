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
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGenerateReport() throws Exception {
        mockMvc.perform(post("/api/reports/popular-books").header("Authorization", "Bearer token"))
            .andExpect(status().isCreated());
    }

    @Test
    void testGetReports() throws Exception {
        mockMvc.perform(get("/api/reports").header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
    }
}
