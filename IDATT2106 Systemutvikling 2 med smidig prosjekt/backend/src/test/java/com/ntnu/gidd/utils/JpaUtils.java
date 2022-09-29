package com.ntnu.gidd.utils;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class JpaUtils {

    public static Predicate getEmptyPredicate() {
        return Expressions.asBoolean(true).isTrue();
    }

    public static Pageable getDefaultPageable() {
        return PageRequest.of(0, 25);
    }
}
