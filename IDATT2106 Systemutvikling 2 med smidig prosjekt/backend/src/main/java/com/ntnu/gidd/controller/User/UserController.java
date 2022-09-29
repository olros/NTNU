package com.ntnu.gidd.controller.User;

import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.User.UserRegistrationDto;
import com.ntnu.gidd.exception.EmailInUseException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.service.User.UserService;
import com.ntnu.gidd.util.Constants;
import com.ntnu.gidd.util.Response;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users/")
@Api(tags = "User management")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isUser(#userId)")
    @ApiOperation(value = "Update a users info")
    public UserDto updateUser(@PathVariable UUID userId, @RequestBody UserDto user){
        log.debug("[X] Request to update user with id={}", user.getId());
        return this.userService.updateUser(userId, user);
    }

    @GetMapping("{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a users info")
    public UserDto getUser(@PathVariable UUID userId){
        log.debug("[X] Request to get user with id={}", userId);
        return userService.getUserByUUID(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all users info")
    public Page<UserDto> getAllUser(@QuerydslPredicate(root = User.class) Predicate predicate,
                              @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.ASC) Pageable pageable){
        log.debug("[X] Request to look up users");
        return this.userService.getAll(predicate, pageable);
    }

    @GetMapping("me/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a logged in users info")
    public UserDto getUser(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();

        log.debug("[X] Request to get personal userinfo with token");
        return this.userService.getUserDtoByEmail(user.getUsername());
    }

    @DeleteMapping("me/")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete the logged in users info")
    public Response deleteUser(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        log.debug("[X] Request to delete User with username={}", user.getUsername());
        userService.deleteUser(user.getUsername());
        return new Response("User has been deleted");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        log.debug("[X] Request to save user with email={}", userRegistrationDto.getEmail());
        return userService.saveUser(userRegistrationDto);
    }
}
