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
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void checkout_createsOrder_clearsCart_andDecrementsStock() throws Exception {
        RegisteredUser user = registerAndLoginUser();
        String adminToken = loginAdmin();

        int chronoTriggerId = createItem(adminToken, Map.of(
                "itemName", "Chrono Trigger",
                "console", "SNES",
                "genre", "RPG",
                "stockLevel", 5,
                "price", 20.0,
                "imageUrl", "https://cdn.example.com/chrono-trigger.jpg",
                "onSale", true,
                "saleDiscountPercent", 25
        ));
        int fZeroId = createItem(adminToken, Map.of(
                "itemName", "F-Zero",
                "console", "SNES",
                "genre", "Racing",
                "stockLevel", 3,
                "price", 15.0,
                "imageUrl", "https://cdn.example.com/f-zero.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addItemToCart(user, chronoTriggerId, 2);
        addItemToCart(user, fZeroId, 1);

        mockMvc.perform(post("/api/orders/users/" + user.id() + "/checkout")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber", startsWith("RGM-")))
                .andExpect(jsonPath("$.userId").value(user.id()))
                .andExpect(jsonPath("$.lineItemCount").value(2))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.totalAmount").value(45.0))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].itemId").value(chronoTriggerId))
                .andExpect(jsonPath("$.items[0].discountedUnitPrice").value(15.0))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].lineTotal").value(30.0))
                .andExpect(jsonPath("$.items[1].itemId").value(fZeroId))
                .andExpect(jsonPath("$.items[1].discountedUnitPrice").value(15.0))
                .andExpect(jsonPath("$.items[1].quantity").value(1))
                .andExpect(jsonPath("$.items[1].lineTotal").value(15.0));

        mockMvc.perform(get("/api/items/" + chronoTriggerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockLevel").value(3));

        mockMvc.perform(get("/api/items/" + fZeroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockLevel").value(2));

        mockMvc.perform(get("/api/carts/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total").value(0.0));

        mockMvc.perform(get("/api/orders/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderNumber", startsWith("RGM-")))
                .andExpect(jsonPath("$[0].totalQuantity").value(3))
                .andExpect(jsonPath("$[0].items", hasSize(2)));
    }

    @Test
    void checkout_rejectsEmptyCart() throws Exception {
        RegisteredUser user = registerAndLoginUser();

        mockMvc.perform(post("/api/orders/users/" + user.id() + "/checkout")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("cart is empty"));
    }

    @Test
    void checkout_rejectsInsufficientStockWithoutCreatingOrder() throws Exception {
        RegisteredUser user = registerAndLoginUser();
        String adminToken = loginAdmin();

        int limitedItemId = createItem(adminToken, Map.of(
                "itemName", "EarthBound",
                "console", "SNES",
                "genre", "RPG",
                "stockLevel", 1,
                "price", 80.0,
                "imageUrl", "https://cdn.example.com/earthbound.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addItemToCart(user, limitedItemId, 2);

        mockMvc.perform(post("/api/orders/users/" + user.id() + "/checkout")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Not enough stock for item 'EarthBound'. Requested 2 but only 1 left"));

        mockMvc.perform(get("/api/items/" + limitedItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockLevel").value(1));

        mockMvc.perform(get("/api/orders/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/carts/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void orderHistory_isVisibleToSelfOrAdminOnly() throws Exception {
        RegisteredUser userOne = registerAndLoginUser();
        RegisteredUser userTwo = registerAndLoginUser();
        String adminToken = loginAdmin();

        int itemId = createItem(adminToken, Map.of(
                "itemName", "Super Metroid",
                "console", "SNES",
                "genre", "Action",
                "stockLevel", 4,
                "price", 30.0,
                "imageUrl", "https://cdn.example.com/super-metroid.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addItemToCart(userOne, itemId, 1);
        mockMvc.perform(post("/api/orders/users/" + userOne.id() + "/checkout")
                        .header("Authorization", "Bearer " + userOne.token()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders/users/" + userOne.id())
                        .header("Authorization", "Bearer " + userTwo.token()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/orders/users/" + userOne.id())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderNumber", startsWith("RGM-")))
                .andExpect(jsonPath("$[0].userId").value(userOne.id()));
    }

    @Test
    void adminCanViewAllOrders_andOrderNumbersAreUnique() throws Exception {
        RegisteredUser userOne = registerAndLoginUser();
        RegisteredUser userTwo = registerAndLoginUser();
        String adminToken = loginAdmin();

        int firstItemId = createItem(adminToken, Map.of(
                "itemName", "Secret of Mana",
                "console", "SNES",
                "genre", "RPG",
                "stockLevel", 4,
                "price", 40.0,
                "imageUrl", "https://cdn.example.com/secret-of-mana.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));
        int secondItemId = createItem(adminToken, Map.of(
                "itemName", "Pilotwings",
                "console", "SNES",
                "genre", "Simulation",
                "stockLevel", 4,
                "price", 25.0,
                "imageUrl", "https://cdn.example.com/pilotwings.jpg",
                "onSale", false,
                "saleDiscountPercent", 0
        ));

        addItemToCart(userOne, firstItemId, 1);
        MvcResult firstCheckout = mockMvc.perform(post("/api/orders/users/" + userOne.id() + "/checkout")
                        .header("Authorization", "Bearer " + userOne.token()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber", startsWith("RGM-")))
                .andReturn();

        addItemToCart(userTwo, secondItemId, 1);
        MvcResult secondCheckout = mockMvc.perform(post("/api/orders/users/" + userTwo.id() + "/checkout")
                        .header("Authorization", "Bearer " + userTwo.token()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber", startsWith("RGM-")))
                .andReturn();

        String firstOrderNumber = objectMapper.readTree(firstCheckout.getResponse().getContentAsString()).get("orderNumber").asText();
        String secondOrderNumber = objectMapper.readTree(secondCheckout.getResponse().getContentAsString()).get("orderNumber").asText();
        assertNotEquals(firstOrderNumber, secondOrderNumber);

        MvcResult allOrders = mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber", startsWith("RGM-")))
                .andExpect(jsonPath("$[1].orderNumber", startsWith("RGM-")))
                .andReturn();

        JsonNode orders = objectMapper.readTree(allOrders.getResponse().getContentAsString());
        assertTrue(orders.isArray() && orders.size() >= 2);
        assertTrue(containsOrderNumber(orders, firstOrderNumber));
        assertTrue(containsOrderNumber(orders, secondOrderNumber));
    }

    @Test
    void allOrders_isAdminOnly() throws Exception {
        RegisteredUser user = registerAndLoginUser();

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    private void addItemToCart(RegisteredUser user, int itemId, int quantity) throws Exception {
        mockMvc.perform(post("/api/carts/users/" + user.id() + "/items")
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "itemId", itemId,
                                "quantity", quantity
                        ))))
                .andExpect(status().isOk());
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
        String username = "order-user-" + suffix;
        String email = "order-user-" + suffix + "@example.com";
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

    private boolean containsOrderNumber(JsonNode orders, String orderNumber) {
        for (JsonNode order : orders) {
            if (orderNumber.equals(order.path("orderNumber").asText())) {
                return true;
            }
        }
        return false;
    }

    private record RegisteredUser(int id, String token) {
    }
}




