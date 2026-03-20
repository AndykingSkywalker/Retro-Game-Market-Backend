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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void getItem_anonymousDefaultsIsWishlistedFalse() throws Exception {
        String adminToken = registerAndLogin();

        MvcResult created = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"Wave Race 64\",\"console\":\"N64\",\"genre\":\"Racing\",\"stockLevel\":10,\"price\":14.99,\"onSale\":false,\"saleDiscountPercent\":0}"))
                .andExpect(status().isCreated())
                .andReturn();

        int itemId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asInt();

        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isWishlisted").value(false));
    }

    @Test
    void getItems_authenticatedIncludesWishlistState() throws Exception {
        String adminToken = registerAndLogin();
        RegisteredUser user = registerAndLoginUser();

        int wishedItemId = createItem(adminToken, Map.of(
                "itemName", "Pokemon Stadium",
                "console", "N64",
                "genre", "Battle",
                "stockLevel", 12,
                "price", 29.99,
                "imageUrl", "https://cdn.example.com/pokemon-stadium.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));
        int notWishedItemId = createItem(adminToken, Map.of(
                "itemName", "Diddy Kong Racing",
                "console", "N64",
                "genre", "Racing",
                "stockLevel", 12,
                "price", 24.99,
                "imageUrl", "https://cdn.example.com/diddy-kong-racing.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        mockMvc.perform(post("/api/wishlists/users/" + user.id() + "/items")
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("itemId", wishedItemId))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/items/" + wishedItemId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isWishlisted").value(true));

        mockMvc.perform(get("/api/items/" + notWishedItemId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isWishlisted").value(false));

        MvcResult listResult = mockMvc.perform(get("/api/items")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(listResult.getResponse().getContentAsString());
        assertTrue(hasWishlistFlag(list, wishedItemId, true));
        assertTrue(hasWishlistFlag(list, notWishedItemId, false));
    }

    private int createItem(String adminToken, Map<String, Object> payload) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asInt();
    }

    private RegisteredUser registerAndLoginUser() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        String username = "item-user-" + suffix;
        String email = "item-user-" + suffix + "@example.com";
        String password = "secret123";

        String createJson = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "email", email,
                "password", password,
                "profilePicture", "https://cdn.example.com/avatars/" + suffix + ".png"
        ));

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        int id = created.get("id").asInt();
        String token = login(username, password);
        return new RegisteredUser(id, token);
    }

    private String registerAndLogin() throws Exception {
        return login("test-admin", "secret123");
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private boolean hasWishlistFlag(JsonNode items, int itemId, boolean expectedFlag) {
        for (JsonNode item : items) {
            if (item.path("id").asInt() == itemId) {
                return item.path("isWishlisted").asBoolean() == expectedFlag;
            }
        }
        return false;
    }

    private record RegisteredUser(int id, String token) {
    }
}
