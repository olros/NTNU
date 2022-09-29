package com.ntnu.gidd.controller.Host;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.service.Host.HostService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("users/me/hosts/")
@Api(tags= "User hosting Management")
public class UserHostController {

    @Autowired
    private HostService hostService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all activities a user is hosting")
    public Page<ActivityListDto> getAll(@QuerydslPredicate(root = Activity.class) Predicate predicate,
                                        @PageableDefault(size = Constants.PAGINATION_SIZE, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable, Authentication authentication){
        log.debug("[X] Request to get all Activities of user with userEmail={}", authentication.getName());
        return hostService.getAllByEmail(predicate, pageable, authentication.getName());
    }

    @GetMapping("{activityId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a activity a user is hosting")
    public ActivityDto get(@PathVariable UUID activityId, Authentication authentication){
        log.debug("[X] Request to get Activity of user with userEmail={} and activityId={}", authentication.getName(),activityId);
        return hostService.getActivityFromUserByEmail(authentication.getName(), activityId);
    }
    @DeleteMapping("{activityId}/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete users form hosting a activity")
    public List<ActivityListDto>  delete(Authentication authentication, @PathVariable UUID activityId ){
        log.debug("[X] Request to deleted host on user with userEmail={}", authentication.getName());
        return hostService.deleteHostfromUserByEmail(activityId, authentication.getName());
    }

}
