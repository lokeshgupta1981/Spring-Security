package com.howtodoinjava.oauth2.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest
public class AppTests {
    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void contextLoads() {
    }
}
