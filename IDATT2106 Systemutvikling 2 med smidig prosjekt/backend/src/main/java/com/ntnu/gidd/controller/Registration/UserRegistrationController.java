package com.ntnu.gidd.controller.Registration;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.exception.RegistrationNotFoundException;
import com.ntnu.gidd.model.Activity;
import java.util.UUID;

import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.repository.RegistrationRepository;
import com.ntnu.gidd.util.Constants;
import com.ntnu.gidd.service.Registration.RegistrationService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@RestController
@RequestMapping("users/me/registrations/")
@Api(tags = "User registration management")
public class UserRegistrationController {

  @Autowired
  private RegistrationService registrationService;

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get all activities a user is registered on")
  public Page<ActivityListDto> getRegistrationsForUser(@QuerydslPredicate(root = Registration.class) Predicate predicate,
                                                                   @PageableDefault(size = Constants.PAGINATION_SIZE,
                                                               sort="activity.startDate", direction = Sort.Direction.ASC) Pageable pageable,
                                                       Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    log.debug("[X] Request to look up activities registered for user with username={}", userDetails.getUsername());
    return registrationService.getRegistrationWithUsername(predicate, pageable, userDetails.getUsername());
  }

  @GetMapping("{activityId}/")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get one activity a user is registered on")
  public ActivityDto getRegistrationWithCompositeIdUser(Authentication authentication, @PathVariable UUID activityId) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    return registrationService.getRegistrationWithUsernameAndActivityId(userDetails.getUsername(), activityId);
  }

  @DeleteMapping("{activityId}/")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Delete one registration on a user")
  public Response deleteRegistration(Authentication authentication, @PathVariable UUID activityId){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    log.debug("[X] Request to delete Registration with activityId={} ", activityId);
    registrationService.deleteRegistrationWithUsernameAndActivityId(userDetails.getUsername(), activityId);
    return new Response("Registration has been deleted");
  }
}
