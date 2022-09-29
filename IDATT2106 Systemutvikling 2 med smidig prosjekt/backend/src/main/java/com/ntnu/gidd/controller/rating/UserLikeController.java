package com.ntnu.gidd.controller.rating;

import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.service.activity.ActivityService;
import com.ntnu.gidd.service.post.PostService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("users/me/")
@Api(tags = "User likes management for logged inn user")
public class UserLikeController {

    @Autowired
    ActivityService activityService;

    @Autowired
    PostService postService;


    @GetMapping("activity-likes/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all liked activities for the logged inn user")
    public Page<ActivityListDto> getLikedActivities(@QuerydslPredicate(root = Activity.class) Predicate predicate,
                                                    @PageableDefault(size = Constants.PAGINATION_SIZE, sort="startDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                    @AuthenticationPrincipal UserDetailsImpl principal){

        return activityService.getLikedActivities(predicate,pageable,principal.getUsername());
    }

    @GetMapping("post-likes/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all liked posts for the logged inn user")
    public Page<PostDto> getLikedPosts(@QuerydslPredicate(root = Activity.class) Predicate predicate,
                                       @PageableDefault(size = Constants.PAGINATION_SIZE, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                       @AuthenticationPrincipal UserDetailsImpl principal){
        return postService.getPostsLikes(predicate,pageable, principal.getUsername());
    }



}
