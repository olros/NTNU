package com.ntnu.gidd.controller.Comment;

import com.ntnu.gidd.dto.Comment.CommentDto;
import com.ntnu.gidd.model.Comment;
import com.ntnu.gidd.service.Comment.CommentService;
import com.ntnu.gidd.util.Constants;
import com.ntnu.gidd.util.Response;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("posts/{postId}/comments/")
@Api(tags= "Post comment management")
public class PostCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("{commentId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a comment by id")
    public CommentDto getCommentById(@PathVariable UUID commentId){
        log.debug("[X] Request to get comment with id={}", commentId);
        return this.commentService.getCommentById(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all comments on a post")
    public Page<CommentDto> getCommentsOnPost(@PathVariable UUID postId,
                                                  @PageableDefault(size = Constants.PAGINATION_SIZE, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable)  {
        log.debug("[X] Request to get comments on post with id={}", postId);
        return this.commentService.getCommentsOnPost(pageable, postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a comment on a post")
    public CommentDto saveComment(@RequestBody Comment comment, @PathVariable UUID postId, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.debug("[X] Request to create comment with id={}", comment.getId());
        return commentService.savePostComment(comment, postId, userDetails.getUsername());
    }

    @PutMapping("{commentId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.postCommentPermissions(#postId, #commentId)")
    @ApiOperation(value = "Update a comment on a post by id")
    public CommentDto updateComment(@PathVariable UUID commentId, @RequestBody CommentDto commentDto,  @PathVariable UUID postId){
        log.debug("[X] Request to update comment with id={}", commentId);
        return this.commentService.updatePostComment(commentId, commentDto, postId);
    }

    @DeleteMapping("{commentId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.postCommentPermissions(#postId, #commentId)")
    @ApiOperation(value = "Delete a comment on a post by id")
    public Response deleteComment(@PathVariable UUID commentId, @PathVariable UUID postId){
        log.debug("[X] Request to delete comment with id={}", commentId);
        this.commentService.deletePostComment(commentId, postId);
        return new Response("Comment has been deleted");
    }

}
