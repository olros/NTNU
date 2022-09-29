package com.ntnu.gidd.controller.invite;

import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.service.invite.InviteService;
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
@RequestMapping("activities/{activityId}/invites/")
@Api(tags= "Activity Invite Management")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all invites on a activity")
    public Page<UserListDto> getAll(@QuerydslPredicate(root = User.class) Predicate predicate,
                                 @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.DESC) Pageable pageable,
                                 @PathVariable UUID activityId){
        log.debug("[X] Request to get Activities invites with id={}", activityId);
        return inviteService.getAllInvites(predicate, pageable, activityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Add a user to invite list on a activity")
    public Page<UserListDto> update(@QuerydslPredicate(root = User.class) Predicate predicate,
                                    @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.DESC) Pageable pageable,
                                    @PathVariable UUID activityId, @RequestBody UserEmailDto user){
        log.debug("[X] Request to create host on activity with id={}", activityId);
        return inviteService.inviteUser(predicate, pageable,activityId, user);
    }

    @DeleteMapping("{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "delete a user from invite list on a activity")
    public Page<UserListDto>  delete(@QuerydslPredicate(root = User.class) Predicate predicate,
                                     @PageableDefault(size = Constants.PAGINATION_SIZE, sort="firstName", direction = Sort.Direction.DESC) Pageable pageable,
                                     @PathVariable UUID activityId, @PathVariable UUID userId){
        log.debug("[X] Request to delete host on activity with id={}", activityId);
        return inviteService.unInviteUser(predicate, pageable, activityId, userId);
    }
}
