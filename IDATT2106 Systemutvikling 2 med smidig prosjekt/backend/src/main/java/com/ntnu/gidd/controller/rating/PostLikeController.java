package com.ntnu.gidd.controller.rating;

import com.ntnu.gidd.dto.LikeDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.service.rating.ActivityLikeService;
import com.ntnu.gidd.service.rating.PostLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("posts/{postId}/likes/")
@Api(tags = "Post like Management")
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @ApiOperation(value = "check if the logged inn user has liked a post")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public LikeDto hasLiked(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID postId) {
        return new LikeDto(postLikeService.hasLiked(principal.getUsername(), postId));

    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "add a like to the post by the logged inn user")
    public LikeDto like(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID postId) {
        return new LikeDto(postLikeService.addLike(principal.getUsername(), postId));


    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "remove a like to the post by the logged inn user")
    public LikeDto unLike(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID postId) {
        return new LikeDto(postLikeService.removeLike(principal.getUsername(), postId));
    }
}