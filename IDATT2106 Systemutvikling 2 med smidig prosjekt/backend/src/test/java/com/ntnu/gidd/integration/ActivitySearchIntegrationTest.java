package com.ntnu.gidd.integration;


import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.repository.ActivityRepository;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivitySearchIntegrationTest {

    private final String URI = "/activities/";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ActivityRepository activityRepository;

    private ActivityFactory activityFactory = new ActivityFactory();

    private Activity tennis;

    private Activity football;


    @BeforeEach
    public void setUp() throws Exception {
        tennis = activityFactory.getObject();
        tennis.setTitle("Tennis");
        tennis.setDescription("Play tennis with stopp");

        football = activityFactory.getObject();
        football.setTitle("Football");
        football.setDescription("Join me for some football in the valley of death");

        activityRepository.saveAll(List.of(tennis, football));
    }

    @AfterEach
    public void cleanUp(){
        activityRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testSearchByTitleReturnsCorrectResults() throws Exception {
        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", tennis.getTitle()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(tennis.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchByDescriptionReturnsCorrectResults() throws Exception {
        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", tennis.getDescription()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(tennis.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchByPartialTitleReturnsCorrectResults() throws Exception {
        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", tennis.getTitle().substring(0, 4)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(tennis.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchByPartialDescriptionReturnsCorrectResults() throws Exception {
        String partialTennisDescription = tennis.getDescription().substring(0, 11);

        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", partialTennisDescription))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(tennis.getId().toString()));
    }

    @Test
    @WithMockUser
    public void testSearchByWrongTitleReturnsNoResults() throws Exception {
        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", "Polo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @WithMockUser
    public void testSearchByWrongDescriptionReturnsNoResults() throws Exception {
        String randomString = getRandomString(50);

        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("search", randomString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

}
