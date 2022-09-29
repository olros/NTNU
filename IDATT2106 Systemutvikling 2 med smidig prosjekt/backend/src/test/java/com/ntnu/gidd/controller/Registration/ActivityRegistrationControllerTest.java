package com.ntnu.gidd.controller.Registration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.transaction.Transactional;

import com.ntnu.gidd.security.UserDetailsImpl;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
public class ActivityRegistrationControllerTest {

  private String URI = "/activities/";

  @Autowired
  private MockMvc mvc;

  @Autowired
  RegistrationRepository registrationRepository;

  @Autowired
  ActivityRepository activityRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

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

  @WithMockUser(value = "spring")
  @Test
  public void testActivityRegistrationControllerGetRegistrationForActivity() throws Exception {
    this.mvc.perform(get(URI + activity.getId() + "/registrations/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].user.email").value(user.getEmail()));
  }


  @WithMockUser(value = "spring")
  @Test
  public void testActivityRegistrationControllerGetRegistrationWithCompositeIdActivity() throws Exception {
    UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
    this.mvc.perform(get(URI + activity.getId() + "/registrations/" + user.getId() + "/")
        .accept(MediaType.APPLICATION_JSON)
        .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user.email").value(user.getEmail()));
  }


  @WithMockUser(value = "spring")
  @Test
  public void testActivityRegistrationControllerSaveReturn201OCreated() throws Exception {

    User testUser = userFactory.getObject();
    Activity testActivity = activityFactory.getObject();


    assert testActivity != null;
    testActivity = activityRepository.save(testActivity);
    assert testUser != null;
    testUser = userRepository.save(testUser);


    this.mvc.perform(post(URI + "/" + testActivity.getId() + "/registrations/")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testActivity.getId()))
        .content(objectMapper.writeValueAsString(testUser)))
        .andExpect(status().isCreated());
  }

  @Transactional
  @WithMockUser(value = "spring")
  @Test
  public void testRegistrationControllerDeleteRegistration() throws Exception {

    Registration testRegistration = new RegistrationFactory().getObject();
    assert testRegistration != null;
    userRepository.save(registration.getUser());
    activityRepository.save(registration.getActivity());
    userRepository.save(testRegistration.getUser());
    activityRepository.save(testRegistration.getActivity());
    registrationRepository.save(testRegistration);
    UserDetails userDetails = UserDetailsImpl.builder().email(testRegistration.getUser().getEmail()).build();

    this.mvc.perform(delete(URI + testRegistration.getActivity().getId() + "/registrations/" + testRegistration.getUser().getId() + "/")
        .with(user(userDetails))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }



}
