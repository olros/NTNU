package com.ntnu.gidd.controller.followers;


import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.followers.FollowRequest;
import com.ntnu.gidd.exception.InvalidFollowRequestException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.service.followers.FollowerService;
import com.ntnu.gidd.util.Constants;
import com.ntnu.gidd.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("users/")
@AllArgsConstructor
@Api(tags= "Following Management")
public class FollowingController {

    private FollowerService followerService;

    @PostMapping("me/following/")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Follow user with the provided id")
    public Response followUser(@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody UserEmailDto user) {
        log.debug("[X] Request for user with id {} to follow user with id {}", principal.getId(), user.getId());

        FollowRequest followRequest = new FollowRequest(principal.getId(), user.getId());
        return followerService.registerFollow(followRequest);
    }

    @GetMapping("me/following/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List all users the current user is following")
    public Page<UserDto> getCurrentUsersFollowing(@AuthenticationPrincipal UserDetailsImpl principal,
                                      @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        return getFollowingFor(principal.getId(), pageable);
    }

    @GetMapping("{userId}/following/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List all users the user with the given id is following")
    public Page<UserDto> getFollowing(@PathVariable UUID userId,
                                      @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        return getFollowingFor(userId, pageable);
    }
    
    private Page<UserDto> getFollowingFor(UUID userId, Pageable pageable) {
        log.debug("[X] Request for to list users the given user is following (id:{})", userId);
        return followerService.getFollowingFor(userId, pageable);
    }

    @DeleteMapping("/me/following/{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Unfollow the given user")
    private Response unfollowUser(@AuthenticationPrincipal UserDetailsImpl principal,
                                  @PathVariable UUID userId) {
        log.debug("[X] Request to unfollow user with id: {}", userId);
        return followerService.unfollowUser(principal.getId(), userId);
    }

}
