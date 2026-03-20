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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addItemToWishlist_andListByUser() throws Exception {
        RegisteredUser user = registerAndLoginUser();
        String adminToken = loginAdmin();
        int itemId = createItem(adminToken, Map.of(
                "itemName", "GoldenEye 007",
                "console", "N64",
                "genre", "Shooter",
                "stockLevel", 10,
                "price", 25.0,
                "imageUrl", "https://cdn.example.com/goldeneye.jpg",
                "onSale", true,
                "saleDiscountPercent", 20
        ));

        addWishlistItem(user, itemId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.id()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemId").value(itemId))
                .andExpect(jsonPath("$.items[0].saleDiscountPercent").value(20.0));

        mockMvc.perform(get("/api/wishlists/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemName").value("GoldenEye 007"));
    }

    @Test
    void addItemToWishlist_isIdempotentForDuplicates() throws Exception {
        RegisteredUser user = registerAndLoginUser();
        String adminToken = loginAdmin();
        int itemId = createItem(adminToken, Map.of(
                "itemName", "Star Fox 64",
                "console", "N64",
                "genre", "Shooter",
                "stockLevel", 8,
                "price", 19.0,
                "imageUrl", "https://cdn.example.com/starfox64.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addWishlistItem(user, itemId).andExpect(status().isOk());
        addWishlistItem(user, itemId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void removeAndClearWishlist_workAsExpected() throws Exception {
        RegisteredUser user = registerAndLoginUser();
        String adminToken = loginAdmin();

        int firstItemId = createItem(adminToken, Map.of(
                "itemName", "Banjo-Kazooie",
                "console", "N64",
                "genre", "Platformer",
                "stockLevel", 7,
                "price", 17.0,
                "imageUrl", "https://cdn.example.com/banjo.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));
        int secondItemId = createItem(adminToken, Map.of(
                "itemName", "Mario Tennis",
                "console", "N64",
                "genre", "Sports",
                "stockLevel", 9,
                "price", 12.0,
                "imageUrl", "https://cdn.example.com/mariotennis.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addWishlistItem(user, firstItemId).andExpect(status().isOk());
        addWishlistItem(user, secondItemId).andExpect(status().isOk());

        mockMvc.perform(delete("/api/wishlists/users/" + user.id() + "/items/" + firstItemId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wishlists/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemId").value(secondItemId));

        mockMvc.perform(delete("/api/wishlists/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wishlists/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void wishlistVisibility_isSelfOrAdminOnly() throws Exception {
        RegisteredUser userOne = registerAndLoginUser();
        RegisteredUser userTwo = registerAndLoginUser();
        String adminToken = loginAdmin();

        String adminItemToken = loginAdmin();
        int itemId = createItem(adminItemToken, Map.of(
                "itemName", "Perfect Dark",
                "console", "N64",
                "genre", "Shooter",
                "stockLevel", 6,
                "price", 21.0,
                "imageUrl", "https://cdn.example.com/perfectdark.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addWishlistItem(userOne, itemId).andExpect(status().isOk());

        mockMvc.perform(get("/api/wishlists/users/" + userOne.id())
                        .header("Authorization", "Bearer " + userTwo.token()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/wishlists/users/" + userOne.id())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    private org.springframework.test.web.servlet.ResultActions addWishlistItem(RegisteredUser user, int itemId) throws Exception {
        return mockMvc.perform(post("/api/wishlists/users/" + user.id() + "/items")
                .header("Authorization", "Bearer " + user.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("itemId", itemId))));
    }

    private int createItem(String adminToken, Map<String, Object> payload) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        return created.get("id").asInt();
    }

    private RegisteredUser registerAndLoginUser() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        String username = "wishlist-user-" + suffix;
        String email = "wishlist-user-" + suffix + "@example.com";
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

    private String loginAdmin() throws Exception {
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

    private record RegisteredUser(int id, String token) {
    }
}

