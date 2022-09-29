package com.ntnu.gidd.service.Host;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.QActivity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Implementaion of a host service
 */
@Slf4j
@Service
public class HostServiceImpl implements HostService {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();

    /**
     * Method to get all activities a user is hosting
     * @param predicate filtering params
     * @param pageable pagination params
     * @param userId id of the user to retrieve for
     * @return list of activities
     */
    @Override
    public Page<ActivityListDto> getAll(Predicate predicate, Pageable pageable, UUID userId) {
        QActivity activity = QActivity.activity;
        predicate = ExpressionUtils.allOf(predicate, activity.hosts.any().id.eq(userId));
        return activityRepository.findAll(predicate, pageable).map(a-> modelMapper.map(a,ActivityListDto.class));

    }
    /**
     * Method to get all activities a user is hosting
     * @param predicate filtering params
     * @param pageable pagination params
     * @param email mail of the user to retrieve for
     * @return list of activities
     */
    @Override
    public Page<ActivityListDto> getAllByEmail(Predicate predicate, Pageable pageable, String email) {
        QActivity activity = QActivity.activity;
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        predicate = ExpressionUtils.allOf(predicate, activity.hosts.any().id.eq(user.getId()).or(activity.creator.id.eq(user.getId())));
        return activityRepository.findAll(predicate, pageable).map(a -> modelMapper.map(a, ActivityListDto.class));
    }

    /**
     * Method to get activity for a hosting user
     * @param userId
     * @param activityId
     * @return the activity requested
     */
    @Override
    public ActivityDto getActivityFromUser(UUID userId, UUID activityId) {
        return modelMapper.map(activityRepository.findActivityByIdAndHosts_Id(activityId, userId)
                .orElseThrow(ActivityNotFoundException::new), ActivityDto.class);

    }

    /**
     * Method to get activity for a hosting user
     * @param email
     * @param activityId
     * @return the activity requested
     */
    @Override
    public ActivityDto getActivityFromUserByEmail(String email, UUID activityId) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(activityRepository.findActivityByIdAndHosts_Id(activityId, user.getId())
                .orElseThrow(ActivityNotFoundException::new), ActivityDto.class);
        
    }

    /**
     * Get a list of hosts on a activity
     * @param id
     * @return list of users
     */
    @Override
    public List<UserListDto> getByActivityId(UUID id) {
        return activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new)
                .getHosts().stream().map(a -> modelMapper.map(a, UserListDto.class)).collect(Collectors.toList());
    }

    /**
     * Method to add a host to a activity
     * @param activityId
     * @param user
     * @return updated list of hosts
     */
    @Override
    public List<UserListDto>  addHosts(UUID activityId, UserEmailDto user) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        ArrayList<User> list = new ArrayList<>(activity.getHosts()) ;
        list.add(userRepository.findByEmail(user.getEmail()).
                orElseThrow(UserNotFoundException::new));
        activity.setHosts(list);
        return activityRepository.save(activity)
                .getHosts().stream()
                .map(a -> modelMapper.map(a, UserListDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Method to delete a host for a activity
     * @param activityId
     * @param userId
     * @return updated list of hosts
     */
    @Override
    public List<UserListDto> deleteHost(UUID activityId, UUID userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        ArrayList<User> list = new ArrayList<>(activity.getHosts()) ;
       list.remove(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));
       activity.setHosts(list);
        return activityRepository.save(activity).getHosts().stream()
                .map(a -> modelMapper.map(a, UserListDto.class))
                .collect(Collectors.toList());

    }

    /**
     * Method to delete a users as a host
     * @param activityId
     * @param userId
     * @return updated list of activities a user is hosting
     */
    @Override
    public List<ActivityListDto> deleteHostfromUser(UUID activityId, UUID userId) {
       User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ArrayList<Activity> list = new ArrayList<>(user.getActivities());
        list.remove(activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new));
        user.setActivities(list);
       return userRepository.save(user).getActivities().stream()
               .map(a -> modelMapper.map(a, ActivityListDto.class))
               .collect(Collectors.toList());
    }
    /**
     * Method to delete a users as a host
     * @param activityId
     * @param email
     * @return updated list of activities a user is hosting
     */
    @Override
    public List<ActivityListDto> deleteHostfromUserByEmail(UUID activityId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        ArrayList<Activity> list = new ArrayList<>(user.getActivities());
        list.remove(activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new));
        user.setActivities(list);
        return userRepository.save(user).getActivities().stream()
                .map(a -> modelMapper.map(a, ActivityListDto.class))
                .collect(Collectors.toList());
    }
}
