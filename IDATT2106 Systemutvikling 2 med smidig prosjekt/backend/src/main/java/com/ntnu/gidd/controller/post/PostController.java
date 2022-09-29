package com.ntnu.gidd.controller.post;


import com.ntnu.gidd.dto.post.PostCreateDto;
import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.service.post.PostService;
import com.ntnu.gidd.util.Constants;
import com.ntnu.gidd.util.Response;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;



@Slf4j
@RestController
@RequestMapping("posts/")
@Api(tags = "Post Management")
public class PostController {


    @Autowired
    private PostService postService;


    @ApiOperation(value = "Find all posts")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PostDto> getAll(@QuerydslPredicate(root = Post.class) Predicate predicate,
                                @PageableDefault(size = Constants.PAGINATION_SIZE, sort="content", direction = Sort.Direction.ASC) Pageable pageable,
                                Authentication authentication){
        log.debug("[x] Request to get all posts");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return postService.findAllPosts(predicate, pageable, userDetails.getUsername());

    }
    @ApiOperation(value = "Find one post by id")
    @GetMapping("{postId}/")
    @ResponseStatus(HttpStatus.OK)
    public PostDto get(Authentication authentication,@PathVariable UUID postId){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.debug("[x] Request to get post by id={}", postId);
        return postService.getPostById(postId, userDetails.getUsername());
    }

    @ApiOperation(value = "Create new post")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PostDto create(Authentication authentication, @RequestBody PostCreateDto post){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.debug("[x] Request to create a post");
        return postService.savePost(post, userDetails.getUsername());
    }

    @ApiOperation(value = "Update post by id")
    @PutMapping("{postId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.postPermissions(#postId)")
    public PostDto update(Authentication authentication,@PathVariable UUID postId, @RequestBody PostDto post){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        log.debug("[x] Request to update a post by id={}", postId);
        return postService.updatePost(postId, post, userDetails.getUsername());

    }

    @ApiOperation(value = "Delete post by id")
    @DeleteMapping("{postId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.postPermissions(#postId)")
    public Response delete(@PathVariable UUID postId){
        postService.deletePost(postId);
        return new Response("Post has been deleted");
    }
}
