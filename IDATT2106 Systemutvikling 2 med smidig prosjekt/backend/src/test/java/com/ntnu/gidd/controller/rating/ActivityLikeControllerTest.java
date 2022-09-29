package com.ntnu.gidd.controller.rating;


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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivityLikeControllerTest {

    public String getURI(Activity activity){
        return "/activities/" + activity.getId().toString() +"/likes/";
    }

    private Activity activity;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();


    @BeforeEach
    public void setup() throws Exception {
        activity = activityRepository.save(Objects.requireNonNull(activityFactory.getObject()));

    }
    @AfterEach
    public void cleanUp(){
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testActivityLikeControllerCheckHasLikedReturnTrueForWhenUserHasLiked() throws Exception {
        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);
        List<User> likes = activity.getLikes();
        likes.add(user);
        activity.setLikes(likes);
        activityRepository.save(activity);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(get(getURI(activity)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(true));
    }


    @Test
    public void testActivityLikeControllerCheckHasLikedReturnFalseForWhenUserHasNotLiked() throws Exception {
        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(get(getURI(activity)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(false));
    }

    @Test
    public void testActivityLikeControllerCheckAddLikeReturnTrueWhenUserLikes() throws Exception {
        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(post(getURI(activity)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(true));
    }

    @Test
    public void testActivityLikeControllerCheckRemoveLikeReturnFalseWhenUserUnLikes() throws Exception {
        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);
        List<User> likes = activity.getLikes();
        likes.add(user);
        activity.setLikes(likes);
        activityRepository.save(activity);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        this.mvc.perform(delete(getURI(activity)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(false));
    }
}
