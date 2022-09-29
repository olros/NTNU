package com.ntnu.gidd.service;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.EquipmentDto;
import com.ntnu.gidd.dto.Registration.RegistrationUserDto;
import com.ntnu.gidd.dto.geolocation.GeoLocationDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.EquipmentListFactory;
import com.ntnu.gidd.factories.RegistrationFactory;
import com.ntnu.gidd.model.*;
import com.ntnu.gidd.repository.*;
import com.ntnu.gidd.service.Email.EmailService;
import com.ntnu.gidd.service.Geolocation.GeolocationService;
import com.ntnu.gidd.service.Registration.RegistrationService;
import com.ntnu.gidd.service.activity.ActivityServiceImpl;
import com.ntnu.gidd.service.equipment.EquipmentService;
import com.ntnu.gidd.service.rating.ActivityLikeService;
import com.ntnu.gidd.utils.JpaUtils;
import com.ntnu.gidd.utils.StringRandomizer;
import com.querydsl.core.types.Predicate;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceImplTest {

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TrainingLevelRepository trainingLevelRepository;

    @Mock
    private RegistrationService registrationService;
    
    @Mock
    private GeolocationService geolocationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ActivityLikeService activityLikeService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentService equipmentService;

    ModelMapper modelMapper = new ModelMapper();

    private Activity activity;
    private Predicate predicate;
    private Pageable pageable;
    private Registration registration;

    @BeforeEach
    public void setUp() throws Exception {
        activity = new ActivityFactory().getObject();
        assert activity != null;
        lenient().when(activityRepository.save(activity)).thenReturn(activity);
        predicate = JpaUtils.getEmptyPredicate();
        pageable = JpaUtils.getDefaultPageable();

        registration = new RegistrationFactory().getObject();
        registration.getUser().setEmail("baregidd@gmail.com");
        lenient().when(userRepository.save(registration.getUser())).thenReturn(registration.getUser());
        lenient().when(activityRepository.save(registration.getActivity())).thenReturn(registration.getActivity());
        lenient().when(registrationRepository.save(registration)).thenReturn(registration);
        lenient().doNothing().when(emailService).sendEmail(any(Mail.class));
        lenient().when(activityLikeService.hasLiked(any(String.class), any(UUID.class))).thenReturn(false);
        lenient().when(geolocationService.findOrCreate(any(Double.class), any(Double.class))).thenReturn(new GeoLocationDto());
        lenient().when(activityLikeService.hasLiked(any(String.class), any())).thenReturn(false);

    }

    @Test
    void testActivityServiceImplCloseActivityAndReturnsTrue() {
        List<RegistrationUserDto> regListUserDtos = Collections.singletonList(modelMapper.map(registration, RegistrationUserDto.class));
        Mockito.doReturn(regListUserDtos).when(registrationService).getRegistrationForActivity(any(UUID.class));

        ActivityDto activity = modelMapper.map(registration.getActivity(), ActivityDto.class);
        assertTrue(activityService.closeActivity(activity));
    }

    @Test
    void testActivityServiceImplUpdateActivityAndReturnsUpdatedActivity() throws Exception {
        TrainingLevel level = activity.getTrainingLevel();
        when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));
        when(trainingLevelRepository.findTrainingLevelByLevel(level.getLevel())).thenReturn(Optional.of(level));
        List<Equipment> equipments = new EquipmentListFactory().getObject();
        activity.setTitle(StringRandomizer.getRandomString(10));
        activity.setEquipment(equipments);

        List<EquipmentDto> equipmentDtos = equipments.stream()
                .map(s -> modelMapper.map(s, EquipmentDto.class))
                .collect(Collectors.toList());

        when(equipmentService.saveAndReturnEquipments(equipmentDtos)).thenReturn(equipments);

        ActivityDto updateActivity = activityService.updateActivity(activity.getId(),modelMapper.map(activity,ActivityDto.class), registration.getUser().getEmail());

        assertThat(activity.getId()).isEqualTo(updateActivity.getId());

        assertThat(activity.getTitle()).isEqualTo(updateActivity.getTitle());
        assertThat(activity.getDescription()).isEqualTo(updateActivity.getDescription());
        assertThat(activity.getCapacity()).isEqualTo(updateActivity.getCapacity());
        assertThat(activity.getEquipment()).isEqualTo(equipments);
        assertThat(activity.getEquipment()).isNotNull();
    }

    @Test
    void testActivityServiceImplGetActivityByIdReturnsActivity() {
        when(activityRepository.findById(activity.getId())).thenReturn(Optional.ofNullable(activity));

        ActivityDto activityFound = activityService.getActivityById(activity.getId(), "");

        assertThat(activityFound.getId()).isEqualTo(activity.getId());
    }

    @Test
    void testActivityServiceImplGetActivitiesReturnsActivities() throws Exception{

        Activity secondActivity = new ActivityFactory().getObject();
        assert secondActivity != null;
        List<Activity> testList = List.of(activity, secondActivity);
        Page<Activity> activities = new PageImpl<>(testList, pageable, testList.size());

        lenient().when(activityRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(activities);
        lenient().when(activityLikeService.checkListLikes(any(), any(String.class)))
                .thenReturn(activities.map(s -> modelMapper.map(s , ActivityListDto.class)));
        Page<ActivityListDto> getActivities = activityService.getActivities(predicate, pageable, "");

        for (int i = 0; i < activities.getContent().size(); i++){
            assertThat(activities.getContent().get(i).getTitle()).isEqualTo(getActivities.getContent().get(i).getTitle());
        }
    }

    @Test
    void testActivityServiceImplGetActivitiesReturnsActivitiesWithGeoLocation() throws Exception{

        Activity secondActivity = new ActivityFactory().getObject();
        assert secondActivity != null;
        List<Activity> testList = List.of(activity, secondActivity);
        Page<Activity> activities = new PageImpl<>(testList, pageable, testList.size());

        lenient().when(activityRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(activities);
        lenient().when(activityLikeService.checkListLikes(any(), any(String.class)))
                .thenReturn(activities.map(s -> modelMapper.map(s , ActivityListDto.class)));
        GeoLocation position = new GeoLocation(0.0, 0.0);
        Page<ActivityListDto> getActivities = activityService.getActivities(predicate, pageable, position,
                                                                            1.0,"");

        for (int i = 0; i < activities.getContent().size(); i++){
            assertThat(activities.getContent().get(i).getTitle()).isEqualTo(getActivities.getContent().get(i).getTitle());
        }
    }

}
