package com.ntnu.gidd.controller.Registration;


import com.ntnu.gidd.Application;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.model.RegistrationId;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.RegistrationRepository;
import com.ntnu.gidd.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
public class UserRegistrationsListControllerTest {

    private String getURI(User user) {
        return "/users/" + user.getId().toString() +"/registrations/" ;
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    ActivityRepository activityRepository;


    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();
    private RegistrationId registrationId = new RegistrationId();
    private Registration registration = new Registration();
    private Activity activity;
    private User user;



    @BeforeEach
    public void setup() throws Exception {
        activity = activityFactory.getObject();
        assert activity != null;
        activity = activityRepository.save(activity);

        user = userFactory.getObject();
        assert user != null;
        user = userRepository.save(user);

        registration.setUser(user);
        registration.setActivity(activity);

        registrationId.setActivityId(activity.getId());
        registrationId.setUserId(user.getId());
        registration.setRegistrationId(registrationId);
        registration = registrationRepository.save(registration);
    }

    @AfterEach
    public void cleanup() {
        registrationRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(value = "spring")
    public void testUserRegistrationListControlleretsAllActivitiesForUser() throws Exception {
        this.mvc.perform(get(getURI(user))
                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
    }
}
