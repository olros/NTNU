package com.ntnu.gidd.service.followers;

import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.followers.FollowRequest;
import com.ntnu.gidd.util.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FollowerService {

     Response registerFollow(FollowRequest followRequest);

    Page<UserDto> getFollowingFor(UUID id, Pageable pageable);

    Page<UserDto> getFollowersOf(UUID id, Pageable pageable);

    Response unfollowUser(UUID actorId, UUID followingId);
}
