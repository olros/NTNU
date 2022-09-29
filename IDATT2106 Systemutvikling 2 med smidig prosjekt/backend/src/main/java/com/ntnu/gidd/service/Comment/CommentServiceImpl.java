package com.ntnu.gidd.service.Comment;

import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.exception.*;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.CommentRepository;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.lang.StrictMath.toIntExact;

/**
 * Implementation  of a comment service
 */
@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class CommentServiceImpl implements CommentService {


  private CommentRepository commentRepository;

  private UserRepository userRepository;

  private ActivityRepository activityRepository;

 private  ModelMapper modelMapper;

 private PostRepository postRepository;
  /**
   * Retrieve a comment by ID
   * @param commentId
   * @return a comment
   */
  @Override
  public CommentDto getCommentById(UUID commentId) {
    return modelMapper.map(this.commentRepository.findById(commentId)
        .orElseThrow(CommentNotFoundException::new), CommentDto.class);
  }

  /**
   * Update a given comment
   * @param commentId
   * @param comment
   * @param creatorEmail
   * @param activityId
   * @return updated comment
   */
  @Override
  public CommentDto updateComment(UUID commentId, CommentDto comment, String creatorEmail, UUID activityId) {

    if (checkIfHostOrCreater(activityId, creatorEmail) || checkIfOwnerOfComment(creatorEmail, comment)) {

      Comment updateCommentDto = this.commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
      Activity updateActivity = this.activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

      List<Comment> comments =  new ArrayList<>(updateActivity.getComments());
      comments.remove(updateCommentDto);

      updateCommentDto.setComment(comment.getComment());
      updateCommentDto = commentRepository.save(updateCommentDto);

      comments.add(updateCommentDto);
      updateActivity.setComments(comments);
      this.activityRepository.save(updateActivity);

      return modelMapper.map(updateCommentDto, CommentDto.class);
    }else
      throw new NotHostOrCreatorException();
  }


  /**
   * Saves a given comment
   * @param comment
   * @return the saved comment
   */
  @Override
  public CommentDto saveComment(Comment comment, UUID activityId, String creatorEmail) {

    User user = userRepository.findByEmail(creatorEmail).orElseThrow(UserNotFoundException::new);
    comment.setUser(user);

    Activity updateActivity = this.activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
    List<Comment> comments =  new ArrayList<>(updateActivity.getComments());
    comment.setId(UUID.randomUUID());
    comment.setActivity(updateActivity);
    comment = commentRepository.save(comment);
    comments.add(comment);
    this.activityRepository.save(updateActivity);
    return modelMapper.map(comment, CommentDto.class);
  }

    /**
     * Method to save a comment on a post
     * @param comment content of the comment to be created
     * @param postId id of the post the comment belongs to
     * @param creatorEmail email of the user that executed the create request
     * @return the saved comment
     */
    public CommentDto savePostComment(Comment comment, UUID postId, String creatorEmail) {
        User user = userRepository.findByEmail(creatorEmail).orElseThrow(UserNotFoundException::new);
        comment.setUser(user);
        Post updatePost = this.postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        List<Comment> comments =  new ArrayList<>(updatePost.getComments());
        comment.setId(UUID.randomUUID());
        comment = commentRepository.save(comment);
        comments.add(comment);
        updatePost.setComments(comments);
        this.postRepository.save(updatePost);
        return modelMapper.map(comment, CommentDto.class);
    }

    /**
     * Method to delete a comment on a post
     * @param commentId id of the comment to delete
     * @param postID id of the post the comment belongs to
     */
    public void deletePostComment(UUID commentId, UUID postID) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
            Post post = postRepository.findById(postID).orElseThrow(PostNotFoundExecption::new);
            List<Comment> comments =  new ArrayList<>(post.getComments());
            comments.remove(comment);
            post.setComments(comments);
            this.commentRepository.deleteById(commentId);
            postRepository.save(post);
    }

    /**
     * Method to update a comment on a post
     * @param commentId id of the comment to update
     * @param comment the new content of the comment
     * @param postId the id of the post that the comment belongs to
     * @return the updated comment
     */
    public CommentDto updatePostComment(UUID commentId, CommentDto comment, UUID postId) {
            Comment updateComment = this.commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
            Post updatePost = this.postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
            List<Comment> comments =  new ArrayList<>(updatePost.getComments());
            comments.remove(updateComment);
            updateComment.setComment(comment.getComment());
            updateComment = commentRepository.save(updateComment);
            comments.add(updateComment);
            updatePost.setComments(comments);
            this.postRepository.save(updatePost);
            return modelMapper.map(updateComment, CommentDto.class);
    }

    /**
     * Get all comments on a post
     * @param pageable pagination params for the request
     * @param postId id of the posts
     * @return paginated list of comments n given post
     */
    public Page<CommentDto>  getCommentsOnPost(Pageable pageable, UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);

        int total = post.getComments().size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Comment> output = new ArrayList<>();
        if (start <= end) {
            output = post.getComments().subList(start, end);
        }
        Page<Comment> comments = new PageImpl<>(output, pageable, total);
        return comments.map((s-> modelMapper.map(s, CommentDto.class)));

    }



    /**
   * Deletes a given comment with given ID
   * @param commentId
   * @param creatorEmail
   * @param activityId
   */
  @Override
  public void deleteComment(UUID commentId, String creatorEmail, UUID activityId) {

    if (checkIfHostOrCreater(activityId, creatorEmail) || checkIfOwnerOfComment(creatorEmail, modelMapper.map(commentRepository.findById(commentId), CommentDto.class))) {
      this.commentRepository.deleteById(commentId);
    } else
      throw new NotHostOrCreatorException();
  }


  /**
   *Deletes all comments on a given activity by activity ID
   * @param activityId
   * @param creatorEmail
   */
  @Override
  @Transactional
  public void deleteAllCommentsOnActivity(UUID activityId, String creatorEmail) {
    if (checkIfHostOrCreater(activityId, creatorEmail)) {

      Activity updateActivity = this.activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

      List<Comment> comments =  new ArrayList<>(updateActivity.getComments());
      comments.clear();
      updateActivity.setComments(comments);
      activityRepository.save(updateActivity);

    }else
      throw new NotHostOrCreatorException();
  }

    @Override
    public void deleteAllCommentsOnUser(String creatorEmail) {
        User user = userRepository.findByEmail(creatorEmail)
                .orElseThrow(UserNotFoundException::new);
        commentRepository.deleteCommentsByUser(user);
    }

    /**
   * Retrieve all comments on a given activity
   * @param activityId
   * @param pageable
   * @return List of all comments on activity
   */
  @Override
  public Page<CommentDto>  getCommentsOnActivity(Pageable pageable, UUID activityId) {

    Activity activityToFind = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
    int total = activityToFind.getComments().size();
    int start = toIntExact(pageable.getOffset());
    int end = Math.min((start + pageable.getPageSize()), total);
    List<Comment> output = new ArrayList<>();
    if (start <= end) {
      output = activityToFind.getComments().subList(start, end);
    }
    Page<Comment> comments = new PageImpl<>(output, pageable, total);
    return comments.map((s-> modelMapper.map(s, CommentDto.class)));

  }


  /**
   * Checks if user is a host or creator of a given activity
   * @param activityId
   * @param creatorEmail
   * @return true/false
   */
  private boolean checkIfHostOrCreater(UUID activityId, String creatorEmail){

    User user = userRepository.findByEmail(creatorEmail).orElseThrow(UserNotFoundException::new);
    Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
    return (activity.getCreator().equals(user) || activity.getHosts().contains(user));
  }

  /**
   * Checks if user is a the writer of a given comment
   *
   * @param email
   * @param commentDto
   * @return true/false
   */
  private boolean checkIfOwnerOfComment(String email, CommentDto commentDto) {
    Comment comment = modelMapper.map(commentDto, Comment.class);
    User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    return user.getComments().contains(comment);
  }

}