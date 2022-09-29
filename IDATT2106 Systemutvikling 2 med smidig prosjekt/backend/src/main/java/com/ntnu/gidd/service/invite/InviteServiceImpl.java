package com.ntnu.gidd.service.invite;

import com.ntnu.gidd.dto.User.UserEmailDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.InvalidUnInviteException;
import com.ntnu.gidd.exception.RegistrationNotFoundException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.Registration.RegistrationService;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of a invite service
 */
@Service
@AllArgsConstructor
@Slf4j
public class InviteServiceImpl  implements InviteService{

    UserRepository userRepository;

    ActivityRepository activityRepository;

    ModelMapper modelMapper;

    RegistrationService registrationService;

    /**
     * Method to invite a user to an activity
     * @param predicate
     * @param pageable
     * @param activityId
     * @param user
     * @return updated lis tof invited users
     */
    @Override
    public Page<UserListDto> inviteUser(Predicate predicate, Pageable pageable, UUID activityId, UserEmailDto user) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        List<User> invites = new ArrayList<>();
        invites.addAll(activity.getInvites());
        invites.add(userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new));
        activity.setInvites(invites);
        activity = activityRepository.save(activity);
        return userRepository.findUserByInvites(activity, pageable).map(s-> modelMapper.map(s, UserListDto.class));
    }

    /**
     * Method to univte a users to an activity
     * @param predicate
     * @param pageable
     * @param activityId
     * @param userId
     * @return updated lis tof invited users
     */
    @Override
    public Page<UserListDto> unInviteUser(Predicate predicate, Pageable pageable, UUID activityId, UUID userId) {

        try{
            if(registrationService.getRegistrationWithCompositeId(userId,activityId) != null){
                log.debug("[x] Request to uninvite a user that is registered");
                throw new InvalidUnInviteException();
            }
        }catch (RegistrationNotFoundException | ActivityNotFoundException | UserNotFoundException ignored){}

        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        List<User> invites = new ArrayList<>();
        invites.addAll(activity.getInvites());
        invites.remove(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
        activity.setInvites(invites);
        activity = activityRepository.save(activity);
        return userRepository.findUserByInvites(activity, pageable).map(s-> modelMapper.map(s, UserListDto.class));
    }

    /**
     * Method to invite a list of users
     * @param activityId
     * @param users
     * @return true/false
     */
    @Override
    public boolean inviteBatch(UUID activityId, List<User> users) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        List<User> invites = new ArrayList<>();
        if(activity.getInvites().size() != 0) invites.addAll(activity.getInvites());
        invites.addAll(users);
        activity.setInvites(invites);
        activityRepository.saveAndFlush(activity);
        return true;
    }

    /**
     * Method to get all ivited users on an activity
     * @param predicate
     * @param pageable
     * @param activityId
     * @return list of invited users
     */
    @Override
    public Page<UserListDto> getAllInvites(Predicate predicate, Pageable pageable, UUID activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        return userRepository.findUserByInvites(activity, pageable).map(s -> modelMapper.map(s, UserListDto.class));
    }


}
