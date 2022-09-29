package com.ntnu.gidd.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(of={"email"}, callSuper = true)
public class User extends UUIDModel {
    
    private String firstName;
    private String surname;
    @Column(unique = true)
    private String email;
    private LocalDate birthDate;
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "hosts", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_id"}))
    private List<Activity> activities;

    @OneToOne
    @JoinColumn(name = "traning_level_id", referencedColumnName = "id")
    private TrainingLevel trainingLevel;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "invites", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_id"}))
    private List<Activity> invites;
    @OneToOne
    private PasswordResetToken resetToken;

    String image;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, insertable = false)
    private List<Comment> comments;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id", nullable = false, insertable = false)
    private List<Post> post;


    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinTable(name = "follower",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following = new ArrayList<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
    private List<User> followers = new ArrayList<>();


    @Transient
    @QueryType(PropertyType.STRING)
    private String search;
    
    public void addFollowing(User userToFollow) {
        if (following == null)
            following = new ArrayList<>();

        following.add(userToFollow);
        userToFollow.addFollower(this);
    }

    private void addFollower(User follower) {
        if (followers == null)
            followers = new ArrayList<>();

        followers.add(follower);
    }
    private void clearPosts(){
        if(post != null)
            post.clear();
    }

    public void removeFollowing(User unfollowed) {
        following.remove(unfollowed);
        unfollowed.removeFollower(this);
    }

    private void removeFollower(User follower) {
        followers.remove(follower);
    }

    @Transient
    public boolean getCurrentUserIsFollowing() {
        return false;
    }

    @Transient
    public Integer getFollowingCount() {
        return following.size();
    }

    @Transient
    public Integer getFollowerCount() {
        return followers.size();
    }

    @PreRemove
    public void removeRelationships(){
        removeComments();
        removeInvites();
        removeActivitiesFromUsers();
        removeComments();
        clearPosts();

    }

    private void removeComments(){
        if(comments != null)
            comments.clear();
    }
    private void removeActivitiesFromUsers() {
        if(activities != null)
           activities.clear();
    }

    private void removeInvites(){
        if(invites != null)
            invites.clear();
    }


}
