package com.ntnu.gidd.repository;


import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.QActivity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ActivityRepository extending JpaRepository with Activity and UUID as id
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID>, QuerydslPredicateExecutor<Activity>, QuerydslBinderCustomizer<QActivity> {
    Optional<Activity> findActivityByIdAndHosts_Id(UUID id, UUID hosts_id);

    @Override
    default void customize(QuerydslBindings bindings, QActivity activity) {
        bindings.bind(activity.title).first(StringExpression::contains);
        bindings.bind(activity.startDateAfter).first((path, value) -> activity.startDate.after(value));
        bindings.bind(activity.startDateBefore).first((path, value) -> activity.startDate.before(value));
        bindings.bind(activity.search).first(((path, value) -> {
            BooleanBuilder predicate = new BooleanBuilder();
            List<String> searchWords = Arrays.asList(value.trim().split("\\s+"));
            searchWords.forEach(searchWord -> predicate
                    .or(activity.title.containsIgnoreCase(searchWord))
                    .or(activity.description.containsIgnoreCase(searchWord)));

            return predicate;
        }));
    }
}
