package com.ntnu.gidd.dto.Activity;


import com.ntnu.gidd.dto.ActivityImageDto;
import com.ntnu.gidd.dto.User.UserListDto;
import com.ntnu.gidd.model.Equipment;
import com.ntnu.gidd.dto.geolocation.GeoLocationDto;
import com.ntnu.gidd.util.TrainingLevelEnum;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityListDto {
    private UUID id;
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean closed;
    private TrainingLevelEnum level;
    @NotNull
    private boolean inviteOnly;
    private List<ActivityImageDto> images;
    private GeoLocationDto geoLocation;
    private int likesCount;
    private boolean hasLiked;
}
