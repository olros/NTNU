package com.ntnu.gidd.dto.post;


import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.User.UserListDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private UUID id;
    private UserListDto creator;
    private String image;
    private String content;
    private int likesCount;
    private boolean hasLiked;
    private ActivityListDto activity;
    private int commentsCount;
    @NotNull
    private ZonedDateTime createdAt;
}
