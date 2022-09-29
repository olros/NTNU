package com.ntnu.gidd.controller.Host;

import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.service.Host.HostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("activities/{activityId}/hosts/")
@Api(tags= "Activity hosting Management")
public class ActivityHostController {

    @Autowired
    private HostService hostService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all users hosting an activity")
    public List<UserListDto> get(@PathVariable UUID activityId){
        log.debug("[X] Request to get Activities with id={}", activityId);
        return hostService.getByActivityId(activityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isCreator(#activityId)")
    @ApiOperation(value = "Add a host to a activity")
    public List<UserListDto> update(@PathVariable UUID activityId, @RequestBody UserEmailDto user){
        log.debug("[X] Request to create host on activity with id={}", activityId);
        return hostService.addHosts(activityId, user);
    }

    @DeleteMapping("{userId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isCreator(#activityId)")
    @ApiOperation(value = "Delete a host from a activity")
    public List<UserListDto>  delete(@PathVariable UUID activityId, @PathVariable UUID userId){
        log.debug("[X] Request to deleted host on activity with id={}", activityId);
        return hostService.deleteHost(activityId, userId);
    }


}
