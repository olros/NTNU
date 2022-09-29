package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.*;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * RegistrationRepository extending the JpaRepository, with Registration as type and RegistrationId as id
 * Contains methods for finding a registration based on the user, activity and registrationId
 */

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, RegistrationId>, QuerydslPredicateExecutor<Registration>, QuerydslBinderCustomizer<QRegistration> {
    List<Registration>findRegistrationsByActivity_Id(UUID activityId);
    Optional<List<Registration>> findRegistrationsByUser_Id(UUID userId);
    Optional<Registration> findRegistrationByUser_IdAndActivity_Id(UUID userid, UUID activityId);
    void deleteRegistrationsByUser_IdAndActivity_Id(UUID userId, UUID activityId);
    void deleteRegistrationsByUser_Id(UUID userId);


    @Override
    default void customize(QuerydslBindings bindings, QRegistration registration) {
        bindings.bind(registration.registrationStartDateAfter).first((path, value) -> registration.activity.startDate.after(value));
        bindings.bind(registration.registrationStartDateBefore).first((path, value) -> registration.activity.startDate.before(value));
   }
}
