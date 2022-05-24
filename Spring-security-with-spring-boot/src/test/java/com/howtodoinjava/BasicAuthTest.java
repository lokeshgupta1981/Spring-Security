package com.howtodoinjava;

import com.howtodoinjava.demo.security.basicauth.AppBasicAuthenticationEntryPoint;
import com.howtodoinjava.demo.security.basicauth.BasicAuthWebSecurityConfiguration;
import com.howtodoinjava.demo.web.AppController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = {
      BasicAuthWebSecurityConfiguration.class,
      AppBasicAuthenticationEntryPoint.class,
      AppController.class
    }
)
@AutoConfigureMockMvc
class BasicAuthTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void expectOKResponse_WhenAccessNotSecuredURL() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/public"))
          .andExpect(status().isOk());
    }

    @Test
    void expectUnauthorizedUser_WhenPasswordIsWrong() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/")
                .with(httpBasic("user","wrong-password")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void expectOKResponse_WhenPasswordIsCorrect() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/")
            .with(httpBasic("user", "password")))
          .andExpect(content().string("Hello World !!"));
    }
}
