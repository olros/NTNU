package com.ntnu.gidd.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

/**
 * RegistrationId is a class defining the id of a registration
 * The unique id is a composite key combining the userId and activityId
 */

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RegistrationId implements Serializable {

  @Column(name = "user_id", columnDefinition = "CHAR(32)")
  private UUID userId;

  @Column(name = "activity_id", columnDefinition = "CHAR(32)")
  private UUID activityId;
}

