package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.CreateReservationRequest;
import com.library.service.ReservationService;
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
 * Integration tests for ReservationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private CreateReservationRequest reservationRequest;

    @BeforeEach
    void setUp() {
        reservationRequest = new CreateReservationRequest(1L);
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testReserveBookReturns201() throws Exception {
        mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reservationRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void testReserveBookWithoutAuthenticationReturns403() throws Exception {
        mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reservationRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testCancelReservationReturns204() throws Exception {
        mockMvc.perform(delete("/api/reservations/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetUserReservationsReturns200() throws Exception {
        mockMvc.perform(get("/api/reservations"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetActiveReservationsReturns200() throws Exception {
        mockMvc.perform(get("/api/reservations/active"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "LIBRARIAN")
    void testGetBookReservationQueueReturns200() throws Exception {
        mockMvc.perform(get("/api/reservations/book/1/queue"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetBookReservationQueueWithoutAuthenticationReturns403() throws Exception {
        mockMvc.perform(get("/api/reservations/book/1/queue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "LIBRARIAN")
    void testFulfillReservationReturns200() throws Exception {
        mockMvc.perform(put("/api/reservations/1/fulfill"))
            .andExpect(status().isOk());
    }

}
