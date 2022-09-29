package com.ntnu.gidd.controller.Registration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ntnu.gidd.Application;
import com.ntnu.gidd.config.ModelMapperConfig;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.RegistrationFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.model.RegistrationId;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.RegistrationRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import javax.transaction.Transactional;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Application.class})
public class UserRegistrationControllerTest {

  private String URI = "/users/me";

  @Autowired
  private MockMvc mvc;

  @Autowired
  RegistrationRepository registrationRepository;

  @Autowired
  ActivityRepository activityRepository;

  @Autowired
  UserRepository userRepository;


  private ActivityFactory activityFactory = new ActivityFactory();
  private UserFactory userFactory = new UserFactory();
  private RegistrationId registrationId = new RegistrationId();
  private Registration registration = new Registration();
  private Activity activity;
  private User user;
  private UserDetailsImpl userDetails;


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

    userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
  }
  @AfterEach
  public void cleanup() {
    registrationRepository.deleteAll();
    activityRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void testUserRegistrationControllerGetRegistrationsForUser() throws Exception {
    this.mvc.perform(get(URI + "/registrations/")
          .with(user(userDetails))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
  }

  @Test
  public void testUserRegistrationControllerGetRegistrationWithCompositeIdUser() throws Exception {
    this.mvc.perform(get(URI +  "/registrations/" + activity.getId() + "/")
        .with(user(userDetails))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(activity.getTitle()));
  }

  @Transactional
  @Test
  public void testRegistrationControllerDeleteRegistration() throws Exception {
    Registration testRegistration = new RegistrationFactory().getObject();
    userRepository.save(testRegistration.getUser());
    activityRepository.save(testRegistration.getActivity());
    registrationRepository.save(testRegistration);
    UserDetailsImpl testUserDetails = UserDetailsImpl.builder().email(testRegistration.getUser().getEmail()).build();


    this.mvc.perform(delete(URI  +  "/registrations/" + testRegistration.getActivity().getId() + "/")
        .with(user(testUserDetails))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Registration has been deleted"));

  }


}
