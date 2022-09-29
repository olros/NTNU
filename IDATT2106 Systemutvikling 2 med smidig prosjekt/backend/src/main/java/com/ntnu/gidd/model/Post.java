package com.ntnu.gidd.model;


import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "post")
public class Post  extends UUIDModel{

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id", columnDefinition = "CHAR(32)")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @Column(columnDefinition = "TEXT")
    private String content;


    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_rating", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
    private List<User> likes = new ArrayList<>();

    @Transient

    public int getLikesCount(){
        return likes.size();
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private List<Comment> comments = new ArrayList<>();

    @Transient
    public int getCommentsCount(){
        return comments.size();
    }

    @PreRemove
    public void removeRelationships(){
        clearComments();
        clearActivity();
        clearCreator();
    }

    public void clearComments(){
        if(comments != null)
            comments.clear();
    }
    public void clearCreator(){
        creator = null;
    }
    public void clearActivity(){
        activity = null;
    }


}
