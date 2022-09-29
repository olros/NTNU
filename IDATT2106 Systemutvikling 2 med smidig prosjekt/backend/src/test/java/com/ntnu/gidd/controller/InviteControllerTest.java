package com.ntnu.gidd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import static org.hamcrest.Matchers.hasItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InviteControllerTest {

    private String getURI(Activity activity){
        return "/activities/" + activity.getId().toString() + "/invites/";
    }

    @Autowired
    private MockMvc mvc;

    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;

    private Activity activity;

    private User user;

    @BeforeEach
    public void setUp() throws Exception {

        user = userFactory.getObject();
        assert user != null;
        user = userRepository.save(user);
        activity = activityFactory.getObject();
        assert activity != null;
        activity.setInvites(List.of(user));
        activity = activityRepository.save(activity);
    }

    @AfterEach
    public void cleanUp(){
        userRepository.deleteAll();
        activityRepository.deleteAll();
    }

    @WithMockUser(value = "spring")
    @Test
    public void testInviteControllerGetAllRetrunsListOfAllInvitesForAActivity() throws Exception {
        this.mvc.perform(get(getURI(activity))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].firstName").value(user.getFirstName()));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testInvitesContollerDeleteInviteReturnsUpdatedListOfInvites() throws Exception {
        User testUser = userRepository.save(Objects.requireNonNull(userFactory.getObject()));
        activity.setInvites(List.of(user, testUser));
        activity = activityRepository.save(activity);

        this.mvc.perform(delete(getURI(activity)+ testUser.getId().toString()+ "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].firstName").value(user.getFirstName()));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testActivityControllerSaveReturn201ok() throws Exception {

        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);

        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(post(getURI(activity)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].firstName",hasItem(user.getFirstName())));

    }
}
