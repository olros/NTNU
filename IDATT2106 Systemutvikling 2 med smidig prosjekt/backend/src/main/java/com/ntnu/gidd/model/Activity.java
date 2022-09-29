package com.ntnu.gidd.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@NoArgsConstructor
@SuperBuilder
@Table(name = "activity")
@EqualsAndHashCode(callSuper = true)
public class Activity extends UUIDModel {

  @NotNull
  private String title;
  @NotNull
  @Column(columnDefinition = "TEXT")
  private String description;
  @NotNull
  private ZonedDateTime startDate;
  @NotNull
  private ZonedDateTime endDate;
  @NotNull
  private ZonedDateTime signupStart;
  @NotNull
  private ZonedDateTime signupEnd;
  @NotNull
  private boolean closed;
  @OneToOne
  @JoinColumn(name = "traning_level_id", referencedColumnName = "id")
  private TrainingLevel trainingLevel;
  @OneToOne
  @JoinColumn(name = "creator_id", referencedColumnName = "id")
  private User creator;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(name = "hosts", joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "user_id"}))
  private List<User> hosts;

  private int capacity;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false, insertable = false)
    private List<ActivityImage> images  = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Equipment> equipment;

  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
  @JoinColumn(name = "activity_id", nullable = false, insertable = false)
  private List<Comment> comments;

    @ManyToOne(cascade = CascadeType.MERGE,  fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name="lat", referencedColumnName="lat"),
            @JoinColumn(name="lng", referencedColumnName="lng")
    } )
    private GeoLocation geoLocation;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) // TODO: this persist transient instances
    @JoinTable(name = "invites", joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "user_id"}))
    private List<User> invites  = new ArrayList<>();
    @Column()
    private boolean inviteOnly = false;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "activity_rating", joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "user_id"}))
    private List<User> likes = new ArrayList<>();

    private void removeHostsFromActivity() {
        if(hosts != null)
            hosts.clear();
    }

    private void removeInvitesFromActivity() {
      if (invites != null)
        invites.clear();
    }

  private void clearHost() {
    if (hosts != null) {
      hosts.clear();
    }
  }
    private void removeLikesFromActivity() {
        if(likes != null)
            likes.clear();
    }
  private void clearComments() {
    if (comments != null) {
      comments.clear();
    }
  }
  private void clearCreator(){
      if(creator != null)
        creator = null;
  }

  @PreRemove
    private void removeRelationshipsFromActivity() {
        removeHostsFromActivity();
        removeInvitesFromActivity();
        removeLikesFromActivity();
        clearComments();
        clearHost();
        clearCreator();

    }

    @Transient
    public int getLikesCount(){
        return likes.size();
    }

    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime  startDateBefore;


  @Transient
  @QueryType(PropertyType.DATETIME)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private ZonedDateTime startDateAfter;


  @Transient
  @QueryType(PropertyType.STRING)
  private String search;
}
