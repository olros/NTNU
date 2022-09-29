package com.ntnu.gidd.service.Host;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HostService {
    Page<ActivityListDto> getAll(Predicate predicate, Pageable pageable, UUID userId);
    Page<ActivityListDto> getAllByEmail(Predicate predicate, Pageable pageable, String email);
    ActivityDto getActivityFromUser(UUID userId, UUID activityId);
    
    ActivityDto getActivityFromUserByEmail(String email, UUID activityId);
    
    List<UserListDto> getByActivityId(UUID id);
    List<UserListDto>  addHosts(UUID activityId, UserEmailDto users);
    List<UserListDto>  deleteHost(UUID activityId, UUID userId);
    List<ActivityListDto>  deleteHostfromUser(UUID activityId, UUID userId);
    List<ActivityListDto>  deleteHostfromUserByEmail(UUID activityId, String email);



}
