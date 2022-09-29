package com.ntnu.gidd.service.rating;

import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.UUIDModel;
import com.ntnu.gidd.model.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ActivityLikeService {
    boolean hasLiked(String email, UUID ActivityId);
    boolean addLike(String email, UUID ActivityId);
    boolean removeLike(String email, UUID ActivityId);
    Page<ActivityListDto> checkListLikes(Page<ActivityListDto> activities, String email);
    Page<ActivityListDto> checkListLikes(Page<ActivityListDto> activities, UUID id);
    boolean hasLiked(UUID userId, UUID ActivityId);

}
