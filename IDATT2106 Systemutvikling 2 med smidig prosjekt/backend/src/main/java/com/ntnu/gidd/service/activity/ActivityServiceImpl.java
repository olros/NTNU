package com.ntnu.gidd.service.activity;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.geolocation.GeoLocationDto;
import com.ntnu.gidd.dto.Registration.RegistrationUserDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.NotInvitedException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.*;
import com.ntnu.gidd.model.Activity;

import com.ntnu.gidd.model.HtmlTemplate;
import com.ntnu.gidd.model.Mail;
import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.*;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.TrainingLevelRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.activity.expression.ActivityExpression;
import com.ntnu.gidd.service.ActivityImage.ActivityImageService;
import com.ntnu.gidd.service.Email.EmailService;
import com.ntnu.gidd.service.Geolocation.GeolocationService;
import com.ntnu.gidd.service.Registration.RegistrationService;
import com.ntnu.gidd.service.equipment.EquipmentService;
import com.ntnu.gidd.service.User.UserService;
import com.ntnu.gidd.service.invite.InviteService;
import com.ntnu.gidd.service.rating.ActivityLikeService;
import com.ntnu.gidd.util.TrainingLevelEnum;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;


/**
 * Implementation of activity service interface
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TrainingLevelRepository trainingLevelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GeolocationService geolocationService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    ActivityImageService activityImageService;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    InviteService inviteService;

    @Autowired
    ActivityLikeService activityLikeService;

    /**
     * Method to update a activity by id
     * @param activityId the id of the activity to update
     * @param activity the new activity info
     * @param email email of the user executing the request
     * @return a DTO of the updated activity
     */

    @Transactional
    @Override
    public ActivityDto updateActivity(UUID activityId, ActivityDto activity, String email) {
        Activity updateActivity = this.activityRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        activityRepository.flush();
        updateActivity.setTitle(activity.getTitle());
        updateActivity.setDescription(activity.getDescription());
        updateActivity.setStartDate(activity.getStartDate());
        updateActivity.setEndDate(activity.getEndDate());
        updateActivity.setSignupStart(activity.getSignupStart());
        updateActivity.setSignupEnd(activity.getSignupEnd());
        updateActivity.setCapacity(activity.getCapacity());
        if(activity.getLevel()!=null)updateActivity.setTrainingLevel(getTrainingLevel(activity.getLevel()));

        if (activity.getGeoLocation() != null)
            setGeoLocation(activity, updateActivity);
        

        if (activity.getEquipment() != null)
            setEquipment(activity, updateActivity);
         
        if(!updateActivity.isClosed() && activity.isClosed()){
          closeActivity(activity);
        }
        updateActivity.setClosed(activity.isClosed());


        if(!updateActivity.isInviteOnly() && activity.isInviteOnly()){
            inviteService.inviteBatch(activityId,
                    registrationService.getRegistratedUsersInActivity(activityId));
        }
        updateActivity.setInviteOnly(activity.isInviteOnly());

        if (activity.getImages() !=  null){
            updateActivity.setImages(activityImageService.updateActivityImage(activity.getImages(), updateActivity));

        }

        ActivityDto activityDto = modelMapper.map(this.activityRepository.save(updateActivity), ActivityDto.class);
        activityDto.setHasLiked(activityLikeService.hasLiked(email, activityDto.getId()));
        return addRegisteredAmount(activityDto);

    }

    /**
     * Helper method for updating the training level on a activity
     * @param level the training level to fetch form the database
     * @return Training level entity for the new training level
     */
    private TrainingLevel getTrainingLevel(TrainingLevelEnum level){
        return trainingLevelRepository.findTrainingLevelByLevel(level).
                orElseThrow(() -> new EntityNotFoundException("Traning level does not exist"));
    }

    /**
     * Helper method to close a activity by sending a mail to the registered users for that activity
     * @param activity the activity that is closed
     * @return boolean on whether or not the mails were sent out
     */
    public boolean closeActivity(ActivityDto activity ){
        List<RegistrationUserDto> regListDtos = registrationService.getRegistrationForActivity(activity.getId());

        try{
            for(RegistrationUserDto registrationDto : regListDtos){
                Map<String, Object> properties = new HashMap<>();
                properties.put("name", registrationDto.getUser().getFirstName() + " " + registrationDto.getUser().getSurname());
                properties.put("activity", activity.getTitle());
                properties.put("url", "https://gidd-idatt2106.web.app/");

                Mail mail = Mail.builder()
                    .from("baregidd@gmail.com")
                    .to(registrationDto.getUser().getEmail())
                    .htmlTemplate(new HtmlTemplate("activity_closed", properties))
                    .subject("Activity closed")
                    .build();
                emailService.sendEmail(mail);
            }
            return true;
        }
        catch (MessagingException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Method to get activity by id for a logged in user
     * @param id id of the activity to fetch
     * @param email email of the logged in user
     * @return the requested activity
     */
    @Override
    public ActivityDto getActivityById(UUID id, String email) {
        Activity activity = this.activityRepository.findById(id).
                orElseThrow(ActivityNotFoundException::new);
        if(activity.isInviteOnly() && !checkReadAccess(activity, email)){
            throw new NotInvitedException();
        }
        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setHasLiked(activityLikeService.hasLiked(email, activity.getId()));
        return addRegisteredAmount(activityDto);

    }
    /**
     * Method to get activity by id for a non logged in user
     * @param id id of the activity to fetch
     * @return the requested activity with limited info
     */
    @Override
    public ActivityDto getActivityById(UUID id) {
        Activity activity = this.activityRepository.findById(id).
                orElseThrow(ActivityNotFoundException::new);
        if(activity.isInviteOnly()){
            throw new NotInvitedException();
        }

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setGeoLocation(null);
        return activityDto;
    }

    /**
     * Check if you have read access to a activity
     * @param activity the activity to check
     * @param email mail of the user executing the request
     * @return boolean for if the user has access
     */
    private boolean checkReadAccess(Activity activity, String email){
        User user = userRepository.findByEmail(email).orElse(null);
        return (activity.getInvites().contains(user) | activity.getCreator().equals(user) | activity.getHosts().contains(user));
    }

    /**
     * Method to get all activities
     * @param predicate filtering params for the request
     * @param pageable pagination params for the request
     * @param email mail of the user executing the request
     * @return a paginated list of activities
     */
    @Override
    public Page<ActivityListDto> getActivities(Predicate predicate, Pageable pageable, String email) {
        assert predicate != null;
        Page<ActivityListDto> activities = this.activityRepository.findAll(predicate, pageable)
                .map(s -> modelMapper.map(s, ActivityListDto.class));

        return activityLikeService.checkListLikes(activities,email);

    }

    /**
     * Method to get all activities with geolocation filter
     * @param predicate filtering params for the request
     * @param pageable pagination params for the request
     * @param position the position of to filter on
     * @param range radius for the location filter
     * @param email email of the logged in user
     * @return a paginated list of activities
     */
    @Override
    public Page<ActivityListDto> getActivities(Predicate predicate, Pageable pageable, GeoLocation position, Double range,  String email) {
        
        predicate = ActivityExpression.of(predicate)
                .closestTo(position)
                .range(range)
                .toPredicate();
        return getActivities(predicate, pageable, email);
    }

    /**
     * Method to create a activity
     * @param activity information about the activity
     * @param creatorEmail the mail of the user that executed the create request
     * @return the activity that was created
     */
    @Override
    public ActivityDto saveActivity(ActivityDto activity, String creatorEmail) {
        Activity newActivity = modelMapper.map(activity, Activity.class);
        User user = userRepository.findByEmail(creatorEmail).orElseThrow(UserNotFoundException::new);
        newActivity.setId(UUID.randomUUID());
        newActivity.setCreator(user);
        newActivity.setHosts(List.of());
        if(activity.getLevel()!= null)newActivity.setTrainingLevel(getTrainingLevel(activity.getLevel()));

        if (activity.getGeoLocation() != null)
            setGeoLocation(activity, newActivity);

        if (activity.getEquipment() != null)
            setEquipment(activity, newActivity);

        if (activity.getImages() !=  null) newActivity.setImages(activityImageService.saveActivityImage(
                newActivity.getImages(), newActivity
        ));
        newActivity  = this.activityRepository.save(newActivity);

        ActivityDto activityDto = modelMapper.map(newActivity, ActivityDto.class);
        return addRegisteredAmount(activityDto);
    }

    /**
     * Helper method to update the geolocation on a activity
     * @param activity the new information to update the activity with
     * @param newActivity the activity to update
     */
    private void setGeoLocation(ActivityDto activity, Activity newActivity) {
        GeoLocationDto geoLocationDto = geolocationService.findOrCreate(activity.getGeoLocation().getLat(), activity.getGeoLocation().getLng());
        GeoLocation geoLocation = new GeoLocation(geoLocationDto
                                                          .getLat(), activity.getGeoLocation()
                .getLng());

        newActivity.setGeoLocation(geoLocation);
    }

    /**
     * Helper method to update the equipment on a activity
     * @param activity the new information to update the activity with
     * @param newActivity the activity to update
     */
    private void setEquipment(ActivityDto activity, Activity newActivity){
        newActivity.setEquipment(equipmentService.saveAndReturnEquipments(activity.getEquipment()));
    }

    /**
     * Method to delete a activity by id
     * @param id of the activity to delete
     */
    @Override
    public void deleteActivity(UUID id){
        Activity activity = activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        this.activityRepository.delete(activity);
    }

    /**
     * Method to get all activities a user has liked
     * @param predicate filtering params for the request
     * @param pageable pagination params for the request
     * @param email mail of the user executing the request
     * @return a paginated list of activities
     */
    public Page<ActivityListDto> getLikedActivities(Predicate predicate, Pageable pageable, String email){
        QActivity activity = QActivity.activity;
        predicate = ExpressionUtils.allOf(predicate, activity.likes.any().email.eq(email));
        Page<ActivityListDto> activities = this.activityRepository.findAll(predicate, pageable)
                .map(s -> modelMapper.map(s, ActivityListDto.class));

        return activityLikeService.checkListLikes(activities,email);
    }
    /**
     * Method to get all activities a user has liked
     * @param predicate filtering params for the request
     * @param pageable pagination params for the request
     * @param id id of the user executing the request
     * @return a paginated list of activities
     */
    public Page<ActivityListDto> getLikedActivities(Predicate predicate, Pageable pageable, UUID id){
        QActivity activity = QActivity.activity;
        predicate = ExpressionUtils.allOf(predicate, activity.likes.any().id.eq(id));
        Page<ActivityListDto> activities = this.activityRepository.findAll(predicate, pageable)
                .map(s -> modelMapper.map(s, ActivityListDto.class));

        return activityLikeService.checkListLikes(activities,id);
    }

    /**
     * Helper method to add registration amount to a activity
     * @param dto the activity to check
     * @return updated activity
     */
    private ActivityDto addRegisteredAmount(ActivityDto dto){
        dto.setRegistered(registrationService.getRegistratedUsersInActivity(dto.getId()).size());
        return dto;
    }
}


