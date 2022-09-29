package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.QUser;
import com.ntnu.gidd.model.User;
import com.querydsl.core.BooleanBuilder;
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

@Repository
public interface UserRepository extends JpaRepository<User, UUID> , QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    Page<User> findUserByInvites(Activity activity, Pageable pageable);
    Optional<User> findByEmail(String email);
    Page<User> findByFollowersId(UUID id, Pageable pageable);
    Page<User> findByFollowingId(UUID id, Pageable pageable);

    @Override
    default void customize(QuerydslBindings bindings, QUser user) {
        bindings.bind(user.search).first(((path, value) -> {
            BooleanBuilder predicate = new BooleanBuilder();
            List<String> searchWords = Arrays.asList(value.trim().split("\\s+"));
            searchWords.forEach(searchWord -> predicate
                    .or(user.firstName.containsIgnoreCase(searchWord))
                    .or(user.surname.containsIgnoreCase(searchWord)));

            return predicate;
        }));
    }
}
