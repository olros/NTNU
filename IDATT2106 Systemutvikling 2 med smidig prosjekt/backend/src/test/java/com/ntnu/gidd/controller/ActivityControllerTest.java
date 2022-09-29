package com.ntnu.gidd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.EquipmentListFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.*;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.GeoLocation;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.EquipmentRepository;
import com.ntnu.gidd.repository.GeoLocationRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.service.traininglevel.TrainingLevelService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivityControllerTest {

    private final String URI = "/activities/";
    private final String TITLE = "Test activity";

    @Autowired
    private MockMvc mvc;

    private ActivityFactory activityFactory = new ActivityFactory();

    private UserFactory userFactory = new UserFactory();

    @Autowired
    private ActivityRepository  activityRepository;

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private TrainingLevelService trainingLevelService;

    @Autowired
    private ObjectMapper objectMapper;

    private Activity activity;

    @BeforeEach
    public void setUp() throws Exception {
        activity = activityFactory.getObject();
        assert activity != null;
        activity.setTitle(TITLE);
        activity = activityRepository.save(activity);
    }

    @AfterEach
    public void cleanUp(){
        activityRepository.deleteAll();
    }

    @Test
    public void testActivityControllerGetAllReturnsOKAndAListOfActivities() throws Exception {
        this.mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].title",hasItem(activity.getTitle())));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testActivityControllerGetReturnsOKAndTheAuthor () throws Exception {
        this.mvc.perform(get(URI+activity.getId().toString()+"/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(activity.getTitle()))
                .andExpect(jsonPath("$.description").value(activity.getDescription()));
    }


    @WithMockUser(value = "spring")
    @Test
    public void testActivityControllerSaveReturn201ok() throws Exception {

        Activity testActivity = activityFactory.getObject();
        User user = userFactory.getObject();
        assert user !=null;
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        assert testActivity != null;
        this.mvc.perform(post(URI).with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testActivity)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(testActivity.getTitle()))
            .andExpect(jsonPath("$.creator.firstName").value(user.getFirstName()))
            .andExpect(jsonPath("$.hosts").isArray());

    }

    @WithMockUser(value = "spring")
    @Test
    public void testCreateActivityWithUnsavedGeoLocationSavesActivityWithCreatedGeolocation() throws Exception {
        Activity activity = activityFactory.getObject();
        User user = userFactory.getObject();
        assert user != null;
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();

        GeoLocation expectedGeoLocation = activity.getGeoLocation();

        this.mvc.perform(post(URI).with(user(userDetails))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.geoLocation.lat").value(expectedGeoLocation.getLat()))
                .andExpect(jsonPath("$.geoLocation.lng").value(expectedGeoLocation.getLng()));

        assert geoLocationRepository.existsById(expectedGeoLocation.getId());
    }

    @WithMockUser(value = "spring")
    @Test
    public void testCreateActivityWithSavedGeoLocationSavesActivityWithExistingGeoLocation() throws Exception {
        Activity activity = activityFactory.getObject();
        User user = userFactory.getObject();
        assert user != null;
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();

        GeoLocation expectedGeoLocation = activity.getGeoLocation();
        geoLocationRepository.save(expectedGeoLocation);

        this.mvc.perform(post(URI).with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.geoLocation.lat").value(expectedGeoLocation.getLat()))
            .andExpect(jsonPath("$.geoLocation.lng").value(expectedGeoLocation.getLng()));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testCreateActivityWithSavedEquipmentSavesActivityWithExistingEquipment() throws Exception{
        Activity activity = activityFactory.getObject();
        User user = userFactory.getObject();
        userRepository.save(user);
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        activity.setEquipment(new EquipmentListFactory().getObject());
        List<Equipment> expectedEquipment = activity.getEquipment();
        equipmentRepository.saveAll(expectedEquipment);

        //EquipmentListFactory creates a random list of equipment with 2 <= size <= 5
        //Therefore we compare the first 2 items in equipmentlist
        
        this.mvc.perform(post(URI).with(user(userDetails))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(activity)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.equipment.length()").value(expectedEquipment.size()))
        .andExpect(jsonPath("$.equipment.[*].name", hasItem(expectedEquipment.get(0).getName())))
        .andExpect(jsonPath("$.equipment.[*].name", hasItem(expectedEquipment.get(1).getName())));
    }

    @WithMockUser(value = "spring")
    @Test
    public void testActivityControllerDeleteActivityAndReturnsOk() throws Exception {

        Activity testActivity = activityFactory.getObject();
        assert testActivity != null;
        User user = userRepository.save(userFactory.getObject());
        UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
        testActivity.setCreator(user);
        testActivity = activityRepository.save(testActivity);

        this.mvc.perform(delete(URI + testActivity.getId().toString() + "/")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Activity has been deleted"));

    }

    @Test
    public void testActivityControllerFiltersOnWantedFieldsForTitle() throws Exception {
        Activity dummy = activityFactory.getObject();
        assert dummy != null;
        dummy.setTitle("Dummy title");
        activityRepository.save(dummy);

        this.mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("title", activity.getTitle()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
    }

    @Test
    public void testFilterActivitiesPartialTitleReturnsCorrectResults() throws Exception {
        Activity dummy = activityFactory.getObject();
        assert dummy != null;
        dummy.setTitle("Dummy title");
        activityRepository.save(dummy);
        mvc.perform(get(URI)
                            .accept(MediaType.APPLICATION_JSON)
                .param("title", TITLE.substring(0, TITLE.length() - 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
    }

    @Test
    public void testFilterActivitiesByStartDateAfterReturnActivitiesStartingAfterGivenDateTime() throws Exception {
        Activity dummy = activityFactory.getObject();
        dummy.setStartDate(ZonedDateTime.now().minusDays(100));
        activityRepository.save(dummy);


        mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("startDateAfter", String.valueOf(activity.getStartDate().minusHours(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
    }

    @Test
    public void testFilterActivitiesByStartDateBeforeReturnActivitiesStartingBeforeGivenDateTime() throws Exception {
        Activity expectedActivity = activityFactory.getObject();
        expectedActivity.setStartDate(ZonedDateTime.now().minusDays(100));
        activityRepository.save(expectedActivity);

        mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("startDateBefore", String.valueOf(activity.getStartDate().minusHours(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(expectedActivity.getTitle()));
    }


    @Test
    public void testFilterActivitiesByStartDateBetweenReturnActivitiesStartingInRange() throws Exception {
        Activity dummy = activityFactory.getObject();
        dummy.setStartDate(ZonedDateTime.now().minusDays(100));
        activityRepository.save(dummy);

        Activity between = activityFactory.getObject();
        between.setStartDate(ZonedDateTime.now().minusDays(50));
        activityRepository.save(between);

        // Due to decimal precision these where sometimes rounded down/up
        // causing the test to fail spontaneously

        mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("startDateAfter", String.valueOf(dummy.getStartDate().plusHours(1)))
                .param("startDateBefore", String.valueOf(activity.getStartDate().minusHours(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(between.getTitle()));
    }

    // TODO: test with multiple levels
    @Test
    public void testFilterByActivityTrainingLevelReturnsActivitiesWithGivenTrainingLevel() throws Exception {

        TrainingLevel mediumTrainingLevel = trainingLevelService.getTrainingLevelMedium();
        activity.setTrainingLevel(mediumTrainingLevel);
        activityRepository.save(activity);

        TrainingLevel lowTrainingLevel = trainingLevelService.getTrainingLevelLow();
        Activity dummy = activityFactory.getObject();
        dummy.setTrainingLevel(lowTrainingLevel);
        activityRepository.save(dummy);


        mvc.perform(get(URI).accept(MediaType.APPLICATION_JSON)
                .param("trainingLevel.level", String.valueOf(mediumTrainingLevel.getLevel())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].title").value(activity.getTitle()));
    }

    @ParameterizedTest
    @MethodSource("rangeProvider")
    public void testGetClosestActivitiesReturnsAllActivitiesWithinGivenRange(String range, int expectedNumberOfElements) throws Exception {

        Activity closestActivity = new ActivityFactory().getObject();
        Activity furthesAwayActivity = new ActivityFactory().getObject();
        GeoLocation origin = new GeoLocation(0.0, 0.0);
        GeoLocation closest = new GeoLocation(1.0, 1.0);
        GeoLocation furthestAway = new GeoLocation(100.0, 100.0);

        geoLocationRepository.saveAll(List.of(origin, closest, furthestAway));

        activity.setGeoLocation(origin);
        closestActivity.setGeoLocation(closest);
        furthesAwayActivity.setGeoLocation(furthestAway);

        activityRepository.saveAll(List.of(activity, closestActivity, furthesAwayActivity));

        this.mvc.perform(get(URI)
                                 .param("lat", String.valueOf(origin.getLat()))
                                 .param("lng", String.valueOf(origin.getLng()))
                                 .param("range", range) 
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(expectedNumberOfElements));
    }

    private static Stream<Arguments> rangeProvider() {
        return Stream.of(
                Arguments.of("100.0", 1),
                Arguments.of("200.0", 2),
                Arguments.of("11000.0", 3)
        );
    }


}
