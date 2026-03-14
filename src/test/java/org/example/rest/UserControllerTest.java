package org.example.rest;

import org.example.domain.User;
import org.example.domain.UserRole;
import org.example.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Test
    void createUser_requiresValidEmail() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\",\"email\":\"not-an-email\",\"password\":\"pw\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_defaultsToCustomerRole() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"role-test\",\"email\":\"role-test@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_returnsJwtToken_forValidCredentials() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\",\"email\":\"andrew@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_rejectsInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"samus\",\"email\":\"samus@example.com\",\"password\":\"power-suit\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"samus\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("invalid username or password"));
    }

    @Test
    void login_withLegacyPlaintextPasswordRow_returnsUnauthorizedNotServerError() throws Exception {
        User legacy = new User();
        legacy.setUsername("legacy-user");
        legacy.setEmail("legacy-user@example.com");
        legacy.setPassword("plaintext-password");
        legacy.setRole(UserRole.CUSTOMER);
        userRepo.save(legacy);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"legacy-user\",\"password\":\"plaintext-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("invalid username or password"));
    }
}
