package com.ntnu.gidd.controller.Host;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.dto.User.UserEmailDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivityHostControllerTest {


    private String getURI(Activity activity) {
        return "/activities/" + activity.getId() + "/hosts/";
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

    @BeforeEach
    public void setUp() throws Exception {
        activity = activityFactory.getObject();
        assert activity != null;
        activity.setHosts(List.of(Objects.requireNonNull(userFactory.getObject())));
        userRepository.saveAll(activity.getHosts());
        activity = activityRepository.save(activity);
    }

    @AfterEach
    public void cleanUp(){
        activityRepository.deleteAll();
        userRepository.deleteAll();

    }

    @WithMockUser(value = "spring")
    @Test
    public void testActivityHostControllerGetAllHostsReturnsListOfHosts() throws Exception {
        this.mvc.perform(get(getURI(activity)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].firstName").value(activity.getHosts().get(0).getFirstName()));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testActivityHostControllerAddHostReturnsNewListOfHosts() throws Exception {
        User user = userFactory.getObject();
        assert user != null;
        userRepository.save(user);
        UserEmailDto userDto = UserEmailDto.builder().email(user.getEmail()).build();
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        activity.setCreator(user);
        activityRepository.save(activity);
        this.mvc.perform(post(getURI(activity))
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[1].email").value(user.getEmail()));

    }


    @WithMockUser(value = "spring")
    @Test
    public void testActivityHostControllerDeletesHostAndReturnsUpdatedList() throws Exception {
        ArrayList<User> list = new ArrayList<>(activity.getHosts());
        User deleteUser = userFactory.getObject();
        assert deleteUser != null;
        list.add(deleteUser);
        activity.setHosts(list);
        activity.setCreator(deleteUser);
        userRepository.save(deleteUser);
        activityRepository.save(activity);
        UserDetails userDetails = UserDetailsImpl.builder().email(deleteUser.getEmail()).build();
        this.mvc.perform(delete(getURI(activity)+activity.getHosts().get(0).getId().toString()+"/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].email").value(deleteUser.getEmail()));

    }


}
