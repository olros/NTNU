package com.ntnu.gidd.model;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Registration is a class for the registration of a user to a activity
 * The unique identifier is a composite key RegistrationId defined by the unique id of the user and activity
 * The class maps the to the user and activity with many to one relations
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor()
@EqualsAndHashCode
@Table(name="registration")
public class Registration extends TimeStampedModel{


    public Registration(RegistrationId id, User user, Activity activity){
        this.registrationId = id;
        this.user = user;
        this.activity = activity;

    }
    @EmbeddedId
    private RegistrationId registrationId;

    @MapsId("user_id")
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    User user;

    @MapsId("activity_id")
    @ManyToOne
    @JoinColumn(name="activity_id", referencedColumnName = "id")
    Activity activity;


    @PreRemove
    public void removeRelationship(){
        activity = null;
        user = null;
        registrationId = null;
    }
    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime registrationStartDateBefore;

    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime  registrationStartDateAfter;
}
