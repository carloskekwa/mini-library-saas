package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BorrowBookRequest;
import com.library.dto.ReturnBookRequest;
import com.library.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for BorrowController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowService borrowService;

    private BorrowBookRequest borrowRequest;
    private ReturnBookRequest returnRequest;

    @BeforeEach
    void setUp() {
        borrowRequest = new BorrowBookRequest(1L);
        returnRequest = new ReturnBookRequest(1L, "GOOD");
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testBorrowBookReturns201() throws Exception {
        mockMvc.perform(post("/api/borrow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(borrowRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void testBorrowBookWithoutAuthenticationReturns403() throws Exception {
        mockMvc.perform(post("/api/borrow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(borrowRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testReturnBookReturns200() throws Exception {
        mockMvc.perform(post("/api/borrow/return")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(returnRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testRenewBorrowReturns200() throws Exception {
        mockMvc.perform(post("/api/borrow/1/renew"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetBorrowHistoryReturns200() throws Exception {
        mockMvc.perform(get("/api/borrow/history"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetActiveBorrowsReturns200() throws Exception {
        mockMvc.perform(get("/api/borrow/active"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "LIBRARIAN")
    void testGetOverdueRecordsReturns200() throws Exception {
        mockMvc.perform(get("/api/borrow/overdue"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetOverdueRecordsWithoutAuthenticationReturns403() throws Exception {
        mockMvc.perform(get("/api/borrow/overdue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetReturnHistoryReturns200() throws Exception {
        mockMvc.perform(get("/api/borrow/returns"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testPayFineReturns200() throws Exception {
        mockMvc.perform(post("/api/borrow/fines/1/pay"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetTotalUnpaidFinesReturns200() throws Exception {
        mockMvc.perform(get("/api/borrow/fines/total"))
            .andExpect(status().isOk());
    }

}
