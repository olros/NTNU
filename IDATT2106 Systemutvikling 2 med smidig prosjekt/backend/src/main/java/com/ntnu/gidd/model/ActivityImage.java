package com.ntnu.gidd.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActivityImage  extends UUIDModel{
    @Column(name = "activity_id", columnDefinition = "CHAR(32)")
    private UUID activityId;
    private String url;
}
