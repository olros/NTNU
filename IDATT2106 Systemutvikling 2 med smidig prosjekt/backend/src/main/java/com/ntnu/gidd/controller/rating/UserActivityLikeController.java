package com.ntnu.gidd.controller.rating;


import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.service.activity.ActivityService;
import com.ntnu.gidd.util.Constants;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
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
@RequestMapping("users/{userId}/activity-likes/")
@Api(tags = "User activity like Management")
public class UserActivityLikeController {

    @Autowired
    ActivityService activityService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ActivityListDto> getLikedActivities(@QuerydslPredicate(root = Activity.class) Predicate predicate,
                                                    @PageableDefault(size = Constants.PAGINATION_SIZE, sort="startDate", direction = Sort.Direction.ASC) Pageable pageable,
                                                    @PathVariable UUID userId){
        return activityService.getLikedActivities(predicate,pageable,userId);
    }



}
