package com.ntnu.gidd.service.rating;

import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.post.PostDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PostLikeService {

    boolean hasLiked(String email, UUID postId);
    boolean addLike(String email, UUID postId);
    boolean removeLike(String email, UUID postId);
    Page<PostDto> checkListLikes(Page<PostDto> posts, String email);
    Page<PostDto> checkListLikes(Page<PostDto> posts, UUID id);
    boolean hasLiked(UUID userId, UUID postId);
}

