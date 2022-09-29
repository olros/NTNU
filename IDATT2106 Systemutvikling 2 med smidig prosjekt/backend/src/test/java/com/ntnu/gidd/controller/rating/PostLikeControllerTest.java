package com.ntnu.gidd.controller.rating;

import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.PostFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.PostRepository;
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

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostLikeControllerTest {

    public String getURI(Post post){
        return "/posts/" + post.getId().toString() +"/likes/";
    }

    private Post post;
    private User user;
    private Activity activity;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();
    private PostFactory postFactory = new PostFactory();


    @BeforeEach
    public void setup() throws Exception {
        post = postFactory.getObject();
        activity = activityRepository.save(post.getActivity());
        user = userRepository.save(post.getCreator());
        post.setActivity(activity);
        post.setCreator(user);
        post = postRepository.save(post);

    }
    @AfterEach
    public void cleanUp(){
        postRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testPostLikeControllerCheckHasLikedReturnTrueForWhenUserHasLiked() throws Exception {
        User testUser = userFactory.getObject();
        assert testUser != null;
        testUser= userRepository.save(testUser);
        List<User> likes = post.getLikes();
        likes.add(testUser);
        post.setLikes(likes);
        postRepository.save(post);
        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();
        this.mvc.perform(get(getURI(post)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(true));
    }


    @Test
    public void testPostLikeControllerCheckHasLikedReturnFalseForWhenUserHasNotLiked() throws Exception {
        User testUser = userFactory.getObject();
        assert testUser != null;
        testUser= userRepository.save(testUser);
        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();
        this.mvc.perform(get(getURI(post)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(false));
    }

    @Test
    public void testPostLikeControllerCheckAddLikeReturnTrueWhenUserLikes() throws Exception {
        User testUser = userFactory.getObject();
        assert testUser != null;
        testUser= userRepository.save(testUser);
        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();
        this.mvc.perform(post(getURI(post)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(true));
    }

    @Test
    public void testPostLikeControllerCheckRemoveLikeReturnFalseWhenUserUnLikes() throws Exception {
        User testUser = userFactory.getObject();
        assert testUser != null;
        testUser= userRepository.save(testUser);
        List<User> likes = post.getLikes();
        likes.add(testUser);
        post.setLikes(likes);
        postRepository.save(post);
        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();
        this.mvc.perform(delete(getURI(post)).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(false));
    }
}
