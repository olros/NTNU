package com.ntnu.gidd.service.Registration;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.Registration.RegistrationUserDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.RegistrationNotFoundException;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.exception.*;
import com.ntnu.gidd.model.*;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.RegistrationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ntnu.gidd.repository.UserRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * RegistrationServiceImpl class for services for the RegistrationRepository
 */
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

  RegistrationRepository registrationRepository;

  UserRepository userRepository;

  ActivityRepository activityRepository;

  ModelMapper modelMapper;

  @Override
  public RegistrationUserDto saveRegistration(UUID user_id, UUID activity_id){
    User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
    Activity activity = activityRepository.findById(activity_id).orElseThrow(ActivityNotFoundException::new);
    if(activity.isClosed())throw new ActivityClosedExecption();
    if(activity.getCapacity() <= registrationRepository.findRegistrationsByActivity_Id(activity_id).size()) throw new ActivityFullExecption();
    Registration registration = registrationRepository.save(new Registration(new RegistrationId(user_id, activity_id), user, activity));
    return modelMapper.map(registration, RegistrationUserDto.class);
  }

  /**
   * Finds all registration for a given activity
   *
   * @param predicate
   * @param pageable
   * @param activity_id
   * @return Page of registration or throws Exception
   */
  @Override
  public Page<RegistrationUserDto> getRegistrationForActivity(Predicate predicate, Pageable pageable, UUID activity_id){
    QRegistration registration = QRegistration.registration;
      predicate = ExpressionUtils.allOf(predicate, registration.activity.id.eq(activity_id));
      Page<Registration> registrations = registrationRepository.findAll(predicate, pageable);
      return registrations.map(p -> modelMapper.map(p, RegistrationUserDto.class));
  }

  /**
   * Finds all user registered for a given activity
   * @param activity_id
   * @return List of users
   */

  @Override
  public List<User> getRegistratedUsersInActivity(UUID activity_id) {
    return registrationRepository.findRegistrationsByActivity_Id(activity_id)
            .stream().map(s->s.getUser()).collect(Collectors.toList());
  }


  /**
   * Finds all registration for a given user by its email
   *
   * @param predicate
   * @param pageable
   * @param username
   * @return Page of registration or throws Exception
   */

  @Override
  public Page<ActivityListDto> getRegistrationWithUsername(Predicate predicate, Pageable pageable, String username) {
    User user = userRepository.findByEmail(username)
            .orElseThrow(UserNotFoundException::new);

    QRegistration registration = QRegistration.registration;
    predicate = ExpressionUtils.allOf(registration.user.id.eq(user.getId()).and(predicate));

    Page<Registration> registrations = registrationRepository.findAll(predicate, pageable);
    return registrations.map(p -> modelMapper.map(p, ActivityListDto.class));
  }

  /**
   * Finds registration with composite id
   * @param user_id
   * @param activity_id
   * @return registration or throws exception
   */
  @Override
  public RegistrationUserDto getRegistrationWithCompositeId(UUID user_id, UUID activity_id) {
    Registration registration = registrationRepository.findRegistrationByUser_IdAndActivity_Id(user_id, activity_id)
            .orElseThrow(RegistrationNotFoundException::new);
    return modelMapper.map(registration, RegistrationUserDto.class);
  }

  @Override
  public ActivityDto getRegistrationWithUsernameAndActivityId(String username, UUID activity_id) {
    User user = userRepository.findByEmail(username)
            .orElseThrow(UserNotFoundException::new);
    Registration registration = registrationRepository.findRegistrationByUser_IdAndActivity_Id(user.getId(), activity_id)
            .orElseThrow(RegistrationNotFoundException::new);
    return modelMapper.map(registration, ActivityDto.class);
  }

  /**
   * Find the registration with the corresponding registration id
   * @param id
   * @return Registration or throws exception
   */
  @Override
  public RegistrationUserDto getRegistrationWithRegistrationId(RegistrationId id) {
    Registration registration = registrationRepository.findById(id).
        orElseThrow(RegistrationNotFoundException::new);
    return modelMapper.map(registration, RegistrationUserDto.class);
  }

  /**
   * Find the registration with the registration id
   * Deletes the registration
   * @param user_id, activity_id
   * @return deletion or throws exception
   */
  @Override
  @Transactional
  public void deleteRegistrationWithCompositeId(UUID user_id, UUID activity_id) {
    registrationRepository.findRegistrationByUser_IdAndActivity_Id(user_id, activity_id)
            .orElseThrow(RegistrationNotFoundException::new);
    registrationRepository.deleteRegistrationsByUser_IdAndActivity_Id(user_id, activity_id);
  }

  /**
   * Find the registration with the users id and the activity id
   *  Deletes the registration
   * @param username, activity_id
   * @return deletion or throws exception
   */

  @Override
  public void deleteRegistrationWithUsernameAndActivityId(String username, UUID activity_id) {
    User user = userRepository.findByEmail(username).
            orElseThrow(UserNotFoundException::new);
    deleteRegistrationWithCompositeId(user.getId(), activity_id);
  }

  @Override
  public void deleteAllRegistrationsWithUsername(String username) {
    User user = userRepository.findByEmail(username).
            orElseThrow(UserNotFoundException::new);
    registrationRepository.deleteRegistrationsByUser_Id(user.getId());
  }

  @Override
  public List<RegistrationUserDto> getRegistrationForActivity(UUID activity_id){
    List<Registration> registrations = registrationRepository.findRegistrationsByActivity_Id(activity_id);
    return registrations.stream().map(p -> modelMapper.map(p, RegistrationUserDto.class)).collect(Collectors.toList());
  }

  @Override
  public Page<ActivityListDto> getRegistrationsForUser(Predicate predicate, Pageable pageable, UUID userId){
    QRegistration registration = QRegistration.registration;
    predicate = ExpressionUtils.allOf(predicate, registration.user.id.eq(userId));
    Page<Registration> registrations = registrationRepository.findAll(predicate, pageable);
    return registrations.map(s -> modelMapper.map(s, ActivityListDto.class));
  }
}
