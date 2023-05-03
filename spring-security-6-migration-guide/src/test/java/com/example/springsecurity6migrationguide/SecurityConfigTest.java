package com.example.springsecurity6migrationguide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpointAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/public"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testSecureEndpointAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testSecureEndpointAccessWithValidCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/employees")
                        .header("Authorization", "Basic YWRtaW46MTIzNA==")) // Base64 encoded "admin:1234"
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
