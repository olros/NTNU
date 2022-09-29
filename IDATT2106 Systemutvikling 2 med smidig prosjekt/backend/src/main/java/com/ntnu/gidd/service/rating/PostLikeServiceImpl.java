package com.ntnu.gidd.service.rating;

import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.exception.PostNotFoundExecption;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of post like service
 */
@AllArgsConstructor
@Service
public class PostLikeServiceImpl implements PostLikeService {

    UserRepository userRepository;

    PostRepository postRepository;

    /**
     * Method to check if a user has liked a post
     * @param email
     * @param postId
     * @return true/false
     */
    @Override
    public boolean hasLiked(String email, UUID postId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findByEmail(email).orElse(null);
        if(user != null && post !=null){
            return post.getLikes().contains(user);
        }
        return false;
    }

    /**
     * Method to add a like to a post by a user
     * @param email
     * @param postId
     * @return
     */
    @Override
    public boolean addLike(String email, UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if(user != null && post !=null){
            if(post.getLikes().contains(user))return true;
            List<User> likes = post.getLikes();
            likes.add(user);
            post.setLikes(likes);
            postRepository.save(post);
            return true;
        }
        return false;
    }

    /**
     * Method to remove a like by a user on a post
     * @param email
     * @param postId
     * @return true/false
     */
    @Override
    public boolean removeLike(String email, UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if(user != null && post !=null){
            List<User> likes = post.getLikes();
            likes.remove(user);
            post.setLikes(likes);
            postRepository.save(post);
            return false;
        }
        return true;
    }

    /**
     * Method to check if a user has liked a list of posts by email
     * @param posts
     * @param email
     * @return updated list of posts DTOS
     */
    @Override
    public Page<PostDto> checkListLikes(Page<PostDto> posts, String email) {
        posts.forEach(s -> s.setHasLiked(hasLiked(email, s.getId())));
        return posts;
    }
    /**
     * Method to check if a user has liked a list of posts by user Id
     * @param posts
     * @param id
     * @return updated list of posts DTOS
     */
    @Override
    public Page<PostDto> checkListLikes(Page<PostDto> posts, UUID id) {
        posts.forEach(s -> s.setHasLiked(hasLiked(id, s.getId())));
        return posts;
    }

    /**
     * Method to check if a user has liked a given post
     * @param userId
     * @param postId
     * @return true/false
     */
    @Override
    public boolean hasLiked(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if(user != null && post !=null){
            return post.getLikes().contains(user);
        }
        return false;
    }
}
