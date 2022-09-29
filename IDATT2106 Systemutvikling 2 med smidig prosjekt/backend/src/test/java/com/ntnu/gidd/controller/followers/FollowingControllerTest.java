package com.ntnu.gidd.controller.followers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.dto.User.UserEmailDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FollowingControllerTest {

    private static final String URI = "/users/";
    private static final String URI_SUFFIX = "/following/";
    private static final String URI_ME = URI + "me" + URI_SUFFIX;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private UserFactory userFactory = new UserFactory();

    private ObjectMapper objectMapper = new ObjectMapper();

    private User actor;

    private User subject;

    private UserDetailsImpl actorUserDetails;

    private String subjectIdRequestBody;

    @BeforeEach
    void setUp() throws Exception {
        actor = userFactory.getObject();
        subject = userFactory.getObject();
        User nonFollowing = userFactory.getObject();

        actor = userRepository.saveAndFlush(actor);
        subject = userRepository.saveAndFlush(subject);
        userRepository.saveAndFlush(nonFollowing);

        actorUserDetails = UserDetailsImpl.builder().id(actor.getId())
                .email(actor.getEmail())
                .build();
        UserEmailDto userEmailDto = new UserEmailDto("", subject.getId());
        subjectIdRequestBody = objectMapper.writeValueAsString(userEmailDto);
    }

    private static String getUsersUri(User user) {
        return URI + user.getId().toString() + URI_SUFFIX;
    }

    private static String getMeDetailUri(User user) {
        return URI_ME + user.getId().toString() + "/";
    }

    @Test
    public void testFollowUserWhenAttemptingToFollowSelfReturnsStatus400() throws Exception {
        String actorIdRequestBody = objectMapper.writeValueAsString(actor.getId());

        mvc.perform(post(URI_ME)
                            .with(user(actorUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(actorIdRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFollowUserWhenValidRequestReturnsStatus201() throws Exception {
        mvc.perform(post(URI_ME)
                            .with(user(actorUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subjectIdRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void testFollowUserWhenValidRequestAddsSubjectToActorsFollowing() throws Exception {
        mvc.perform(post(URI_ME)
                            .with(user(actorUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subjectIdRequestBody));

        actor = userRepository.findById(actor.getId()).get();

        assertThat(actor.getFollowing()).contains(subject);
    }

    @Test
    public void testFollowUserWhenValidRequestAddsActorToSubjectsFollowers() throws Exception {
        mvc.perform(post(URI_ME)
                            .with(user(actorUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subjectIdRequestBody));

        subject = userRepository.findById(subject.getId()).get();

        assertThat(subject.getFollowers()).contains(actor);
    }


    @Test
    public void testFollowUserWhenUnauthorizedReturnsHttp401() throws Exception {
        mvc.perform(post(URI_ME)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subjectIdRequestBody))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFollowUserWhenSubjectNotFoundReturnsStatus404() throws Exception {
        UserEmailDto nonExistentUser = new UserEmailDto("", UUID.randomUUID());
        String nonExistentUserIdRequestBody = objectMapper.writeValueAsString(nonExistentUser);

        mvc.perform(post(URI_ME)
                            .with(user(actorUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nonExistentUserIdRequestBody))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentUsersFollowingWhenSuccessfulReturnsHttp200() throws Exception {
        mvc.perform(get(URI_ME)
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentUsersFollowingWhenSuccessfulReturnsAllUserAreFollowing() throws Exception {
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(get(URI_ME)
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowing().size()))
                .andExpect(jsonPath("$.content.[*].id", hasItem(subject.getId().toString())));
    }

    @Test
    public void testGetCurrentUsersFollowingWhenUnauthenticatedReturnsHttp401() throws Exception {
        mvc.perform(get(URI_ME)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetFollowingWhenSuccessfulReturnsHttp200() throws Exception {
        mvc.perform(get(getUsersUri(actor))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFollowingWhenSuccessfulReturnsAllUserAreFollowing() throws Exception {
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(get(getUsersUri(actor))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowing().size()))
                .andExpect(jsonPath("$.content.[*].id", hasItem(subject.getId().toString())));
    }

    @Test
    public void testGetFollowingWhenUserNotFoundReturnsHttp404() throws Exception {
        User nonExistentUser = userFactory.getObject();

        mvc.perform(get(getUsersUri(nonExistentUser))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetFollowingWhenUnauthenticatedReturnsHttp401() throws Exception {
        mvc.perform(get(getUsersUri(actor))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
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
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(get(URI_ME)
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowing().size()))
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
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(get(getUsersUri(actor))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(actor.getFollowing().size()))
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
    public void testUnfollowUserWhenSubjectDoesNotExistReturnsHttp404() throws Exception {
        User nonExistentUser = userFactory.getObject();

        mvc.perform(delete(getMeDetailUri(nonExistentUser))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnfollowUserWhenSuccessfulReturnsHttp200() throws Exception {
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void testUnfollowUserWhenSuccessfulRemovesSubjectFromActorsFollowing() throws Exception {
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON));

        actor = userRepository.findById(actor.getId()).get();

        assertThat(actor.getFollowing()).doesNotContain(subject);
    }

    @Test
    public void testUnfollowUserWhenSuccessfulRemovesActorFromSubjectsFollowers() throws Exception {
        actor.addFollowing(subject);
        userRepository.save(actor);

        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON));

        subject = userRepository.findById(subject.getId()).get();

        assertThat(subject.getFollowers()).doesNotContain(actor);
    }

    @Test
    public void testUnfollowUserWhenSubjectDoesNotFollowActorReturnsHttp200() throws Exception {
        mvc.perform(delete(getMeDetailUri(subject))
                            .with(user(actorUserDetails))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnfollowUserWhenUnauthenticatedReturnsHttp401() throws Exception {
        User nonExistentUser = userFactory.getObject();

        mvc.perform(delete(getMeDetailUri(nonExistentUser))
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}