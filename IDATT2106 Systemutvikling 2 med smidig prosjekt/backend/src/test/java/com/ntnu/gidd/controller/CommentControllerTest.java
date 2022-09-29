package com.ntnu.gidd.controller;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.CommentRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.service.Comment.CommentService;
import java.util.List;
import javax.transaction.Transactional;
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
import org.springframework.web.servlet.view.document.AbstractXlsView;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerTest {

  private String getURI(Activity activity) {
    return "/activities/" + activity.getId() + "/comments/";
  }

  @Autowired
  private MockMvc mvc;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Activity activity;

  private User user;

  private ModelMapper modelMapper = new ModelMapper();

  Comment comment = new Comment();
  private ActivityFactory activityFactory = new ActivityFactory();
  private UserFactory userFactory = new UserFactory();

  @BeforeEach
  public void setup() throws Exception {

    activity = activityFactory.getObject();
    assert activity != null;

    user = userFactory.getObject();
    assert user != null;
    user = userRepository.save(user);

    activity.setCreator(user);

    comment.setComment("Hey, look at this comment!");
    comment.setUser(user);
    activity.setComments(List.of(comment));

    activity = activityRepository.save(activity);

    comment.setActivity(activity);
    comment = commentRepository.save(comment);




  }
  @AfterEach
  public void cleanUp() {
    commentRepository.deleteAll();
    activityRepository.deleteAll();
    userRepository.deleteAll();

  }


  @WithMockUser(value = "spring")
  @Test
  public void testCommentControllerGetCommentByIdandReturn200Ok() throws Exception {
    this.mvc.perform(get(getURI(activity) + comment.getId() + "/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(comment.getId().toString()));
  }


  @WithMockUser(value = "spring")
  @Test
  public void testCommentControllerGetAllCommentsOnActivityAndReutrnsListOfComments() throws Exception {
    this.mvc.perform(get(getURI(activity)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[0].id").value(comment.getId().toString()));
  }


  @WithMockUser(value = "spring")
  @Test
  public void testCommentControllerSaveCommentAndReturnCreated() throws Exception {

    User testUser = userFactory.getObject();
    testUser = userRepository.save(testUser);
    Activity testActivity = activityFactory.getObject();
    testActivity = activityRepository.save(testActivity);
    Comment testComment = new Comment();
    testComment.setId((null));
    testComment.setComment("This is a test");
    testComment.setUser(testUser);

    UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();


    this.mvc.perform(post(getURI(testActivity))
        .with(user(userDetails))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testComment)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.comment").value(testComment.getComment().toString()));

  }

  @WithMockUser(value = "spring")
  @Test
  public void testCommentControllerUpdateCommentAndReturnUodatedComment() throws Exception {

    this.comment.setComment("This is a new comment!");

    UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
    CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
    this.mvc.perform(put(getURI(activity) + comment.getId() + "/")
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
    Activity testActivity = activityFactory.getObject();
    testUser = userRepository.save(testUser);
    testActivity.setCreator(testUser);
    testActivity = activityRepository.save(testActivity);

    Comment testComment = new Comment();
    testComment.setComment("This is a test");
    testComment.setUser(testUser);
    testComment = commentRepository.save(testComment);

    UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();


    this.mvc.perform(delete(getURI(testActivity) +testComment.getId() + "/")
        .with(user(userDetails))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(value = "spring")
  @Test
  public void testCommentControllerDeleteAllCommentsOnActivityAndReturn200ok() throws Exception {

    User testUser = userFactory.getObject();
    Activity testActivity = activityFactory.getObject();
    testUser = userRepository.save(testUser);
    testActivity.setCreator(testUser);

    Comment testComment = new Comment();
    testComment.setComment("This is a test");
    testComment.setUser(testUser);
    testComment = commentRepository.save(testComment);
    testActivity.setComments(List.of(testComment));
    testActivity = activityRepository.save(testActivity);


    UserDetails userDetails = UserDetailsImpl.builder().email(testUser.getEmail()).build();


    this.mvc.perform(delete(getURI(testActivity))
        .with(user(userDetails))
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

}
