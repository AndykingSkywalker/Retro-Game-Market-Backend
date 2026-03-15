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

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerProfilePictureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateUser_emptyProfilePicture_clearsValue() throws Exception {
        RegisteredUser user = registerAndLogin("https://cdn.example.com/original.png");

        mockMvc.perform(put("/api/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profilePicture\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profilePicture").value(nullValue()));
    }

    @Test
    void updateUser_rejectsNonHttpProfilePictureUrl() throws Exception {
        RegisteredUser user = registerAndLogin("https://cdn.example.com/original.png");

        mockMvc.perform(put("/api/users/" + user.id())
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profilePicture\":\"ftp://cdn.example.com/pic.png\"}"))
                .andExpect(status().isBadRequest());
    }

    private RegisteredUser registerAndLogin(String profilePicture) throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        String username = "user-" + suffix;
        String email = "user-" + suffix + "@example.com";
        String password = "secret123";

        String createJson = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "email", email,
                "password", password,
                "profilePicture", profilePicture
        ));

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        int id = created.get("id").asInt();

        String loginJson = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode login = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return new RegisteredUser(id, login.get("token").asText());
    }

    private record RegisteredUser(int id, String token) {
    }
}


