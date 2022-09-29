package com.ntnu.gidd.service.followers;

import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.followers.FollowRequest;
import com.ntnu.gidd.exception.InvalidFollowRequestException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.User.UserService;
import com.ntnu.gidd.utils.JpaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerServiceImplTest {

    private FollowerService followerService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    private final UserFactory userFactory = new UserFactory();

    private User actor;

    private User subject;

    private FollowRequest followRequest;

    private List<User> followedByActor;

    private List<User> subjectsFollowers;

    private Pageable pageable;

    @BeforeEach
    void setUp() throws Exception {
        followerService = new FollowerServiceImpl(userService, userRepository, modelMapper);

        actor = userFactory.getObject();
        subject = userFactory.getObject();
        followRequest = new FollowRequest(actor.getId(), subject.getId());


        lenient().when(userService.getUserById(actor.getId())).thenReturn(actor);
        lenient().when(userService.getUserById(subject.getId())).thenReturn(subject);

        followedByActor = List.of(subject);
        pageable = JpaUtils.getDefaultPageable();
        Page<User> followers = new PageImpl<>(followedByActor, pageable, followedByActor.size());

        lenient().when(userRepository.findByFollowersId(actor.getId(), pageable))
                .thenReturn(followers);

        subjectsFollowers = List.of(actor);
        pageable = JpaUtils.getDefaultPageable();
        Page<User> following = new PageImpl<>(subjectsFollowers, pageable, followedByActor.size());

        lenient().when(userRepository.findByFollowingId(subject.getId(), pageable))
                .thenReturn(following);
    }

    @Test
    public void testRegisterFollowWhenActorAndSubjectAreIdenticalThrowsError() {
        followRequest.setSubjectId(actor.getId());

        assertThatExceptionOfType(InvalidFollowRequestException.class)
                .isThrownBy(() -> followerService.registerFollow(followRequest));
    }

    @Test
    public void testRegisterFollowWhenValidRequestAddsSubjectToActorsFollowing() {
        followerService.registerFollow(followRequest);

        assertThat(actor.getFollowing()).contains(subject);
    }

    @Test
    public void testRegisterFollowWhenValidRequestAddsActorToSubjectsFollowers() {
        followerService.registerFollow(followRequest);

        assertThat(subject.getFollowers()).contains(actor);
    }

    @Test
    public void testRegisterFollowWhenActorNotFoundThrowsError() {
        when(userService.getUserById(actor.getId())).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.registerFollow(followRequest));
    }

    @Test
    public void testRegisterFollowWhenSubjectNotFoundThrowsError() {
        when(userService.getUserById(subject.getId())).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.registerFollow(followRequest));
    }

    @Test
    void testGetFollowingForReturnsAllUsersFollowedByGivenUser() {
        Page<UserDto> actualFollowing = followerService.getFollowingFor(actor.getId(), pageable);

        assertThat(actualFollowing).hasSameSizeAs(followedByActor);

        Stream<UUID> actualFollowingIds = actualFollowing.stream()
                .map(UserDto::getId);

        assertThat(actualFollowingIds).contains(subject.getId());
    }

    @Test
    void testGetFollowingForWhenUserDoesNotExistThrowsError() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistentUserId)).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.getFollowingFor(nonExistentUserId, pageable));
    }


    @Test
    void testGetFollowersOfReturnsAllUsersFollowingGivenUser() {
        Page<UserDto> actualFollowers = followerService.getFollowersOf(subject.getId(), pageable);

        assertThat(actualFollowers).hasSameSizeAs(followedByActor);

        Stream<UUID> actualFollowingIds = actualFollowers.stream()
                .map(UserDto::getId);

        assertThat(actualFollowingIds).contains(actor.getId());
    }

    @Test
    void testGetFollowersOfWhenUserHasNoFollowersReturnsEmptyPage() {
        int expectedSize = 0;
        Page<User> emptyFollowersPage = new PageImpl<>(new ArrayList<>(), pageable, expectedSize);

        lenient().when(userRepository.findByFollowingId(subject.getId(), pageable))
                .thenReturn(emptyFollowersPage);

        Page<UserDto> actualFollowers = followerService.getFollowersOf(subject.getId(), pageable);

        assertThat(actualFollowers).hasSize(expectedSize);
    }

    @Test
    void testGetFollowersOfWhenUserDoesNotExistThrowsError() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistentUserId)).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.getFollowersOf(nonExistentUserId, pageable));
    }

    @Test
    void testUnfollowUserWhenActorDoesNotExistThrowsError() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistentUserId)).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.unfollowUser(nonExistentUserId, subject.getId()));
    }

    @Test
    void testUnfollowUserWhenSubjectDoesNotExistThrowsError() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistentUserId)).thenThrow(UserNotFoundException.class);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> followerService.unfollowUser(actor.getId(), nonExistentUserId));
    }

    @Test
    void testUnfollowUserWhenBothExistRemovesSubjectFromActorsFollowing() {
        actor.addFollowing(subject);

        followerService.unfollowUser(actor.getId(), subject.getId());

        assertThat(actor.getFollowing()).doesNotContain(subject);
    }

    @Test
    void testUnfollowUserWhenBothExistRemovesActorFromSubjectsFollowers() {
        actor.addFollowing(subject);

        followerService.unfollowUser(actor.getId(), subject.getId());

        assertThat(subject.getFollowers()).doesNotContain(actor);
    }
}