package com.ntnu.gidd.integration;

import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserSearchIntegrationTest {

    private String URI = "/users/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User userJohn;

    private User userTom;

    private List<User> users;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();

        userJohn = User.builder()
                .id(UUID.randomUUID())
                .email("john@doe.com")
                .firstName("John")
                .surname("Doe")
                .build();

        userTom = User.builder()
                .id(UUID.randomUUID())
                .email("tom@doe.com")
                .firstName("Tom")
                .surname("Doe")
                .build();

        users = List.of(userJohn, userTom);

        userRepository.saveAll(users);
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testSearchByFirstNameReturnsCorrectResults() throws Exception {
        mockMvc.perform(get(URI)
                                .param("search", userJohn.getFirstName())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(userJohn.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchBySurnameReturnsCorrectResults() throws Exception {
        mockMvc.perform(get(URI)
                                .param("search", userJohn.getSurname())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(users.size()))
                .andExpect(jsonPath("$.content.[*].id").value(containsInAnyOrder(userJohn.getId().toString(), userTom.getId().toString())));
    }

    @Test
    @WithMockUser
    public void testSearchByPartialFirstNameReturnsCorrectResults() throws Exception {
        String partialUserTomFirstName = userJohn.getFirstName().substring(0, 3);

        mockMvc.perform(get(URI)
                                .param("search", partialUserTomFirstName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(userJohn.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchByPartialSurnameReturnsCorrectResults() throws Exception {
        String partialUsersSurname = userJohn.getSurname().substring(0, 2);

        mockMvc.perform(get(URI)
                                .param("search", partialUsersSurname)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(users.size()))
                .andExpect(jsonPath("$.content.[*].id").value(containsInAnyOrder(userJohn.getId().toString(), userTom.getId().toString())));
    }

    @Test
    @WithMockUser
    public void testSearchByWrongFirstAndSurnameReturnsNoResults() throws Exception {
        String randomString = getRandomString(5);

        mockMvc.perform(get(URI)
                                .param("search", randomString)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser
    public void testSearchByFullNameReturnsCorrectResults() throws Exception {
        String fullNameJohn = userJohn.getSurname() + " " + userJohn.getSurname();

        mockMvc.perform(get(URI)
                                .param("search", fullNameJohn)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(users.size()))
                .andExpect(jsonPath("$.content.[*].id").value(containsInAnyOrder(userJohn.getId().toString(), userTom.getId().toString())));
    }
}
