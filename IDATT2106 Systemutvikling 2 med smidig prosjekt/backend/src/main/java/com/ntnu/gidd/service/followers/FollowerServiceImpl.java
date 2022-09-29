package com.ntnu.gidd.service.followers;

import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.followers.FollowRequest;
import com.ntnu.gidd.exception.InvalidFollowRequestException;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.User.UserService;
import com.ntnu.gidd.util.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of a follower service
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FollowerServiceImpl implements FollowerService {

    private UserService userService;

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    /**
     * Method to register a follower on a user
     * @param followRequest
     * @return success response
     */
    @Override
    public Response registerFollow(FollowRequest followRequest) {
        log.debug("[X] Registering request to follow: {}", followRequest);

        if (followRequest.isIdentical())
            throw new InvalidFollowRequestException("You cannot follow yourself");

        User actor = userService.getUserById(followRequest.getActorId());
        User subject = userService.getUserById(followRequest.getSubjectId());

        actor.addFollowing(subject);

        log.debug("[X] Successfully followed user (actor:{}, subject:{}", actor.getId(), subject.getId());
        return new Response("Successfully followed user");
    }

    /**
     * Method to get all users a user is following
     * @param id the id of the user
     * @param pageable pagiantion params
     * @return List of users
     */
    @Override
    public Page<UserDto> getFollowingFor(UUID id, Pageable pageable) {
        log.debug("[X] Retrieving all users this user is following (id:{})", id);
        User subject = userService.getUserById(id);

        return userRepository.findByFollowersId(subject.getId(), pageable)
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    /**
     * Method to get all users that is following a given user
     * @param id id of the user
     * @param pageable pagiantion params
     * @return List of followers
     */
    @Override
    public Page<UserDto> getFollowersOf(UUID id, Pageable pageable) {
        log.debug("[X] Retrieving followers of user with id {}", id);
        User subject = userService.getUserById(id);

        return userRepository.findByFollowingId(subject.getId(), pageable)
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    /**
     * Method to unfollow a user
     * @param actorId the user that is executing the request
     * @param subjectId the user to unfollow
     * @return success response
     */
    @Override
    public Response unfollowUser(UUID actorId, UUID subjectId) {
        log.debug("[X] Unfollowing user with id:{}, actor:{}", subjectId, actorId);
        User actor = userService.getUserById(actorId);
        User subject = userService.getUserById(subjectId);

        actor.removeFollowing(subject);

        log.debug("[X] Successfully followed user (actor:{}, subject:{}", actor.getId(), subject.getId());
        return new Response("Successfully unfollowed user");
    }
}
