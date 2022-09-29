package com.ntnu.gidd.controller.rating;

import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.service.post.PostService;
import com.ntnu.gidd.util.Constants;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("users/{userId}/post-likes/")
@Api(tags = "User likes management for posts")
public class UserPostLikeController {

    @Autowired
    PostService postService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all liked posts for a user")
    public Page<PostDto> getLikedPosts(@QuerydslPredicate(root = Activity.class) Predicate predicate,
                                       @PageableDefault(size = Constants.PAGINATION_SIZE, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                       @PathVariable UUID userId){
        return postService.getPostsLikes(predicate,pageable,userId);
    }



}
