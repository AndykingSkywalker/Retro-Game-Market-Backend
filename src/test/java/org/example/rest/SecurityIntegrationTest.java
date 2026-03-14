package org.example.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicItemRead_remainsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk());
    }

    @Test
    void customerCannotListAllUsers() throws Exception {
        AuthSession customer = registerCustomerAndLogin("customer-users", "customer-users@example.com", "secret123");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", customer.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListAllUsers() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();

        mockMvc.perform(get("/api/users")
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItem("test-admin")))
                .andExpect(jsonPath("$[*].role", hasItem("ADMIN")));
    }

    @Test
    void customerCannotCreateItems() throws Exception {
        AuthSession customer = registerCustomerAndLogin("customer-item", "customer-item@example.com", "secret123");

        mockMvc.perform(post("/api/items")
                        .header("Authorization", customer.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"EarthBound\",\"console\":\"SNES\",\"genre\":\"RPG\",\"stockLevel\":4,\"price\":199.99}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateItems() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", admin.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"EarthBound\",\"console\":\"SNES\",\"genre\":\"RPG\",\"stockLevel\":4,\"price\":199.99,\"imageUrl\":\"https://cdn.example.com/earthbound.jpg\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("EarthBound"));
    }

    @Test
    void customerCanReadOwnUserProfileButNotAnotherUsers() throws Exception {
        AuthSession customerA = registerCustomerAndLogin("customer-a", "customer-a@example.com", "secret123");
        AuthSession customerB = registerCustomerAndLogin("customer-b", "customer-b@example.com", "secret123");

        mockMvc.perform(get("/api/users/" + customerA.userId())
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("customer-a"));

        mockMvc.perform(get("/api/users/" + customerB.userId())
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadAnotherUsersProfile() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();
        AuthSession customer = registerCustomerAndLogin("customer-profile", "customer-profile@example.com", "secret123");

        mockMvc.perform(get("/api/users/" + customer.userId())
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("customer-profile"));
    }

    @Test
    void adminCanPromoteCustomerToAdmin() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();
        AuthSession customer = registerCustomerAndLogin("customer-promote", "customer-promote@example.com", "secret123");

        mockMvc.perform(post("/api/users/" + customer.userId() + "/promote-admin")
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.userId()))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"customer-promote\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void customerCannotPromoteAnotherUserToAdmin() throws Exception {
        AuthSession customerA = registerCustomerAndLogin("customer-promote-a", "customer-promote-a@example.com", "secret123");
        AuthSession customerB = registerCustomerAndLogin("customer-promote-b", "customer-promote-b@example.com", "secret123");

        mockMvc.perform(post("/api/users/" + customerB.userId() + "/promote-admin")
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDemoteUserToCustomer() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();
        AuthSession customer = registerCustomerAndLogin("customer-demote", "customer-demote@example.com", "secret123");

        mockMvc.perform(post("/api/users/" + customer.userId() + "/promote-admin")
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(post("/api/users/" + customer.userId() + "/demote-customer")
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.userId()))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"customer-demote\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void customerCannotDemoteAnotherUser() throws Exception {
        AuthSession customerA = registerCustomerAndLogin("customer-demote-a", "customer-demote-a@example.com", "secret123");
        AuthSession customerB = registerCustomerAndLogin("customer-demote-b", "customer-demote-b@example.com", "secret123");

        mockMvc.perform(post("/api/users/" + customerB.userId() + "/demote-customer")
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerCanUseOwnCartButNotAnotherUsersCart() throws Exception {
        AuthSession customerA = registerCustomerAndLogin("cart-a", "cart-a@example.com", "secret123");
        AuthSession customerB = registerCustomerAndLogin("cart-b", "cart-b@example.com", "secret123");
        AuthSession admin = loginBootstrappedAdmin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", admin.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"GoldenEye 007\",\"console\":\"N64\",\"genre\":\"Shooter\",\"stockLevel\":5,\"price\":49.99}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(post("/api/carts/users/" + customerA.userId() + "/items")
                        .header("Authorization", customerA.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customerA.userId()));

        mockMvc.perform(get("/api/carts/users/" + customerA.userId())
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/carts/users/" + customerB.userId())
                        .header("Authorization", customerA.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadAnyUsersCart() throws Exception {
        AuthSession customer = registerCustomerAndLogin("cart-customer", "cart-customer@example.com", "secret123");
        AuthSession admin = loginBootstrappedAdmin();

        mockMvc.perform(post("/api/items")
                        .header("Authorization", admin.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"Metroid Prime\",\"console\":\"GameCube\",\"genre\":\"Adventure\",\"stockLevel\":3,\"price\":59.99}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/carts/users/" + customer.userId() + "/items")
                        .header("Authorization", customer.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"quantity\":2}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/carts/users/" + customer.userId())
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer.userId()));
    }

    @Test
    void loginResponseIncludesRole() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test-admin\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void customerCanReadAuthMe() throws Exception {
        AuthSession customer = registerCustomerAndLogin("me-customer", "me-customer@example.com", "secret123");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", customer.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.userId()))
                .andExpect(jsonPath("$.username").value("me-customer"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void adminCanReadAuthMe() throws Exception {
        AuthSession admin = loginBootstrappedAdmin();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", admin.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void authMeRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    private AuthSession registerCustomerAndLogin(String username, String email, String password) throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andReturn();

        String registerBody = registerResult.getResponse().getContentAsString();
        int userId = Integer.parseInt(registerBody.replaceAll(".*\"id\":(\\d+).*", "$1"));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andReturn();

        String loginBody = loginResult.getResponse().getContentAsString();
        String token = loginBody.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        return new AuthSession(userId, token);
    }

    private AuthSession loginBootstrappedAdmin() throws Exception {
        MvcResult userResult = mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + loginBootstrappedAdminToken()))
                .andExpect(status().isOk())
                .andReturn();

        String body = userResult.getResponse().getContentAsString();
        int userId = Integer.parseInt(body.replaceAll(".*\"username\":\"test-admin\".*\"id\":(\\d+).*", "$1").replaceAll(".*\"id\":(\\d+).*test-admin.*", "$1"));
        return new AuthSession(userId, loginBootstrappedAdminToken());
    }

    private String loginBootstrappedAdminToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test-admin\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        String loginBody = loginResult.getResponse().getContentAsString();
        return loginBody.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    private record AuthSession(int userId, String token) {
        private String bearer() {
            return "Bearer " + token;
        }
    }
}
