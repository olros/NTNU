package com.ntnu.gidd.controller.post;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.dto.post.PostCreateDto;
import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.PostFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import java.util.Objects;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {
    private final String URI = "/posts/";


    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();


    @Autowired
    private MockMvc mvc;


    private PostFactory postFactory = new PostFactory();
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    ModelMapper modelMapper;

    @BeforeEach
    public void setup() throws Exception {
        post = postFactory.getObject();
        assert post != null;
        activityRepository.save(post.getActivity());
        userRepository.save(post.getCreator());
        post = postRepository.save(post);

    }
    @AfterEach
    public void cleanup(){
        postRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @WithMockUser(value = "spring")
    @Test
    public void testPostControllerGetAllReturnsPaginatedList() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        this.mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].content",hasItem(post.getContent())))
                .andExpect(jsonPath("$.content.[*].id",hasItem(post.getId().toString())));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testPostControllerGetReturnsPost() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        this.mvc.perform(get(URI + post.getId() + "/").accept(MediaType.APPLICATION_JSON)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.id").value(post.getId().toString()));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testPostControllerCreateSavesPostAndReturnsNewPost() throws Exception {
        Activity testActivity = activityRepository.save(Objects.requireNonNull(activityFactory.getObject()));
        PostCreateDto newPost = PostCreateDto.builder().activityId(testActivity.getId())
                .content(getRandomString(22))
                .image(getRandomString(11))
                .build();
        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        this.mvc.perform(post(URI).with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(newPost.getContent()))
                .andExpect(jsonPath("$.image").value(newPost.getImage()));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testPostControllerUpdateChnagesPostAndReturnsUpdatedPost() throws Exception {
        String newContent = getRandomString(22);
        PostDto updatePost = PostDto.builder().content(newContent).build();
        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        this.mvc.perform(put(URI + post.getId() + "/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testPostControllerDeleteReturnsResponse() throws Exception {
        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        this.mvc.perform(delete(URI + post.getId() + "/")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
