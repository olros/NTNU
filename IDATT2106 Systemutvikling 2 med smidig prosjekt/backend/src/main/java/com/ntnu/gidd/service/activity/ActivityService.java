package com.ntnu.gidd.service.activity;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.model.GeoLocation;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActivityService {
    ActivityDto updateActivity(UUID id, ActivityDto activity, String email);
    ActivityDto getActivityById(UUID id, String email);
    ActivityDto getActivityById(UUID id);
    ActivityDto saveActivity(ActivityDto activity, String creatorEmail);
    void deleteActivity(UUID id);
    Page<ActivityListDto> getActivities(Predicate predicate, Pageable pageable, GeoLocation position, Double range, String email);
    Page<ActivityListDto> getActivities(Predicate predicate, Pageable pageable,  String email);
    Page<ActivityListDto> getLikedActivities(Predicate predicate, Pageable pageable, String email);
    Page<ActivityListDto> getLikedActivities(Predicate predicate, Pageable pageable, UUID id);

}

