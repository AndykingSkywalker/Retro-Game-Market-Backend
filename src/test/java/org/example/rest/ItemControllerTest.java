package org.example.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                        .content("{\"itemName\":\"Chrono Trigger\",\"console\":\"SNES\",\"genre\":\"RPG\",\"stockLevel\":10,\"price\":79.99,\"imageUrl\":\"https://cdn.example.com/chrono-trigger.jpg\",\"onSale\":true,\"saleDiscountPercent\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("https://cdn.example.com/chrono-trigger.jpg"))
                .andExpect(jsonPath("$.itemName").value("Chrono Trigger"))
                .andExpect(jsonPath("$.saleDiscountPercent").value(15));
    }

    @Test
    void updateItem_persistsSaleDiscountPercent() throws Exception {
        String token = registerAndLogin();

        MvcResult created = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"Mega Man X\",\"console\":\"SNES\",\"genre\":\"Action\",\"stockLevel\":5,\"price\":59.99,\"onSale\":true,\"saleDiscountPercent\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(created.getResponse().getContentAsString());
        int itemId = createdJson.get("id").asInt();

        mockMvc.perform(put("/api/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"saleDiscountPercent\":25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saleDiscountPercent").value(25));
    }

    @Test
    void createItem_rejectsSaleDiscountPercentAbove100() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"Donkey Kong Country\",\"console\":\"SNES\",\"genre\":\"Platformer\",\"stockLevel\":8,\"price\":39.99,\"onSale\":true,\"saleDiscountPercent\":120}"))
                .andExpect(status().isBadRequest());
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
