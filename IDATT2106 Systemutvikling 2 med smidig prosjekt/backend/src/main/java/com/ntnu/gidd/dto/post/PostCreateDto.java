package com.ntnu.gidd.dto.post;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateDto {
    private String content;
    private UUID activityId;
    private String image;

}
