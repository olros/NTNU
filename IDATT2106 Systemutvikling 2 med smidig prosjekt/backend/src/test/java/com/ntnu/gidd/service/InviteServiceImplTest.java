package com.ntnu.gidd.service;

import com.ntnu.gidd.config.ModelMapperConfig;
import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.Registration.RegistrationService;
import com.ntnu.gidd.service.invite.InviteServiceImpl;
import com.ntnu.gidd.utils.JpaUtils;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ModelMapperConfig.class)
@SpringBootTest
public class InviteServiceImplTest {

    @Mock
    UserRepository userRepository;


    @Mock
    ActivityRepository activityRepository;


    @Spy
    @Autowired
    ModelMapper modelMapper;

    @Mock
    RegistrationService registrationService;

    @InjectMocks
    InviteServiceImpl inviteService;


    ActivityFactory activityFactory = new ActivityFactory();

    UserFactory userFactory = new UserFactory();

    private Activity activity;

    private User user;
    private Predicate predicate;
    private Pageable pageable;



    @BeforeEach
    public void setup() throws Exception {
        activity = activityFactory.getObject();
        assert activity != null;

        when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));

        user = userFactory.getObject();
        assert user != null;
        activity.setInvites(List.of(user));
        lenient().when(activityRepository.save(activity)).thenReturn(activity);
        predicate = JpaUtils.getEmptyPredicate();
        pageable = JpaUtils.getDefaultPageable();
    }

    @Test
    public void testInviteUserAddsNewUserToInviteList() throws Exception {
        User addedUser = userFactory.getObject();
        assert addedUser != null;
        List<User> users = List.of(addedUser, user);
        Page<User> invites = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findByEmail(addedUser.getEmail())).thenReturn(Optional.of(addedUser));

       when(userRepository.findUserByInvites(activity,pageable)).thenReturn(invites);

        Page<UserListDto> actualPage = inviteService.inviteUser(predicate, pageable, activity.getId(), UserEmailDto.builder().email(addedUser.getEmail()).build());

        assertThat(actualPage.getContent().size()).isEqualTo(invites.getContent().size());
        for (int i = 0; i< actualPage.getContent().size(); i++) {
            assertThat(invites.getContent().get(i).getEmail()).isEqualTo(invites.getContent().get(i).getEmail());
        }
    }

    @Test
    public void testUnInviteUserRemovesUserFromInviteList() throws Exception {
        User removedUser = userFactory.getObject();
        assert removedUser != null;
        List<User> users = new ArrayList<>();
        users.addAll(List.of(user, removedUser));
        activity.setInvites(users);
        Page<User> invites = new PageImpl<>(activity.getInvites(), pageable, activity.getInvites().size());
        when(userRepository.findById(removedUser.getId())).thenReturn(Optional.of(removedUser));
        when(userRepository.findUserByInvites(activity,pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, activity.getInvites().size()-1));
        Page<UserListDto> actualPage = inviteService.unInviteUser(predicate, pageable, activity.getId(), removedUser.getId());
        assertThat(actualPage.getContent().size()).isEqualTo(invites.getContent().size()-1);
        assertThat(activity.getInvites().size()).isEqualTo(actualPage.getContent().size());

    }

    @Test
    public void testInviteBatchAddsListOfUsers() throws Exception {
        List<User> users = new ArrayList<>();
        int startSize = activity.getInvites().size();
        users.addAll(List.of(Objects.requireNonNull(userFactory.getObject()), userFactory.getObject()));
        inviteService.inviteBatch(activity.getId(), users);
        assertThat(activity.getInvites().size()).isEqualTo(startSize+users.size());

    }
}
