package org.example.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createItem_requiresItemName() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"console\":\"SNES\",\"genre\":\"RPG\",\"stockLevel\":10,\"price\":12.5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_acceptsImageUrl() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"Chrono Trigger\",\"console\":\"SNES\",\"genre\":\"RPG\",\"stockLevel\":10,\"price\":79.99,\"imageUrl\":\"https://cdn.example.com/chrono-trigger.jpg\",\"onSale\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("https://cdn.example.com/chrono-trigger.jpg"))
                .andExpect(jsonPath("$.itemName").value("Chrono Trigger"));
    }

    private String registerAndLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test-admin\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return body.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }
}
