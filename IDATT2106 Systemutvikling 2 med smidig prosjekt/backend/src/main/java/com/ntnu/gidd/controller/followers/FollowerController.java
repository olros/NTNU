package com.ntnu.gidd.controller.followers;


import com.ntnu.gidd.dto.User.UserDto;
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
@Api(tags= "Followers Management")
public class FollowerController {

    private FollowerService followerService;

    @GetMapping("me/followers/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List the current users followers")
    public Page<UserDto> getCurrentUsersFollowers(@AuthenticationPrincipal UserDetailsImpl principal,
                                                  @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        return getFollowersOf(principal.getId(), pageable);
    }

    @GetMapping("{userId}/followers/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List the given users followers")
    public Page<UserDto> getFollowers(@PathVariable UUID userId,
                                      @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        return getFollowersOf(userId, pageable);
    }

    private Page<UserDto> getFollowersOf(UUID userId, Pageable pageable) {
        log.debug("[X] Request for to list users followers (id:{})", userId);
        return followerService.getFollowersOf(userId, pageable);
    }
    @DeleteMapping("/me/followers/{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Remove the given user from the current users followers")
    private Response removeFollower(@AuthenticationPrincipal UserDetailsImpl principal,
                                  @PathVariable UUID userId) {
        log.debug("[X] Request to remove follower with id: {}", userId);
        return followerService.unfollowUser(userId, principal.getId());
    }



}
