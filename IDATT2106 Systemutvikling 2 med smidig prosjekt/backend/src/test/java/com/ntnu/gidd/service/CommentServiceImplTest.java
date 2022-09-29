package com.ntnu.gidd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


import com.ntnu.gidd.config.ModelMapperConfig;
import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.CommentRepository;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.activity.ActivityService;
import com.ntnu.gidd.service.Comment.CommentService;
import com.ntnu.gidd.service.Comment.CommentServiceImpl;
import com.ntnu.gidd.util.ContextAwareModelMapper;
import com.ntnu.gidd.utils.JpaUtils;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Import( {ModelMapperConfig.class})
@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

  @InjectMocks
  private CommentServiceImpl commentService;

  @Mock
  private ActivityRepository activityRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ActivityService activityService;

  @Mock
  private PostRepository postRepository;



  ModelMapper modelMapper = new ContextAwareModelMapper();

  private Activity activity;
  private User user;
  private Comment comment;
  private Comment comment2;
  private List<Comment> commentsExpected;
  private Pageable pageable;

  @BeforeEach
  public void setUp() throws Exception {
    commentService = new CommentServiceImpl(commentRepository, userRepository, activityRepository, modelMapper, postRepository);
    activity = new ActivityFactory().getObject();
    assert activity != null;

    user = new UserFactory().getObject();
    assert user != null;

    pageable = JpaUtils.getDefaultPageable();

    activity.setCreator(user);
    comment = new Comment();
    comment2 = new Comment();
    comment.setUser(user);
    comment2.setUser(user);
    commentsExpected = List.of(comment, comment2);
    activity.setComments(commentsExpected);
    lenient().when(activityRepository.save(activity)).thenReturn(activity);
    lenient().when(userRepository.save(user)).thenReturn(user);
    lenient().when(commentRepository.save(comment)).thenReturn(comment);

  }

  @Test
  void testCommentServiceImplGetCommentByIdReturnsComment() {
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    CommentDto commentFound = commentService.getCommentById(comment.getId());

    assertThat(commentFound.getId()).isEqualTo(comment.getId());

  }

  @Test
  void testCommentControllerImplGetAllCommentsAndReturnsListOfComments() {
    Page<Comment> comments = new PageImpl<>(commentsExpected, pageable, commentsExpected.size());

    when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));
    Page<CommentDto> getComments = commentService.getCommentsOnActivity(pageable, activity.getId());

    for (int i = 0; i < comments.getContent().size(); i++) {
      assertThat(comments.getContent().get(i).getComment()).isEqualTo(getComments.getContent().get(i).getComment());
    }


    }

  @Test
  void testCommentControllerImplUpdateCommentAndReturnUpdatedComment() {
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
    comment.setComment("Lets update the comment!");

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
    when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));

    CommentDto updateComment = commentService.updateComment(comment.getId(),
        modelMapper.map(comment, CommentDto.class), user.getEmail(), activity.getId());

    assertThat(comment.getId()).isEqualTo(updateComment.getId());
    assertThat(comment.getCreatedAt()).isEqualTo(updateComment.getCreatedAt());


  }

  @Test
  void testCommentServiceImplDeleteCommentAndReturn200ok() {
    commentsExpected = List.of(comment2);
    activity.setComments(commentsExpected);

    lenient().when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
    doNothing().when(commentRepository).deleteById(comment.getId());

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
    when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));

    commentService.deleteComment(comment.getId(), user.getEmail(), activity.getId());

    when(commentRepository.findAll()).thenReturn(commentsExpected);
    List<Comment> commentsFound = commentRepository.findAll();

    Assertions.assertIterableEquals(commentsExpected, commentsFound);


  }

  @Test
  void testCommentServiceImplDeleteAllCommentsOnActivityAndReturn200ok() {
    commentsExpected = List.of();
    activity.setComments(commentsExpected);

    lenient().when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
    when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));

    commentService.deleteAllCommentsOnActivity(activity.getId(), user.getEmail());

    when(commentRepository.findAll()).thenReturn(commentsExpected);
    List<Comment> commentsFound = commentRepository.findAll();

    Assertions.assertIterableEquals(commentsExpected, commentsFound);

  }


}
