package com.ntnu.gidd.controller.Host;

import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserHostControllerTest {


    private String URI = "/users/me/hosts/";
    private String getURI(User user) {
        return "/user/" + user.getId().toString() + "/hosts/";
    }
    @Autowired
    private MockMvc mvc;

    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        user = userFactory.getObject();
        assert user != null;
        user.setActivities(List.of(Objects.requireNonNull(activityFactory.getObject()), activityFactory.getObject()));
        activityRepository.saveAll(user.getActivities());

        user = userRepository.save(user);

    }
    @AfterEach
    public void cleanUp(){
        userRepository.deleteAll();
        activityRepository.deleteAll();
    }

    @WithMockUser(value = "spring")
    @Test
    public void testUserHostControllerGetAllReturnsListOActivities() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON).with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(user.getActivities().size()))
                .andExpect(jsonPath("$.content.[*].title", hasItem(user.getActivities().get(0).getTitle())))
                .andExpect(jsonPath("$.content.[*].title", hasItem(user.getActivities().get(1).getTitle())));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testUserHostGetReturnsTheWantedActivity() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(get(URI+user.getActivities().get(0).getId().toString()+"/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(user.getActivities().get(0).getTitle()));

    }

    @WithMockUser(value = "spring")
    @Test
    public void testUserHostControllerDeletesHostAndReturnsUpdatedList() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        ArrayList<Activity> list = new ArrayList<>(user.getActivities());
        Activity deleteActivity = activityFactory.getObject();
        assert deleteActivity != null;
        list.add(deleteActivity);
        user.setActivities(list);
        activityRepository.save(deleteActivity);
        userRepository.save(user);
        assert user.getActivities().size() == list.size();
        this.mvc.perform(delete(URI+deleteActivity.getId().toString()+"/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(list.size()-1));

    }

    @WithMockUser(value = "spring")
    @Test
    public void testFilterActivitiesOnWantedFieldsForTitle() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        String expectedActivity = user.getActivities()
                .get(0)
                .getTitle();

        this.mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("title", expectedActivity)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(expectedActivity));
    }

    @Test
    @WithMockUser
    public void testFilterActivitiesPartialTitleReturnsCorrectResults() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        String TITLE = "Long testing string that does not mean anything";
        Activity expectedActivity = user.getActivities()
                .get(0);
        expectedActivity.setTitle(TITLE);
        activityRepository.save(expectedActivity);
        user = userRepository.save(user);


        mvc.perform(get(URI)
                .accept(MediaType.APPLICATION_JSON)
                .param("title", TITLE.substring(0, TITLE.length() - 1))
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(expectedActivity
                                                                         .getTitle()));
    }


    @Test
    @WithMockUser
    public void testFilterActivitiesByStartDateBetweenReturnActivitiesStartingInRange() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        Activity priorActivity = user.getActivities()
                .get(1);
        priorActivity
                .setStartDate(ZonedDateTime.now().minusDays(100));
        activityRepository.save(priorActivity);
        user = userRepository.save(user);

        // Due to decimal precision these where sometimes rounded down/up
        // causing the test to fail spontaneously
        Activity latterActivity = user.getActivities()
                .get(0);

        mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("startDateAfter", String.valueOf(latterActivity.getStartDate().minusHours(1)))
                .param("startDateBefore", String.valueOf(latterActivity.getStartDate().plusHours(1)))
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(latterActivity
                                                                         .getTitle()));
    }
}
