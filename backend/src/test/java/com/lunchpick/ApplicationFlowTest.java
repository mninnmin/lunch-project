package com.lunchpick;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationFlowTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void userCanSignupReceiveRecommendationAndBookmarkIt() throws Exception {
        String signupBody = """
                {"nickname":"점심친구","email":"friend@example.com","password":"safe-password-123"}
                """;
        String signup = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupBody))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user.nickname").value("점심친구"))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(signup).get("token").asText();

        String recommendation = mockMvc.perform(post("/api/recommendations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mood\":\"LIGHT\",\"category\":\"JAPANESE\",\"price\":\"ALL\",\"spice\":\"NONE\",\"party\":\"SOLO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.menu.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        JsonNode menu = objectMapper.readTree(recommendation).get("menu");

        mockMvc.perform(post("/api/favorites/" + menu.get("id").asLong()).header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        String favorites = mockMvc.perform(get("/api/favorites").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(favorites).get(0).get("id").asLong()).isEqualTo(menu.get("id").asLong());
    }
}
