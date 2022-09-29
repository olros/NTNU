package com.ntnu.gidd.controller.followers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FollowerControllerTest {

    private static final String URI = "/users/";
    private static final String URI_SUFFIX = "/followers/";
    private static final String URI_ME = URI + "me" + URI_SUFFIX;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private UserFactory userFactory = new UserFactory();
    
    private User actor;

    private User subject;

    private User nonFollowing;

    private UserDetailsImpl actorUserDetails;

    @BeforeEach
    void setUp() throws Exception {
        actor = userFactory.getObject();
        subject = userFactory.getObject();
        nonFollowing = userFactory.getObject();

        actor = userRepository.saveAndFlush(actor);
        subject = userRepository.saveAndFlush(subject);
        nonFollowing = userRepository.saveAndFlush(nonFollowing);

        actorUserDetails = UserDetailsImpl.builder().id(actor.getId())
                .email(actor.getEmail())
                .build();
    }

    private static String getUsersUri(User user) {
        return URI + user.getId().toString() + URI_SUFFIX;
    }

    private static String getMeDetailUri(User user) {
        return URI_ME + user.getId().toString() + "/";
    }

    @Test
    public void testGetCurrentUsersFollowersWhenSuccessfulReturnsHttp200() throws Exception {
        mvc.perform(get(URI_ME)
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentUsersFollowersWhenSuccessfulReturnsAllUserAreFollowing() throws Exception {
        subject.addFollowing(actor);
        userRepository.save(subject);

        mvc.perform(get(URI_ME)
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowers().size()))
                .andExpect(jsonPath("$.content.[*].id", hasItem(subject.getId().toString())));
    }

    @Test
    public void testGetCurrentUsersFollowersWhenUnauthenticatedReturnsHttp401() throws Exception {
        mvc.perform(get(URI_ME)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetFollowersWhenSuccessfulReturnsHttp200() throws Exception {
        mvc.perform(get(getUsersUri(actor))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFollowersWhenSuccessfulReturnsAllUserAreFollowing() throws Exception {
        subject.addFollowing(actor);
        userRepository.save(subject);

        actor = userRepository.findById(actor.getId()).get();

        mvc.perform(get(getUsersUri(actor))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowers().size()))
                .andExpect(jsonPath("$.content.[*].id", hasItem(subject.getId().toString())));
    }

    @Test
    public void testGetFollowersWhenUserNotFoundReturnsHttp404() throws Exception {
        User nonExistentUser = userFactory.getObject();

        mvc.perform(get(getUsersUri(nonExistentUser))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetFollowersWhenUnauthenticatedReturnsHttp401() throws Exception {
        mvc.perform(get(getUsersUri(actor))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testRemoveFollowerWhenSuccessfulReturnsHttp200() throws Exception {
        subject.addFollowing(actor);
        userRepository.save(subject);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void testRemoveFollowerWhenSuccessfulRemovesSubjectFromActorsFollowers() throws Exception {
        subject.addFollowing(actor);
        userRepository.save(subject);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON));

        actor = userRepository.findById(actor.getId()).get();

        assertThat(actor.getFollowers()).doesNotContain(subject);
    }

    @Test
    public void testRemoveFollowerWhenSuccessfulRemovesActorFromSubjectsFollowing() throws Exception {
        subject.addFollowing(actor);
        userRepository.save(subject);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON));

        subject = userRepository.findById(subject.getId()).get();

        assertThat(subject.getFollowing()).doesNotContain(actor);
    }

    @Test
    public void testRemoveFollowerWhenSubjectDoesNotFollowActorReturnsHttp200() throws Exception {
        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveFollowerWhenUnauthenticatedReturnsHttp401() throws Exception {
        User nonExistentUser = userFactory.getObject();

        mvc.perform(delete(getMeDetailUri(nonExistentUser))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}