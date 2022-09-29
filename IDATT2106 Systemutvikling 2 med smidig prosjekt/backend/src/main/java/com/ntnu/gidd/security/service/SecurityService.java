package com.ntnu.gidd.security.service;


import com.ntnu.gidd.model.*;
import com.ntnu.gidd.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SecurityService {
    ActivityRepository activityRepository;

    UserRepository userRepository;

    RegistrationRepository registrationRepository;

    CommentRepository commentRepository;

    PostRepository postRepository;


    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = authentication != null ? (UserDetails) authentication.getPrincipal() : null;
        String email = userDetails != null ? userDetails.getUsername() : "";
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean hasActivityAccess(Activity activity, User user){
        return  (activity.getHosts() != null &&activity.getHosts().contains(user))
                || (activity.getCreator() != null && activity.getCreator().equals(user));

    }
    public  boolean userHasActivityAccess(UUID activityId){
        Activity activity = activityRepository.findById(activityId).orElse(null);
        User user = getUser();
        if(activity != null && user != null ){
            return hasActivityAccess(activity,user);
        }
        return false;

    }

    public boolean isCreator(UUID activityId){
        Activity activity = activityRepository.findById(activityId).orElse(null);
        User user = getUser();
        if(activity != null && user != null  && activity.getCreator() != null){
            return activity.getCreator().equals(user);
        }
        return false;
    }
    public boolean isUser(UUID userId){
        User user = getUser();
        return user != null && user.getId().equals(userId);
    }

    public boolean isRegisteredUser(UUID activityId, UUID userId){
        User user = getUser();
        Registration registration = registrationRepository.findRegistrationByUser_IdAndActivity_Id(userId, activityId).orElse(null);
        return registration != null && registration.getUser().equals(user);
    }

    public boolean isOwnComment(UUID commentId){
        Comment comment = commentRepository.findById(commentId).orElse(null);
        User user = getUser();
        if(comment != null && user != null){
            return comment.getUser().equals(user);
        }
        return false;
    }

    private boolean isOwnPost(UUID postId){
        Post post = postRepository.findById(postId).orElse(null);
        User user = getUser();
        if(post != null && user != null){
            return post.getCreator().equals(user);
        }
        return false;
    }
    public boolean registrationPermissions(UUID activityId, UUID userId){
        return isCreator(activityId) || isRegisteredUser(activityId, userId);
    }

    public boolean commentPermissions(UUID activityId, UUID commentId){
        return userHasActivityAccess(activityId)|| isOwnComment(commentId);
    }

    public boolean postCommentPermissions(UUID postId, UUID commentId){
        return postPermissions(postId)|| isOwnComment(commentId);
    }

    public boolean postPermissions(UUID postId){
        return isOwnPost(postId);
    }



}
