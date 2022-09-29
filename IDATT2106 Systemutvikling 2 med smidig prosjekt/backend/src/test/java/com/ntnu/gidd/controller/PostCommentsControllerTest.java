package com.ntnu.gidd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.PostFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.CommentRepository;
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


import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostCommentsControllerTest {

    private String getURI(Post post) {
        return "/posts/" + post.getId().toString() + "/comments/";
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;

    private ModelMapper modelMapper = new ModelMapper();

    Comment comment = new Comment();
    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();
    private PostFactory postFactory = new PostFactory();

    @BeforeEach
    public void setup() throws Exception {

        post = postFactory.getObject();
        userRepository.save(post.getCreator());
        activityRepository.save(post.getActivity());
        comment = commentRepository.save(Comment.builder().id(UUID.randomUUID()).comment("hello").build());
        List<Comment> commentList = List.of(comment);
        post.setComments(commentList);
        post = postRepository.save(post);



    }
    @AfterEach
    public void cleanUp(){
        postRepository.deleteAll();
        activityRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();

    }


    @WithMockUser(value = "spring")
    @Test
    public void testCommentControllerGetCommentByIdandReturn200Ok() throws Exception {
        this.mvc.perform(get(getURI(post) + comment.getId() + "/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(comment.getId().toString()));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testCommentControllerGetAllCommentsOnActivityAndReutrnsListOfComments() throws Exception {
        this.mvc.perform(get(getURI(post)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].id").value(comment.getId().toString()));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testCommentControllerSaveCommentAndReturnCreated() throws Exception {

        User testUser = userFactory.getObject();
        testUser = userRepository.save(testUser);
        Post testPost = postFactory.getObject();
        userRepository.save(testPost.getCreator());
        activityRepository.save(testPost.getActivity());
        testPost = postRepository.save(testPost);
        Comment testComment = new Comment();
        testComment.setId((null));
        testComment.setComment("This is a test");

        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();


        this.mvc.perform(post(getURI(testPost))
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value(testComment.getComment()));

    }

    @WithMockUser(value = "spring")
    @Test
    public void testCommentControllerUpdateCommentAndReturnUodatedComment() throws Exception {

        this.comment.setComment("This is a new comment!");

        UserDetails userDetails = UserDetailsImpl.builder().email(post.getCreator().getEmail()).build();
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        this.mvc.perform(put(getURI(post) + comment.getId() + "/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId().toString()))
                .andExpect(jsonPath("$.comment").value(comment.getComment()));

    }

    @WithMockUser(value = "spring")
    @Test
    public void testCommentControllerDeleteCommentByIdAndReturn200ok() throws Exception {


        User testUser = userFactory.getObject();
        testUser = userRepository.save(testUser);
        Post testPost = postFactory.getObject();
        userRepository.save(testPost.getCreator());
        activityRepository.save(testPost.getActivity());
        Comment testComment = new Comment();
        testComment.setId((UUID.randomUUID()));
        testComment.setComment("This is a test");
        testComment.setUser(testUser);
        testComment = commentRepository.save(testComment);
        List<Comment> commentList = List.of(testComment);
        testPost.setComments(commentList);
        testPost = postRepository.save(testPost);


        UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();


        this.mvc.perform(delete(getURI(testPost) +testComment.getId() + "/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}
