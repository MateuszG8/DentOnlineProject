package com.example.dentonline.controllers;


import com.example.dentonline.DentOnlineApplication;
import com.example.dentonline.dto.SignupDTO;
import com.example.dentonline.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.Socket;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Testcontainers
@SpringBootTest(classes = DentOnlineApplication.class)
public class AuthControllerTest {

    @Autowired

    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

   @Autowired
   private ObjectMapper objectMapper;


    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.3"));

    @BeforeAll
    static void initAll() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }
    @AfterAll
    static void tearDownAll() {
        mongoDBContainer.stop();
    }

    @Test
    void containerStartsAndPublicPortIsAvailable() {
        try (Socket socket = new Socket(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort())) {
            Assertions.assertTrue(socket.isConnected());
        } catch (IOException e) {
            Assertions.fail("Expected port is not available!");
        }
    }
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setUsername("testuser");
        signupDTO.setPassword("testpassword");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isOk());
    }

}
