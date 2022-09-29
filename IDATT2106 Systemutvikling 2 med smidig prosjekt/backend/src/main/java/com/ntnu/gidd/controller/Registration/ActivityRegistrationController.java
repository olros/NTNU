package com.ntnu.gidd.controller.Registration;

import com.ntnu.gidd.dto.Registration.RegistrationUserDto;
import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.exception.ActivityFullExecption;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.RegistrationNotFoundException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Activity;
import java.util.UUID;

import com.ntnu.gidd.model.Registration;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ntnu.gidd.service.Registration.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("activities/{activityId}/registrations/")
@Api(tags = "Activity registration management")
public class ActivityRegistrationController {

  @Autowired
  private RegistrationService registrationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Add a registration to a activity")
  public RegistrationUserDto postRegistration(@PathVariable UUID activityId, @RequestBody UserEmailDto user) {
    try{

    }catch (UserNotFoundException | ActivityNotFoundException ex){
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, ex.getMessage());
    }catch (ActivityFullExecption ex){
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    log.debug("[X] Request to Post Registration with userid={}", user.getEmail());
    return registrationService.saveRegistration(user.getId(), activityId);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get all registrations on a activity")
  public Page<RegistrationUserDto> getRegistrationForActivity(@QuerydslPredicate(root = Registration.class) Predicate predicate,
                                                              @PageableDefault(size = Constants.PAGINATION_SIZE, sort="activity.startDate", direction = Sort.Direction.ASC) Pageable pageable,
                                                              @PathVariable UUID activityId) {
    log.debug("[X] Request to look up user registered for activity with id={}", activityId);
    return registrationService.getRegistrationForActivity(predicate, pageable, activityId);
  }

  @GetMapping("{userId}/")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.registrationPermissions(#activityId, #userId)")
  @ApiOperation(value = "Get one registration on a activity")
  public RegistrationUserDto getRegistrationWithCompositeIdActivity(@PathVariable UUID userId, @PathVariable UUID activityId) {
    return registrationService.getRegistrationWithCompositeId(userId, activityId);
  }

  @DeleteMapping("{userId}/")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.registrationPermissions(#activityId, #userId)")
  @ApiOperation(value = "Delete one registration on a activity")
  public Response deleteRegistration(@PathVariable UUID activityId, @PathVariable UUID userId){
    log.debug("[X] Request to delete Registration with userId={} ", userId);
    registrationService.deleteRegistrationWithCompositeId(userId, activityId);
    return new Response("Registration has been deleted");
  }

}
