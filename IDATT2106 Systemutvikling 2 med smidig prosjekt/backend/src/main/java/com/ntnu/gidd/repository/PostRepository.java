package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.QActivity;
import com.ntnu.gidd.model.QPost;
import com.ntnu.gidd.model.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface PostRepository  extends JpaRepository<Post, UUID> , QuerydslPredicateExecutor<Post>{
    void deletePostsByCreator(User creator);



}
