package com.library.controller;

import com.library.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for NotificationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetAllNotificationsReturns200() throws Exception {
        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAllNotificationsWithoutAuthenticationReturns403() throws Exception {
        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetUnreadNotificationsReturns200() throws Exception {
        mockMvc.perform(get("/api/notifications/unread"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testGetUnreadCountReturns200() throws Exception {
        mockMvc.perform(get("/api/notifications/count/unread"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testMarkAsReadReturns200() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testMarkAllAsReadReturns200() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testDeleteNotificationReturns204() throws Exception {
        mockMvc.perform(delete("/api/notifications/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1", roles = "MEMBER")
    void testDeleteAllNotificationsReturns204() throws Exception {
        mockMvc.perform(delete("/api/notifications"))
            .andExpect(status().isNoContent());
    }

}
