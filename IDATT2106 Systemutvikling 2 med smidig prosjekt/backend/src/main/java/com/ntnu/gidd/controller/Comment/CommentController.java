package com.ntnu.gidd.controller.Comment;


import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.service.Comment.CommentService;
import com.ntnu.gidd.util.Constants;
import java.rmi.activation.ActivationException;
import java.util.List;
import java.util.UUID;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("activities/{activityId}/comments/")
@Api(tags= "Activity comment management")
public class CommentController {

  @Autowired
  private CommentService commentService;

  @GetMapping("{commentId}/")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get a comment by id")
  public CommentDto getCommentbyId(@PathVariable UUID commentId){
    log.debug("[X] Request to get comment with id={}", commentId);
    return this.commentService.getCommentById(commentId);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "List all comments on a activity")
  public Page<CommentDto> getCommentsOnActivity(@PathVariable UUID activityId,
                                                @PageableDefault(size = Constants.PAGINATION_SIZE, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable)  {
    log.debug("[X] Request to get comments on activity with id={}", activityId);
    return this.commentService.getCommentsOnActivity(pageable, activityId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create a comment on a activity")
  public CommentDto saveComment(@RequestBody Comment comment, @PathVariable UUID activityId,  Authentication authentication){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.debug("[X] Request to create comment with id={}", comment.getId());
    return commentService.saveComment(comment, activityId, userDetails.getUsername());
  }

  @PutMapping("{commentId}/")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.commentPermissions(#activityId, #commentId)")
  public CommentDto updateComment(@PathVariable UUID commentId, @RequestBody CommentDto commentDto, Authentication authentication, @PathVariable UUID activityId){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.debug("[X] Request to update comment with id={}", commentId);
    return this.commentService.updateComment(commentId, commentDto, userDetails.getUsername(), activityId);
  }

  @DeleteMapping("{commentId}/")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.commentPermissions(#activityId, #commentId)")
  @ApiOperation(value = "Delete a comment on a activity by id")
  public void deleteComment(@PathVariable UUID commentId, Authentication authentication, @PathVariable UUID activityId){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.debug("[X] Request to delete comment with id={}", commentId);
    this.commentService.deleteComment(commentId, userDetails.getUsername(), activityId);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.userHasActivityAccess(#activityId)")
  @ApiOperation(value = "Delete all comments on a activity")
  public void deleteAllCommentsOnActivity(@PathVariable UUID activityId, Authentication authentication){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.debug("[X] Request to delete all comments on activity with id={}", activityId);
    this.commentService.deleteAllCommentsOnActivity(activityId, userDetails.getUsername());
  }

}
