package com.ntnu.gidd.controller.Registration;

import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.service.Registration.RegistrationService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("users/{userId}/registrations/")
@Api(tags = "User registration management")
public class UserRegistrationListController {

    @Autowired
    private RegistrationService registrationService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all activities a user is registered on")
    public Page<ActivityListDto> getRegistrationsForUser(@QuerydslPredicate(root = Registration.class) Predicate predicate,
                                                         @PageableDefault(size = Constants.PAGINATION_SIZE,
                                                                 sort="activity.startDate", direction = Sort.Direction.ASC) Pageable pageable,
                                                         @PathVariable UUID userId) {
        return registrationService.getRegistrationsForUser(predicate, pageable,userId);
    }
}
