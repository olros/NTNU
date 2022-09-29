package com.ntnu.gidd.security;


import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.RegistrationFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.RegistrationRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = MOCK)
public class SecurityServiceTest {

    @InjectMocks
    SecurityService securityService;

    @Mock
    ActivityRepository activityRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RegistrationRepository registrationRepository;

    private ActivityFactory activityFactory = new ActivityFactory();
    private UserFactory userFactory = new UserFactory();
    private RegistrationFactory registrationFactory = new RegistrationFactory();


    private User user;
    private Activity activity;
    private Registration registration;

    @BeforeEach
    public void setup() throws Exception {
        user = userFactory.getObject();
        activity = activityFactory.getObject();
        registration = registrationFactory.getObject();
        lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
    }

    @Test
    public void testUserHasActivityAccessTrueForCreatorAndHost(){
        when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));
        activity.setCreator(user);
        assertThat(securityService.userHasActivityAccess(activity.getId())).isTrue();
        activity.setCreator(null);
        activity.setHosts(List.of(user));
        assertThat(securityService.userHasActivityAccess(activity.getId())).isTrue();

    }

    @Test
    public void testUserHasActivityAccessFalseForNonCreatorAndHost(){
        when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));
        assertThat(securityService.userHasActivityAccess(activity.getId())).isFalse();
        assertThat(securityService.userHasActivityAccess(activity.getId())).isFalse();
    }

    @Test
    public void testisUserReturnsTrueIfUserIdIsUser(){
        assertThat(securityService.isUser(user.getId())).isTrue();
    }


    @Test
    public void testisUserReturnsFalseIfUserIdIsNotUser(){
        assertThat(securityService.isUser(UUID.randomUUID())).isFalse();
    }

    @Test
    public void testisRegisteredUserReturnsTrueIfUserIsCreator(){
        registration.getActivity().setCreator(user);
        when(activityRepository.findById(any())).thenReturn(Optional.of(registration.getActivity()));
        lenient().when(registrationRepository.findRegistrationByUser_IdAndActivity_Id(any(), any())).thenReturn(Optional.ofNullable(registration));
        assertThat(securityService.registrationPermissions(registration.getActivity().getId(), registration.getUser().getId())).isTrue();


    }

    @Test
    public void testisRegisteredUserReturnsTrueIfUserIsAffectedUser(){
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(registration.getUser()));
        lenient().when(registrationRepository.findRegistrationByUser_IdAndActivity_Id(any(), any())).thenReturn(Optional.ofNullable(registration));
        assertThat(securityService.registrationPermissions(registration.getActivity().getId(), registration.getUser().getId())).isTrue();


    }




}
