package com.example.dentonline;

import com.example.dentonline.DentOnlineApplication;
import com.example.dentonline.models.User;
import com.example.dentonline.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DataMongoTest
//@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {DentOnlineApplication.class})
//@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser")
    void whenGetUser_thenReturnUserDTO() throws Exception {
        // Przygotowanie
        String userId = "someUserId";
        User testUser = new User();
        testUser.setId(userId);
        testUser.setFirstName("Test");
        testUser.setPassword("Test");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Wykonanie i Sprawdzenie
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()));
    }

    @Test
    @WithMockUser(username = "testUser")
    void whenGetUser_withInvalidId_thenThrowException() throws Exception {
        // Przygotowanie
        String invalidUserId = "invalidUserId";

        when(userRepository.findById(invalidUserId)).thenThrow(new UsernameNotFoundException("User not found"));

        // Wykonanie i Sprawdzenie
        mockMvc.perform(get("/api/users/" + invalidUserId))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(invalidUserId);
    }
}


